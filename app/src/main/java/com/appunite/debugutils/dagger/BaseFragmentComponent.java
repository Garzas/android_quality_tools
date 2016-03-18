package com.appunite.debugutils.dagger;

import dagger.Component;

@ActivityScope
@Component(
        modules = {
                FragmentModule.class
        }
)
public interface BaseFragmentComponent {

}
