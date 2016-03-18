package com.appunite.debugutils;

import com.appunite.debugutils.models.MovieDetails;
import com.appunite.debugutils.models.MoviesRequest;
import com.appunite.debugutils.models.SearchResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface OmdbService {

    @GET("/?r=json")
    Observable<SearchResponse> movieList(
            @Query("s") String search,
            @Query("type") String type,
            @Query("y") Integer year
    );

    @GET("/?r=json")
    Observable<SearchResponse> downloadMovies(
            @Query("s") String name,
            @Query("y") Integer year
    );


    @GET("/?r=json&plot=full")
    Observable<MovieDetails> getMovie(
            @Query("i") String id
    );


}
