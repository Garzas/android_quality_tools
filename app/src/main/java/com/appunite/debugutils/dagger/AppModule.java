package com.appunite.debugutils.dagger;

import android.app.Application;
import android.content.Context;

import com.appunite.debugutils.App;
import com.appunite.debugutils.BuildConfig;
import com.appunite.debugutils.OmdbDao;
import com.appunite.debugutils.OmdbService;
import com.appunite.rx.dagger.NetworkScheduler;
import com.appunite.rx.dagger.UiScheduler;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;


@Module
public final class AppModule {

    private static final String TAG = AppModule.class.getCanonicalName();

    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    @ForApplication
    public Context activityContext() {
        return app.getApplicationContext();
    }


    @Provides
    @Singleton
    Picasso providePicasso(@ForApplication Context context, OkHttpClient okHttpClient) {
        return new Picasso.Builder(context)
                .indicatorsEnabled(BuildConfig.DEBUG)
                .loggingEnabled(BuildConfig.DEBUG)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(@ForApplication final Context context) {
        return new OkHttpClient.Builder().build();
    }


    @Provides
    @Singleton
    OmdbService provideRestAdapterBuilder() {

        return new Retrofit.Builder()
                .baseUrl("http://www.omdbapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(OmdbService.class);
    }

    @Provides
    @Singleton
    OmdbDao provideOmdbDao(OmdbService service,
                               @NetworkScheduler Scheduler networkScheduler,
                               @UiScheduler Scheduler uiScheduler
                                 ) {
        return new OmdbDao(service, networkScheduler, uiScheduler);
    }

    @Provides
    @Singleton
    File provideCacheDirectory(@ForApplication Context context) {
        return context.getCacheDir();
    }

    @Provides
    @Singleton
    Locale provideLocale() {
        return Locale.getDefault();
    }


}
