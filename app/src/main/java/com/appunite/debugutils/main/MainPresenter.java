package com.appunite.debugutils.main;

import android.view.View;

import com.appunite.debugutils.OmdbDao;
import com.appunite.debugutils.models.MovieShortInfo;
import com.appunite.debugutils.models.MoviesRequest;
import com.appunite.debugutils.models.SearchResponse;
import com.appunite.detector.SimpleDetector;
import com.appunite.rx.ObservableExtensions;
import com.appunite.rx.functions.BothParams;
import com.appunite.rx.functions.Functions1;
import com.appunite.rx.operators.MoreOperators;
import com.appunite.rx.operators.OperatorSampleWithLastWithObservable;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.observers.Observers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class MainPresenter {

    @Nonnull
    private final Observable<BothParams<String, Boolean>> toolbarSearchObservable;
    @Nonnull
    private final PublishSubject<String> titleSubject = PublishSubject.create();
    @Nonnull
    private final Observable<MoviesRequest> moviesRequestObservable;
    @Nonnull
    private final Observable<Boolean> progressObservable;
    @Nonnull
    private final PublishSubject<String> openDetailsSubject = PublishSubject.create();
    @Nonnull
    private final Observable<List<BaseItem>> observableList;
    @Nonnull
    private final Observable<List<MovieItem>> movieList;
    @Nonnull
    private final BehaviorSubject<String> typeSubject = BehaviorSubject.create();
    @Nonnull
    private final BehaviorSubject<Integer> yearSubject = BehaviorSubject.create();
    @Nonnull
    private final PublishSubject<Integer> searchVisibilitySubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<Integer> optionsVisibilitySubject = PublishSubject.create();
    @Nonnull
    private final Observable<Boolean> showOptionsObservable;


    public abstract static class BaseItem implements SimpleDetector.Detectable<BaseItem> {
    }

    public class MovieItem extends BaseItem {

        @Nonnull
        private final String omdbId;
        @Nonnull
        private final String title;
        @Nonnull
        private final String type;
        @Nonnull
        private final String year;
        @Nonnull
        private final String poster;

        public MovieItem(@Nonnull String omdbId, @Nonnull String title, @Nonnull String type,
                         @Nonnull String year, @Nonnull String poster) {
            this.omdbId = omdbId;
            this.title = title;
            this.type = type;
            this.year = year;
            this.poster = poster;
        }

        @Nonnull
        public String getOmdbId() {
            return omdbId;
        }

        @Nonnull
        public String getTitle() {
            return title;
        }

        @Nonnull
        public String getType() {
            return type;
        }

        @Nonnull
        public String getYear() {
            return year;
        }

        @Nonnull
        public String getPoster() {
            return poster;
        }

        @Override
        public boolean matches(@Nonnull BaseItem item) {
            return false;
        }

        @Override
        public boolean same(@Nonnull BaseItem item) {
            return false;
        }

        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    openDetailsSubject.onNext(omdbId);
                }
            });
        }

    }

    @Inject
    public MainPresenter(@Nonnull final OmdbDao omdbDao) {

        final Observable<Boolean> isSearchVisible = searchVisibilitySubject
                .map(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer == View.VISIBLE;
                    }
                });

        final Observable<Object> startSearch = isSearchVisible
                .skip(1)
                .filter(Functions1.isFalse())
                .map(Functions1.toObject());

        moviesRequestObservable = Observable.combineLatest(
                titleSubject,
                typeSubject.startWith((String) null),
                yearSubject.startWith((Integer) null),
                new Func3<String, String, Integer, MoviesRequest>() {
                    @Override
                    public MoviesRequest call(String title, String type, Integer year) {
                        return new MoviesRequest(title, type, null);
                    }
                }
        )
                .lift(OperatorSampleWithLastWithObservable.<MoviesRequest>create(startSearch))
                .compose(ObservableExtensions.<MoviesRequest>behaviorRefCount());

        moviesRequestObservable.subscribe(omdbDao.moviesRequestObserver());

        movieList = omdbDao.omdbMoviesResponseObservable()
                .map(new Func1<SearchResponse, List<MovieItem>>() {
                    @Override
                    public List<MovieItem> call(SearchResponse search) {
                        return ImmutableList.copyOf(Iterables.transform(search.getMoviesList(),
                                new Function<MovieShortInfo, MovieItem>() {
                                    @Nullable
                                    @Override
                                    public MovieItem apply(MovieShortInfo search) {
                                        return new MovieItem(
                                                search.getImdbID(),
                                                search.getTitle(),
                                                search.getType(),
                                                search.getYear(),
                                                search.getPoster()
                                        );
                                    }
                                }));
                    }
                });

        observableList = movieList
                .startWith(ImmutableList.<MovieItem>builder().build())
                .map(new Func1<List<MovieItem>, List<BaseItem>>() {
                    @Override
                    public List<BaseItem> call(List<MovieItem> movieItems) {
                        return ImmutableList.<BaseItem>builder()
                                .addAll(movieItems)
                                .build();
                    }
                });


        showOptionsObservable = optionsVisibilitySubject
                .map(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer visibility) {
                        switch (visibility) {
                            case View.VISIBLE:
                                return false;
                            default:
                                return true;
                        }
                    }
                })
                .mergeWith(startSearch.map(Functions1.returnFalse()));

        toolbarSearchObservable = Observable.combineLatest(
                titleSubject.startWith("Debug Utils"),
                isSearchVisible.map(Functions1.neg()),
                new Func2<String, Boolean, BothParams<String, Boolean>>() {
                    @Override
                    public BothParams<String, Boolean> call(String s, Boolean aBoolean) {
                        return new BothParams<>(s, aBoolean);
                    }
                }
        );

        progressObservable = Observable.merge(
                moviesRequestObservable.map(Functions1.returnTrue()),
                observableList.map(Functions1.returnFalse()));

    }

    @Nonnull
    public Observable<List<BaseItem>> getObservableList() {
        return observableList;
    }

    @Nonnull
    public Observable<Object> hideKeyboardObservable() {
        return moviesRequestObservable.map(Functions1.toObject());
    }

    @Nonnull
    public Observable<String> openDetailsObservable() {
        return openDetailsSubject;
    }

    @Nonnull
    public Observable<Boolean> showProgressObservable() {
        return progressObservable;
    }

    @Nonnull
    public Observable<List<MovieItem>> getMovieListObservable() {
        return movieList;
    }

    @Nonnull
    public Observer<String> titleObserver() {
        return titleSubject;
    }

    @Nonnull
    public Observable<BothParams<String, Boolean>> getToolbarSearchObservable() {
        return toolbarSearchObservable;
    }

    @Nonnull
    public Observer<Integer> searchViewVisibilityObserver() {
        return searchVisibilitySubject;
    }

    @Nonnull
    public Observable<Boolean> getShowOptionsObservable() {
        return showOptionsObservable;
    }

    @Nonnull
    public Observer<Integer> optionsViewVisiblityObserver() {
        return optionsVisibilitySubject;
    }

    @Nonnull
    public Observer<String> movieTypeObserver() {
        return typeSubject;
    }

    @Nonnull Observable<String> movieTypeObservable() {
        return typeSubject;
    }

    @Nonnull
    public Observer<Integer> movieYearObserver() {
        return yearSubject;
    }


}
