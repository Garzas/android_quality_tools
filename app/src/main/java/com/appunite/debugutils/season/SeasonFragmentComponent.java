package com.appunite.debugutils.season;

import com.appunite.debugutils.dagger.BaseFragmentComponent;
import com.appunite.debugutils.dagger.FragmentModule;
import com.appunite.debugutils.dagger.FragmentScope;

import dagger.Component;


@FragmentScope
@Component(
        dependencies = SeasonActivityComponent.class,
        modules = {
                FragmentModule.class,
        }
)
public interface SeasonFragmentComponent extends BaseFragmentComponent {

    void inject(SeasonFragment fragment);
}