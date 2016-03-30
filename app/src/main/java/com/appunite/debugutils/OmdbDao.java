package com.appunite.debugutils;

import com.appunite.debugutils.models.MovieDetails;
import com.appunite.debugutils.models.MoviesRequest;
import com.appunite.debugutils.models.SearchResponse;
import com.appunite.rx.ObservableExtensions;
import com.appunite.rx.ResponseOrError;
import com.appunite.rx.dagger.NetworkScheduler;
import com.appunite.rx.dagger.UiScheduler;
import com.appunite.rx.operators.MoreOperators;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class OmdbDao {

    private final Scheduler mNetworkScheduler;
    private final OmdbService mService;

    @Nonnull
    private final BehaviorSubject<ResponseOrError<SearchResponse>> movieListResponseSubject = BehaviorSubject.create();
    @Nonnull
    private final BehaviorSubject<ResponseOrError<MovieDetails>> movieResponseSubject = BehaviorSubject.create();
    @Nonnull
    private final PublishSubject<Object> refreshSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<MoviesRequest> moviesRequestSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> movieRequestSubject = PublishSubject.create();

    @Inject
    public OmdbDao(final OmdbService service,
                   @NetworkScheduler final Scheduler networkScheduler,
                   @UiScheduler final Scheduler uiScheduler) {
        this.mNetworkScheduler = networkScheduler;
        this.mService = service;

        moviesRequestSubject
                .flatMap(new Func1<MoviesRequest, Observable<ResponseOrError<SearchResponse>>>() {
                    @Override
                    public Observable<ResponseOrError<SearchResponse>> call(MoviesRequest moviesRequest) {
                        return service.movieList(
                                moviesRequest.getTitle(),
                                moviesRequest.getType(),
                                moviesRequest.getYear())
                                .compose(ResponseOrError.<SearchResponse>toResponseOrErrorObservable())
                                .compose(MoreOperators.<SearchResponse>repeatOnError(networkScheduler))
                                .compose(ObservableExtensions.<ResponseOrError<SearchResponse>>behaviorRefCount())
                                .subscribeOn(networkScheduler)
                                .observeOn(uiScheduler);
                    }
                })
                .subscribe(movieListResponseSubject);

        movieRequestSubject
                .flatMap(new Func1<String, Observable<ResponseOrError<MovieDetails>>>() {
                    @Override
                    public Observable<ResponseOrError<MovieDetails>> call(String s) {
                        return service.getMovie(s)
                                .compose(ResponseOrError.<MovieDetails>toResponseOrErrorObservable())
                                .compose(MoreOperators.<MovieDetails>repeatOnError(networkScheduler))
                                .compose(ObservableExtensions.<ResponseOrError<MovieDetails>>behaviorRefCount())
                                .subscribeOn(networkScheduler)
                                .observeOn(uiScheduler);
                    }
                })
                .subscribe(movieResponseSubject);
    }

    @Nonnull
    public Observer<Object> refreshObserver() {
        return refreshSubject;
    }

    @Nonnull
    public Observable<SearchResponse> omdbMoviesResponseObservable() {
        return movieListResponseSubject
                .compose(ResponseOrError.<SearchResponse>onlySuccess());
    }

    @Nonnull
    public Observable<Throwable> omdbMoviesErrorObservable() {
        return movieListResponseSubject
                .compose(ResponseOrError.<SearchResponse>onlyError());
    }

    @Nonnull
    public Observer<MoviesRequest> moviesRequestObserver() {
        return moviesRequestSubject;
    }

    @Nonnull
    public Observer<String> getMovieObserver() {
        return movieRequestSubject;
    }

    @Nonnull
    public Observable<ResponseOrError<MovieDetails>> movieResponseObservable() {
        return movieResponseSubject;
    }
}
