package com.appunite.debugutils.season;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appunite.debugutils.BaseFragment;
import com.appunite.debugutils.R;
import com.appunite.debugutils.dagger.BaseActivityComponent;
import com.appunite.debugutils.dagger.FragmentModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SeasonFragment extends BaseFragment {


    private String id;
    public static SeasonFragment newInstance(String id) {
        return new SeasonFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.season_fragment, container, false);
    }


    @Override
    public void onFragmentViewCreated(View view, Bundle savedInstanceState) {

    }


    @Override
    protected void injectComponent(@Nonnull BaseActivityComponent baseActivityComponent, @Nonnull FragmentModule fragmentModule, @Nullable Bundle savedInstanceState) {
        DaggerSeasonFragmentComponent
                .builder()
                .fragmentModule(fragmentModule)
                .build()
                .inject(this);
    }

}


