package com.appunite.debugutils.season;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appunite.debugutils.BaseFragment;
import com.appunite.debugutils.R;
import com.appunite.debugutils.dagger.BaseActivityComponent;
import com.appunite.debugutils.dagger.FragmentModule;
import com.appunite.debugutils.models.Season;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.InjectView;
import rx.Observable;

public class SeasonFragment extends BaseFragment {

    private static final String SEASON_NUMBER = "season_number";

    private int season;

    @InjectView(R.id.season_recyclerview)
    RecyclerView recyclerView;

    @Inject
    SeasonPresenter seasonPresenter;

    EpisodeAdapter episodeAdapter;

    public static SeasonFragment newInstance(int seasonNumber) {
        Bundle bundle = new Bundle();
        bundle.putInt(SEASON_NUMBER, seasonNumber);
        SeasonFragment fragment = new SeasonFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        season = bundle.getInt(SEASON_NUMBER);
        return inflater.inflate(R.layout.season_fragment, container, false);
    }


    @Override
    public void onFragmentViewCreated(View view, Bundle savedInstanceState) {
        episodeAdapter = new EpisodeAdapter();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(episodeAdapter);

        Observable.just(season)
                .compose(this.<Integer>bindToLifecycle())
                .subscribe(seasonPresenter.seasonObserver());

        seasonPresenter.getEpisodesObservable()
                .compose(this.<List<SeasonPresenter.EpisodeItem>>bindToLifecycle())
                .subscribe(episodeAdapter);
    }


    @Override
    protected void injectComponent(@Nonnull BaseActivityComponent baseActivityComponent,
                                   @Nonnull FragmentModule fragmentModule,
                                   @Nullable Bundle savedInstanceState) {
        DaggerSeasonFragmentComponent
                .builder()
                .seasonActivityComponent(((SeasonActivityComponent) baseActivityComponent))
                .fragmentModule(fragmentModule)
                .build()
                .inject(this);
    }

}


