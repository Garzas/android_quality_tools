package com.appunite.debugutils;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;


import com.appunite.debugutils.dagger.BaseActivityComponent;
import com.appunite.debugutils.dagger.BaseActivityComponentProvider;
import com.appunite.debugutils.debug.DebugDrawerHelper;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import javax.annotation.Nonnull;


public abstract class BaseActivity extends RxAppCompatActivity implements BaseActivityComponentProvider {

    private BaseActivityComponent activityComponent;
    private DebugDrawerHelper debugDrawerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityComponent = createActivityComponent(savedInstanceState);
        super.onCreate(savedInstanceState);
        debugDrawerHelper = new DebugDrawerHelper(this);

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(debugDrawerHelper.setContentView(layoutResID));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(debugDrawerHelper.setContentView(view));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(debugDrawerHelper.setContentView(view), params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        debugDrawerHelper.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        debugDrawerHelper.onResume();
    }

    @Nonnull
    public BaseActivityComponent getActivityComponent() {
        return activityComponent;
    }
}
