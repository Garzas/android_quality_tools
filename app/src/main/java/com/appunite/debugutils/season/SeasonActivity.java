package com.appunite.debugutils.season;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.appunite.debugutils.App;
import com.appunite.debugutils.BaseActivity;
import com.appunite.debugutils.OmdbService;
import com.appunite.debugutils.R;
import com.appunite.debugutils.dagger.ActivityModule;
import com.appunite.debugutils.dagger.BaseActivityComponent;
import com.appunite.debugutils.models.Season;
import com.appunite.rx.android.util.LogTransformer;
import com.google.common.collect.Maps;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.functions.Action1;

public class SeasonActivity extends BaseActivity {

    private static final String SERIES_ID = "series_id";

    @InjectView(R.id.season_pager)
    ViewPager viewPager;
    @InjectView(R.id.season_tab_layout)
    TabLayout tabLayout;

    SeasonAdapter adapter;

    @Inject
    OmdbService service;

    @Inject
    SeasonPresenter presenter;

    public static Intent newIntent(Context context, String id) {
        Intent intent = new Intent(context, SeasonActivity.class);
        intent.putExtra(SERIES_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.season_layout);
        SeasonActivityComponent component = (SeasonActivityComponent) getActivityComponent();
        component.inject(this);
        ButterKnife.inject(this);

        final String seriesId = getIntent().getStringExtra(SERIES_ID);

        adapter = new SeasonAdapter(getSupportFragmentManager(), seriesId, service);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        Observable.just(seriesId)
                .compose(this.<String>bindToLifecycle())
                .subscribe(presenter.seriesIdObserver());

        presenter.getUpdateSeasonAdapter()
                .compose(this.<Season>bindToLifecycle())
                .subscribe(new Action1<Season>() {
                    @Override
                    public void call(Season season) {
                        adapter.call(season);
                        viewPager.setAdapter(adapter);
                        tabLayout.setupWithViewPager(viewPager);
                    }
                });
    }

    @Nonnull
    @Override
    public BaseActivityComponent createActivityComponent(@Nullable Bundle savedInstanceState) {
        return DaggerSeasonActivityComponent
                .builder()
                .activityModule(new ActivityModule(this))
                .appComponent(App.getAppComponent(getApplication()))
                .build();
    }
}

