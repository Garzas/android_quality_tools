package com.appunite.debugutils.main;

import com.appunite.debugutils.OmdbDao;
import com.appunite.debugutils.OmdbService;
import com.appunite.debugutils.models.MovieShortInfo;
import com.appunite.debugutils.models.SearchResponse;
import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    private MainPresenter mainPresenter;

    @Mock
    OmdbDao omdbDao;

    @Mock
    OmdbService omdbService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mainPresenter = new MainPresenter(omdbDao);

        when(omdbService.downloadMovies(anyString(), anyInt())).thenReturn(Observable.just(new SearchResponse(ImmutableList.of(
                new MovieShortInfo("title", "id", "type", "year", "poster"),
                new MovieShortInfo("title2", "id2", "type", "year", "poster")
        ))));
    }

}
