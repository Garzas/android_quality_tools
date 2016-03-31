package com.appunite.debugutils.season;

import com.appunite.debugutils.OmdbService;
import com.appunite.debugutils.models.Season;
import com.appunite.rx.ObservableExtensions;
import com.appunite.rx.android.util.LogTransformer;
import com.appunite.rx.functions.BothParams;
import com.appunite.rx.functions.Functions2;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class SeasonPresenter {

    @Inject
    OmdbService service;

    @Nonnull
    private final PublishSubject<String> seriesIdSubject = PublishSubject.create();
    @Nonnull
    private final Observable<Season> seasonObservable;

    @Inject
    public SeasonPresenter() {

        final PublishSubject<Integer> seasonNumberSubject = PublishSubject.create();

        seasonObservable = Observable.combineLatest(
                seriesIdSubject,
                seasonNumberSubject.startWith(1),
                Functions2.<String, Integer>bothParams())
                .flatMap(new Func1<BothParams<String, Integer>, Observable<Season>>() {
                    @Override
                    public Observable<Season> call(BothParams<String, Integer> seriesIdAndSeasonNumber) {
                        final String seriesId = seriesIdAndSeasonNumber.param1();
                        final Integer seasonNumber = seriesIdAndSeasonNumber.param2();
                        return service.getSeason(seriesId, seasonNumber)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .compose(LogTransformer.<Season>transformer("dupa", "dupa"))
                .filter(new Func1<Season, Boolean>() {
                    @Override
                    public Boolean call(Season season) {
                        return season.hasResponse();
                    }
                })
                .compose(ObservableExtensions.<Season>behaviorRefCount());


        seasonObservable
                .map(new Func1<Season, Integer>() {
                    @Override
                    public Integer call(Season season) {
                        return season.getSeason() + 1;
                    }
                })
                .subscribe(seasonNumberSubject);
    }

    @Nonnull
    public Observer<String> seriesIdObserver() {
        return seriesIdSubject;
    }

    @Nonnull
    public Observable<Season> getUpdateSeasonAdapter() {
        return seasonObservable;
    }

}
