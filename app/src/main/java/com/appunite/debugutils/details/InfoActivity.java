package com.appunite.debugutils.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appunite.debugutils.App;
import com.appunite.debugutils.BaseActivity;
import com.appunite.debugutils.R;
import com.appunite.debugutils.dagger.ActivityModule;
import com.appunite.debugutils.dagger.BaseActivityComponent;
import com.appunite.debugutils.models.MovieDetails;
import com.appunite.debugutils.season.SeasonActivity;
import com.google.common.base.Strings;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.functions.Action1;

public class InfoActivity extends BaseActivity {

    private static final String OMDB_ID = "omdb_id";

    private String omdbId;

    @InjectView(R.id.movie_release_date)
    TextView releaseDate;
    @InjectView(R.id.movie_genre)
    TextView genre;
    @InjectView(R.id.movie_rated)
    TextView rated;
    @InjectView(R.id.movie_duration)
    TextView duration;
    @InjectView(R.id.movie_director)
    TextView director;
    @InjectView(R.id.movie_writer)
    TextView writer;
    @InjectView(R.id.movie_actors)
    TextView actors;
    @InjectView(R.id.movie_plot)
    TextView plot;
    @InjectView(R.id.movie_language)
    TextView language;
    @InjectView(R.id.movie_awards)
    TextView awards;
    @InjectView(R.id.movie_type)
    TextView type;
    @InjectView(R.id.movie_imdb_raiting)
    TextView imdbRaiting;
    @InjectView(R.id.movie_metascore)
    TextView metascore;
    @InjectView(R.id.movie_poster_header)
    ImageView moviePoster;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @InjectView(R.id.seasons)
    TextView seasons;

    @Inject
    InfoPresenter infoPresenter;

    @Inject
    Picasso picasso;

    public static Intent newIntent(Context context, String id) {
        Intent intent = new Intent(context, InfoActivity.class);
        intent.putExtra(OMDB_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);
        InfoActivityComponent component = (InfoActivityComponent) getActivityComponent();
        component.inject(this);
        omdbId = getIntent().getStringExtra(OMDB_ID);
        ButterKnife.inject(this);

        Observable.just(omdbId)
                .compose(this.<String>bindToLifecycle())
                .subscribe(infoPresenter.omdbIdObserver());
        

        infoPresenter.getMovieDetailsObservable()
                .compose(this.<MovieDetails>bindToLifecycle())
                .subscribe(new Action1<MovieDetails>() {
                    @Override
                    public void call(MovieDetails movieDetails) {
                        releaseDate.setText(movieDetails.getReleased());
                        genre.setText(movieDetails.getGenre());
                        rated.setText(movieDetails.getRated());
                        duration.setText(movieDetails.getRuntime());
                        director.setText(movieDetails.getDirector());
                        writer.setText(movieDetails.getWriter());
                        actors.setText(movieDetails.getActors());
                        plot.setText(movieDetails.getPlot());
                        language.setText(movieDetails.getLanguage());
                        awards.setText(movieDetails.getAwards());
                        type.setText(movieDetails.getType());
                        imdbRaiting.setText(movieDetails.getImdbRating());
                        metascore.setText(movieDetails.getMetascore());
                        collapsingToolbarLayout.setTitle(movieDetails.getTitle());
                        collapsingToolbarLayout.setSelected(true);

                        if(movieDetails.getType().equals("series")) {
                            seasons.setVisibility(View.VISIBLE);
                        }

                        picasso
                                .load(Strings.emptyToNull(movieDetails.getPoster()))
                                .resizeDimen(R.dimen.example_avatar_size, R.dimen.example_avatar_size)
                                .centerInside()
                                .into(moviePoster);
                    }
                });

        RxView.clicks(seasons)
                .compose(this.bindToLifecycle())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        startActivity(SeasonActivity.newIntent(InfoActivity.this, omdbId));
                    }
                });
    }

    @Nonnull
    @Override
    public BaseActivityComponent createActivityComponent(@Nullable Bundle savedInstanceState) {
        return DaggerInfoActivityComponent
                .builder()
                .activityModule(new ActivityModule(this))
                .appComponent(App.getAppComponent(getApplication()))
                .build();
    }
}
