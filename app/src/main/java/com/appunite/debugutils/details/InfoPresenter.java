package com.appunite.debugutils.details;

import com.appunite.debugutils.OmdbDao;
import com.appunite.debugutils.models.MovieDetails;
import com.appunite.rx.ResponseOrError;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.subjects.PublishSubject;

public class InfoPresenter {

    private final PublishSubject<String> omdbIdSubject = PublishSubject.create();
    private final PublishSubject<MovieDetails> movieDetailsSubject = PublishSubject.create();

    @Inject
    public InfoPresenter(@Nonnull final OmdbDao omdbDao) {

        omdbIdSubject
                .subscribe(omdbDao.getMovieObserver());


        omdbDao.movieResponseObservable()
                .compose(ResponseOrError.<MovieDetails>onlySuccess())
                .subscribe(movieDetailsSubject);



    }


    @Nonnull
    public Observer<String> omdbIdObserver() {
        return omdbIdSubject;
    }

    @Nonnull
    public Observable<MovieDetails> getMovieDetailsObservable() {
        return movieDetailsSubject;
    }

}
