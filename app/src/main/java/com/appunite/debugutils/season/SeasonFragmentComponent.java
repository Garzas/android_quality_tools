package com.appunite.debugutils.season;

import com.appunite.debugutils.dagger.BaseFragmentComponent;
import com.appunite.debugutils.dagger.FragmentModule;
import com.appunite.debugutils.dagger.FragmentScope;
import com.appunite.debugutils.details.InfoActivity;

import dagger.Component;


@FragmentScope
@Component(
        dependencies = InfoActivity.class,
        modules = {
                FragmentModule.class,
        }
)
public interface SeasonFragmentComponent extends BaseFragmentComponent {

    void inject(SeasonFragment fragment);
}