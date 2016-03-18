package com.appunite.debugutils.main;

import com.appunite.debugutils.dagger.ActivityModule;
import com.appunite.debugutils.dagger.ActivityScope;
import com.appunite.debugutils.dagger.AppComponent;
import com.appunite.debugutils.dagger.BaseActivityComponent;

import dagger.Component;


    @ActivityScope
    @Component(
            dependencies = AppComponent.class,
            modules = ActivityModule.class
    )
    public interface MainActivityComponent extends BaseActivityComponent {

        void inject(MainActvity activity);

    }
