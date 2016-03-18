package com.appunite.debugutils.dagger;

import android.content.Context;

import com.appunite.debugutils.App;
import com.appunite.debugutils.OmdbService;
import com.appunite.rx.dagger.NetworkScheduler;
import com.appunite.rx.dagger.UiScheduler;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;
import rx.Scheduler;

@Singleton
@Component(
        modules = {
                AppModule.class,
                BaseModule.class,
        }
)
public interface AppComponent {

    void inject(App app);

    @UiScheduler
    Scheduler getUiScheduler();

    @NetworkScheduler
    Scheduler getNetworkScheduler();

    @ForApplication
    Context getContext();

    OmdbService getOmdbService();

    Picasso getPicasso();

}