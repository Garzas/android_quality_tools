package com.appunite.debugutils.season;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appunite.debugutils.OmdbService;
import com.appunite.debugutils.models.Season;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import rx.functions.Action1;

public class SeasonAdapter extends FragmentStatePagerAdapter implements Action1<Season> {

    @Nonnull
    private final FragmentManager fragmentManager;
    private final String seriesId;
    private OmdbService service;
    @Nonnull
    private List<Season> tabTitles = new ArrayList<>();


    public SeasonAdapter(@Nonnull FragmentManager fragmentManager, final String seriesId, final OmdbService service) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.seriesId = seriesId;
        this.service = service;

    }

    @Override
    public Fragment getItem(int position) {
        return SeasonFragment.newInstance(tabTitles.get(position));
    }

    @Override
    public int getCount() {
        return tabTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.format("Season %d", position + 1);
    }

    @Override
    public void call(Season seasons) {
        tabTitles.add(seasons);
        notifyDataSetChanged();
    }
}
