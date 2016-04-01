package com.appunite.debugutils.season;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appunite.debugutils.models.Season;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import rx.functions.Action1;

public class SeasonAdapter extends FragmentStatePagerAdapter implements Action1<List<Season>> {

    @Nonnull
    private List<Season> seasonList = new ArrayList<>();

    public SeasonAdapter(@Nonnull FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return SeasonFragment.newInstance(seasonList.get(position).getSeason());
    }

    @Override
    public int getCount() {
        return seasonList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.format("Season %d", position + 1);
    }

    @Override
    public void call(List<Season> seasons) {
        seasonList = ImmutableList.copyOf(seasons);
        notifyDataSetChanged();
    }
}
