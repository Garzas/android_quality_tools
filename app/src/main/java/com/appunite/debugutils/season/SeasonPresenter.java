package com.appunite.debugutils.season;

import com.appunite.debugutils.OmdbService;
import com.appunite.debugutils.models.Episode;
import com.appunite.debugutils.models.Season;
import com.appunite.detector.SimpleDetector;
import com.appunite.rx.ObservableExtensions;
import com.appunite.rx.functions.BothParams;
import com.appunite.rx.functions.Functions1;
import com.appunite.rx.functions.Functions2;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observers.Observers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class SeasonPresenter {

    @Nonnull
    private final Observable<Boolean> progressObservable;
    @Nonnull
    private final PublishSubject<String> seriesIdSubject = PublishSubject.create();
    @Nonnull
    private final Observable<List<Season>> seasonObservable;
    @Nonnull
    private final BehaviorSubject<Integer> seasonIndexSubject = BehaviorSubject.create();
    @Nonnull
    private final Observable<List<EpisodeItem>> episodesObservable;

    public SeasonPresenter(@Nonnull final OmdbService service) {

        final PublishSubject<Integer> nextSeasonNumberSubject = PublishSubject.create();

        seasonObservable = Observable.combineLatest(
                seriesIdSubject,
                nextSeasonNumberSubject.startWith(1),
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
                .filter(new Func1<Season, Boolean>() {
                    @Override
                    public Boolean call(Season season) {
                        return season.hasResponse();
                    }
                })
                .scan(Collections.<Season>emptyList(), new Func2<List<Season>, Season, List<Season>>() {
                    @Override
                    public List<Season> call(List<Season> seasonList, Season season) {
                        return ImmutableList.<Season>builder().addAll(seasonList).add(season).build();
                    }
                })
                .compose(ObservableExtensions.<List<Season>>behaviorRefCount());


        seasonObservable
                .scan(0, new Func2<Integer, List<Season>, Integer>() {
                    @Override
                    public Integer call(Integer integer, List<Season> seasonList) {
                        integer++;
                        return integer;
                    }
                })
                .subscribe(nextSeasonNumberSubject);

        episodesObservable = Observable.combineLatest(
                seasonIndexSubject,
                seasonObservable,
                new Func2<Integer, List<Season>, List<Episode>>() {
                    @Override
                    public List<Episode> call(Integer seasonNumber, List<Season> seasonList) {
                        return seasonList.get(seasonNumber).getEpisodes();
                    }
                }
        )
                .map(new Func1<List<Episode>, List<EpisodeItem>>() {
                    @Override
                    public List<EpisodeItem> call(List<Episode> episodes) {
                        return ImmutableList.copyOf(Iterables.transform(episodes, new Function<Episode, EpisodeItem>() {
                            @Nullable
                            @Override
                            public EpisodeItem apply(Episode episode) {
                                return new EpisodeItem(episode.getTitle(), episode.getImdbRating(), episode.getEpisode());
                            }
                        }));
                    }
                })
                .compose(ObservableExtensions.<List<EpisodeItem>>behaviorRefCount());


        progressObservable = Observable.merge(
                seriesIdSubject.map(Functions1.returnTrue()),
                seasonObservable.map(Functions1.returnFalse())
        );
    }

    @Nonnull
    public Observer<String> seriesIdObserver() {
        return seriesIdSubject;
    }

    @Nonnull
    public Observable<List<Season>> getUpdateSeasonAdapter() {
        return seasonObservable;
    }

    @Nonnull
    public Observable<Boolean> getProgressObservable() {
        return progressObservable;
    }

    public Observer<Integer> seasonObserver() {
        return seasonIndexSubject;
    }

    @Nonnull
    public Observable<List<EpisodeItem>> getEpisodesObservable() {
        return episodesObservable;
    }

    public class EpisodeItem implements SimpleDetector.Detectable<EpisodeItem> {

        @Nonnull
        private final String title;
        private final Double rating;
        private final int number;

        public EpisodeItem(@Nonnull String title, Double rating, int number) {
            this.title = title;
            this.rating = rating;
            this.number = number;
        }

        public Double getRating() {
            return rating;
        }

        public int getNumber() {
            return number;
        }

        @Nonnull
        public String getTitle() {
            return title;
        }

        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                }
            });
        }

        @Override
        public boolean matches(@Nonnull EpisodeItem episodeItem) {
            return false;
        }

        @Override
        public boolean same(@Nonnull EpisodeItem episodeItem) {
            return false;
        }
    }

}
