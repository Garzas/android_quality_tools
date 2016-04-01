package com.appunite.debugutils.season;

import com.appunite.debugutils.dagger.ActivityModule;
import com.appunite.debugutils.dagger.ActivityScope;
import com.appunite.debugutils.dagger.AppComponent;
import com.appunite.debugutils.dagger.BaseActivityComponent;

import dagger.Component;

@ActivityScope
@Component(

        dependencies = AppComponent.class,
        modules = {ActivityModule.class, SeasonActivityModule.class}
)
public interface SeasonActivityComponent extends BaseActivityComponent {

    void inject(SeasonActivity activity);

    SeasonPresenter seasonPresenter();
}