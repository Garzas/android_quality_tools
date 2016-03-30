package com.appunite.debugutils.season;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.appunite.debugutils.App;
import com.appunite.debugutils.BaseActivity;
import com.appunite.debugutils.R;
import com.appunite.debugutils.dagger.ActivityModule;
import com.appunite.debugutils.dagger.BaseActivityComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SeasonActivity extends BaseActivity {

    private static final String SERIES_ID = "series_id";
    private String seriesId;

    @InjectView(R.id.season_pager)
    ViewPager viewPager;
    @InjectView(R.id.season_tab_layout)
    TabLayout tabLayout;

    @Inject
    SeasonAdapter adapter;

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
        seriesId = getIntent().getStringExtra(SERIES_ID);
        ButterKnife.inject(this);

        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //TODO 2 razy uruchamiam newInstance z activity i z adaptera

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

