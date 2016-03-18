package com.appunite.debugutils.main;

import com.appunite.debugutils.OmdbDao;
import com.appunite.debugutils.models.MoviesRequest;
import com.appunite.debugutils.models.Search;
import com.appunite.debugutils.models.SearchResponse;
import com.appunite.detector.SimpleDetector;
import com.appunite.rx.ObservableExtensions;
import com.appunite.rx.ResponseOrError;
import com.appunite.rx.functions.BothParams;
import com.appunite.rx.functions.Functions1;
import com.appunite.rx.operators.OperatorSampleWithLastWithObservable;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.List;

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

    private final Observable<Boolean> showSearchObservable;
    private final Observable<BothParams<String,Boolean>> toolbarSearchObservable;

    public Observer<String> titleObserver() {
        return titleSubject;
    }

    public Observable<BothParams<String, Boolean>> getToolbarSearchObservable() {
        return toolbarSearchObservable;
    }

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


    public class OptionsItem extends BaseItem {

        private final List<String> typeList;

        public OptionsItem(List<String> typeList) {
            this.typeList = typeList;
        }

        public List<String> getTypeList() {
            return typeList;
        }

        @Override
        public boolean matches(@Nonnull BaseItem item) {
            return false;
        }

        @Override
        public boolean same(@Nonnull BaseItem item) {
            return false;
        }

        public Observer<String> clickObserver() {
            return Observers.create(new Action1<String>() {
                @Override
                public void call(String title) {
                }
            });
        }

    }

    @Nonnull
    private final PublishSubject<String> titleSubject = PublishSubject.create();
    @Nonnull
    private final Observable<MoviesRequest> moviesRequestObservable;
    @Nonnull
    private final Observable<Boolean> progressObservable;
    @Nonnull
    private final PublishSubject<String> openDetailsSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<Boolean> showOptionsSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<Object> showSearchSubject = PublishSubject.create();
    @Nonnull
    private final Observable<List<BaseItem>> observableList;
    @Nonnull
    private final Observable<List<MovieItem>> movieList;
    @Nonnull
    private final BehaviorSubject<String> searchSubject = BehaviorSubject.create();
    @Nonnull
    private final BehaviorSubject<String> typeSubject = BehaviorSubject.create();
    @Nonnull
    private final BehaviorSubject<Integer> yearSubject = BehaviorSubject.create();

    @Inject
    public MainPresenter(@Nonnull final OmdbDao omdbDao) {

        showSearchObservable = showSearchSubject
                .scan(false, new Func2<Boolean, Object, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean, Object o) {
                        return !aBoolean;
                    }
                });

        moviesRequestObservable = Observable.combineLatest(
                titleSubject,
                typeSubject.startWith((String) null),
                yearSubject.startWith((Integer) null),
                new Func3<String, String, Integer, MoviesRequest>() {
                    @Override
                    public MoviesRequest call(String title, String type, Integer year) {
                        return new MoviesRequest(title, type, year);
                    }
                }
        )
                .lift(OperatorSampleWithLastWithObservable.<MoviesRequest>create(showSearchObservable
                        .filter(Functions1.isFalse())
                        .map(Functions1.toObject())));

        moviesRequestObservable.subscribe(omdbDao.moviesRequestObservable());

        movieList = omdbDao.omdbMoviesResponseObservable()
                .compose(ResponseOrError.<SearchResponse>onlySuccess())
                .map(new Func1<SearchResponse, List<MovieItem>>() {
                    @Override
                    public List<MovieItem> call(SearchResponse search) {
                        return ImmutableList.copyOf(Iterables.transform(search.getMoviesList(), new Function<Search, MovieItem>() {
                            @Nullable
                            @Override
                            public MovieItem apply(Search search) {
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

            observableList = Observable.combineLatest(
                    movieList.startWith(ImmutableList.<MovieItem>builder().build()),
                    showOptionsSubject.startWith(false),
                    new Func2<List<MovieItem>, Boolean, List<BaseItem>>() {
                        @Override
                        public List<BaseItem> call(List<MovieItem> movieItems, Boolean showOptions) {
                            if (showOptions) {
                                return ImmutableList.<BaseItem>builder()
                                        .add(new OptionsItem(ImmutableList.of("any", "movie", "series", "episode")))
                                        .addAll(movieItems)
                                        .build();
                            } else {
                                return ImmutableList.<BaseItem>builder()
                                        .addAll(movieItems)
                                        .build();
                            }

                        }
                    }
            );


        toolbarSearchObservable = Observable.combineLatest(
                titleSubject.startWith("Debug Utils"),
                showSearchObservable,
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


    public Observable<List<BaseItem>> getObservableList() {
        return observableList;
    }

    public Observable<String> titleObservable() {
        return titleSubject;
    }

    public Observable<Object> hideKeyboardObservable() {
        return moviesRequestObservable.map(Functions1.toObject());
    }

    public Observable<String> openDetailsObservable() {
        return openDetailsSubject;
    }

    public Observable<Boolean> showProgressObservable() {
        return progressObservable;
    }

    public Observer<String> resumeEditTextObserver() {
        return searchSubject;
    }

    public Observer<Object> showSearchObserver() {
        return showSearchSubject;
    }

    public Observable<Boolean> getShowSearchObservable() {
        return showSearchObservable;
    }

    public Observable<List<MovieItem>> getMovieListObservable() {
        return movieList;
    }
}
