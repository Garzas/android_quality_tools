package com.appunite.debugutils.season;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class SeasonAdapter extends FragmentPagerAdapter {

    @Nonnull
    private List<String> tabTitles;

    @Inject
    public SeasonAdapter(@Nonnull FragmentManager fragmentManager) {
        super(fragmentManager);

        tabTitles = new ArrayList<>();
        tabTitles.add("first");
        tabTitles.add("second");

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
        return tabTitles.get(position);
    }

}
