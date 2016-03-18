package com.appunite.debugutils.details;


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
public interface InfoActivityComponent extends BaseActivityComponent {

    void inject(InfoActivity activity);
}