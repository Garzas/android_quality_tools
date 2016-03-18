package com.appunite.debugutils.debug;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.appunite.debugutils.R;
import com.codemonkeylabs.fpslibrary.TinyDancer;
import com.github.pedrovgs.lynx.LynxActivity;
import com.github.pedrovgs.lynx.LynxConfig;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.squareup.leakcanary.LeakCanary;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

@Singleton
public class DebugDrawerHelper {

    private Activity activity;
    private SerialSubscription subscription = new SerialSubscription();
    private static DelayInterceptor delayInterceptor;
    final DebugPresenter debugPresenter;
    DebugDrawerPreferences debugPreferences;

    public DebugDrawerHelper(Activity activity) {
        this.activity = activity;
        delayInterceptor = new DelayInterceptor();
        debugPreferences = new DebugDrawerPreferences(activity.getApplicationContext());
        debugPresenter = new DebugPresenter(activity);
        if(debugPreferences.getLeakCanaryState()){
            LeakCanary.install(activity.getApplication());
        }

        //TODO turn off leakcanary

    }

    @Nonnull
    public View setContentView(int childId) {
        final View child = activity.getLayoutInflater().inflate(childId, null);
        return setContentView(child);
    }

    @Nonnull
    public View setContentView(@Nonnull View child) {
        final View root = activity.getLayoutInflater().inflate(R.layout.debug_layout, null);
        final ViewGroup mainFrame = (ViewGroup) root.findViewById(R.id.main_frame);
        mainFrame.removeAllViews();
        mainFrame.addView(child);
        final ScalpelFrameLayout scalpelFrame= (ScalpelFrameLayout) mainFrame;
        final DebugAdapter debugAdapter = new DebugAdapter(debugPreferences);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);

        RecyclerView debugRecyclerView = (RecyclerView) root.findViewById(R.id.debug_drawer);
        debugRecyclerView.setBackgroundColor(Color.parseColor("#cc222222"));

        debugRecyclerView.setLayoutManager(layoutManager);
        debugRecyclerView.setAdapter(debugAdapter);

        final DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        subscription.set(Subscriptions.from(
                debugPresenter.simpleListObservable()
                        .subscribe(debugAdapter),

                Observable.just(activity.getResources().getDisplayMetrics().density * 160f)
                        .subscribe(debugPresenter.densityObserver()),

                Observable.just(metrics.widthPixels + "x" + metrics.heightPixels)
                        .subscribe(debugPresenter.resolutionObserver()),

                debugPresenter.getDelayObservable()
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                delayInterceptor.setDelay(integer);
                            }
                        }),

                debugPresenter.setScalpelObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                scalpelFrame.setLayerInteractionEnabled(aBoolean);
                            }
                        }),

                debugPresenter.setDrawViewsObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                scalpelFrame.setDrawViews(!aBoolean);
                            }
                        }),

                debugPresenter.setShowIdsObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                scalpelFrame.setDrawIds(aBoolean);
                            }
                        }),

                debugPresenter.getFpsLabelObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean isSet) {
                                if (isSet) {
                                    TinyDancer.create().show(activity.getApplicationContext());
                                } else {
                                    TinyDancer.create().show(activity.getApplicationContext());
                                    TinyDancer.hide(activity.getApplicationContext());
                                }
                            }
                        }),

                debugPresenter.showLogObservable()
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                LynxConfig lynxConfig = new LynxConfig();
                                lynxConfig.setMaxNumberOfTracesToShow(4000);
                                Intent lynxActivityIntent = LynxActivity.getIntent(activity, lynxConfig);
                                activity.startActivity(lynxActivityIntent);
                            }
                        }),

                debugPresenter.getLeakCanaryObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean isSet) {
                                debugPreferences.saveLeakCanaryState(isSet);
                                activity.recreate();
                                //TODO disable leakcannary for all activities
                            }
                        })


        ));
        return root;
    }

    public void onDestroy() {
        subscription.set(Subscriptions.empty());
    }

    public void onResume() {
            TinyDancer.create().show(activity.getApplicationContext());
            TinyDancer.hide(activity.getApplicationContext());
    }

    @Nonnull
    public static Interceptor getDelayInterceptor(Interceptor interceptor) {
//        if (BuildConfig.DEBUG || interceptor == null) {
            return delayInterceptor;
//        }
//        else {
//            return interceptor;
//        }

    }

}
