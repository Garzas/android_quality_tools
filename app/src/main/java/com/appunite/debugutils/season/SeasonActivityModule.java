package com.appunite.debugutils.season;


import com.appunite.debugutils.OmdbService;
import com.appunite.debugutils.dagger.ActivityScope;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SeasonActivityModule {

    @Provides
    @ActivityScope
    SeasonPresenter providePresenter(OmdbService service) {
        return new SeasonPresenter(service);
    }
}
