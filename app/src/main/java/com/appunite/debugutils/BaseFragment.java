package com.appunite.debugutils;

import android.os.Bundle;
import android.view.View;

import com.appunite.debugutils.dagger.BaseActivityComponent;
import com.appunite.debugutils.dagger.FragmentModule;
import com.trello.rxlifecycle.components.support.RxFragment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import butterknife.ButterKnife;

public abstract class BaseFragment extends RxFragment {

    protected abstract void injectComponent(@Nonnull BaseActivityComponent baseActivityComponent,
                                            @Nonnull FragmentModule fragmentModule,
                                            @Nullable Bundle savedInstanceState);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final BaseActivityComponent activityComponent = ((BaseActivity) getActivity())
                .getActivityComponent();
        injectComponent(activityComponent, new FragmentModule(this), savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }
}