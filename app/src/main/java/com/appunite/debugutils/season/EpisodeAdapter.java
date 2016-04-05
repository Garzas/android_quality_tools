package com.appunite.debugutils.season;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appunite.debugutils.R;
import com.appunite.detector.ChangesDetector;
import com.appunite.detector.SimpleDetector;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.functions.Action1;


public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeHolder>
        implements Action1<List<SeasonPresenter.EpisodeItem>>, ChangesDetector.ChangesAdapter {


    private List<SeasonPresenter.EpisodeItem> episodeList = new ArrayList<>();
    @Nonnull
    private final ChangesDetector<SeasonPresenter.EpisodeItem, SeasonPresenter.EpisodeItem> changesDetector;

    public EpisodeAdapter() {
        this.changesDetector = new ChangesDetector<>(new SimpleDetector<SeasonPresenter.EpisodeItem>());
    }

    @Override
    public void call(List<SeasonPresenter.EpisodeItem> episodeItems) {
        this.episodeList = episodeItems;
        changesDetector.newData(this, episodeList, false);
    }

    static class EpisodeHolder extends RecyclerView.ViewHolder {

        private final View view;
        private Subscription mSubscription;

        @InjectView(R.id.episode_title)
        TextView title;
        @InjectView(R.id.episode_number)
        TextView episodeNumber;
        @InjectView(R.id.episode_rating)
        TextView rating;


        public EpisodeHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.inject(this, itemView);
        }


        public void bind(@Nonnull SeasonPresenter.EpisodeItem item) {
            title.setText(item.getTitle());
            episodeNumber.setText(Integer.toString(item.getNumber()));
            rating.setText(item.getRating().toString());

        }

        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }

        public static EpisodeHolder create(ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new EpisodeHolder(inflater.inflate(R.layout.episode_item, parent, false));
        }

    }

    @Override
    public EpisodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return EpisodeHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(EpisodeHolder holder, int position) {
        holder.bind(episodeList.get(position));
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }
}
