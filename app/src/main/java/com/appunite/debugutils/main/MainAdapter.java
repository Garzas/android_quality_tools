package com.appunite.debugutils.main;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.appunite.debugutils.R;
import com.appunite.debugutils.dagger.ForActivity;
import com.appunite.debugutils.util.PaletteTransformation;
import com.appunite.detector.ChangesDetector;
import com.appunite.detector.SimpleDetector;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

abstract class BaseItemHolder extends RecyclerView.ViewHolder {

    public BaseItemHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(@Nonnull MainPresenter.BaseItem item);

    public abstract void recycle();
}

public class MainAdapter extends RecyclerView.Adapter<BaseItemHolder> implements
        Action1<List<MainPresenter.BaseItem>>, ChangesDetector.ChangesAdapter {

    private static final int TYPE_MOVIE = 1;

    @Nonnull
    private List<MainPresenter.BaseItem> baseItems = ImmutableList.of();
    @Nonnull
    private final ChangesDetector<MainPresenter.BaseItem, MainPresenter.BaseItem> changesDetector;
    private final Picasso mPicasso;
    private final Resources resources;

    @Inject
    public MainAdapter(Picasso picasso, @ForActivity @Nonnull Resources resources) {
        this.changesDetector =  new ChangesDetector<>(new SimpleDetector<MainPresenter.BaseItem>());
        this.mPicasso = picasso;
        this.resources = resources;
    }

    @Override
    public void call(List<MainPresenter.BaseItem> baseItems) {
        this.baseItems = baseItems;
        changesDetector.newData(this, baseItems, false);
    }

    static class MovieHolder extends BaseItemHolder {

        @InjectView(R.id.movie_title)
        TextView name;
        @InjectView(R.id.movie_poster)
        ImageView imageView;
        @InjectView(R.id.movie_year)
        TextView year;
        @InjectView(R.id.title_layout)
        View titleView;

        private final View itemView;
        private final Picasso mPicasso;
        private final Resources resources;
        private Subscription mSubscription;
        private int bodyColor;
        private int secondBodyColor;


        public MovieHolder(View itemView, Picasso mPicasso, Resources resources) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.mPicasso = mPicasso;
            this.resources = resources;
            this.itemView = itemView;
            bodyColor = resources.getColor(R.color.default_body);
            secondBodyColor = resources.getColor(R.color.default_body);
        }

        @Override
        public void bind(@Nonnull MainPresenter.BaseItem item) {
            MainPresenter.MovieItem movieItem = (MainPresenter.MovieItem) item;

            name.setText(movieItem.getTitle());
            year.setText(String.format("(%s)", movieItem.getYear()));

            name.setSelected(true);
            mPicasso
                    .load(Strings.emptyToNull(movieItem.getPoster()))
                    .error(R.drawable.template_image)
                    .placeholder(R.drawable.internaly)
                    .fit()
                    .centerCrop()
                    .transform(PaletteTransformation.instance())
                    .into(imageView, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            Palette palette = PaletteTransformation.getPalette(bitmap);

                            if (palette.getMutedSwatch() != null) {
                                bodyColor = palette.getMutedSwatch().getBodyTextColor();
                                secondBodyColor = palette.getMutedSwatch().getRgb();
                            }
                            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
                                    new int[]{secondBodyColor, bodyColor, bodyColor, secondBodyColor});

                            titleView.setBackground(gradientDrawable);

                            if (isColorLight(bodyColor)) {
                                name.setTextColor(resources.getColor(android.R.color.black));
                                year.setTextColor(resources.getColor(android.R.color.black));
                            } else {
                                name.setTextColor(resources.getColor(android.R.color.white));
                                year.setTextColor(resources.getColor(android.R.color.white));
                            }

                        }
                    });

            mSubscription = new CompositeSubscription(
                    ViewObservable.clicks(itemView)
                            .subscribe(movieItem.clickObserver())
            );

        }

        public boolean isColorLight(int color){
            double darkness = 1-(0.299*Color.red(color) + 0.587* Color.green(color) + 0.114*Color.blue(color))/255;
            return darkness < 0.5;
        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }


        public static MovieHolder create(ViewGroup parent, Picasso mPicasso, Resources resources) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new MovieHolder(inflater.inflate(R.layout.movie_item, parent, false), mPicasso, resources);
        }

    }


    static class OptionsHolder extends BaseItemHolder {

        private final View view;
        private Subscription mSubscription;

        @InjectView(R.id.debug_spinner_name)
        TextView spinnerName;

        @InjectView(R.id.debug_spinner)
        Spinner spinner;


        public OptionsHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void bind(@Nonnull MainPresenter.BaseItem item) {
            MainPresenter.OptionsItem spinnerItem = (MainPresenter.OptionsItem) item;
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, spinnerItem.getTypeList());

            spinner.setAdapter(adapter);
            mSubscription = new CompositeSubscription(RxAdapterView.itemSelections(spinner)
                    .map(new Func1<Integer, String>() {
                        @Override
                        public String call(Integer integer) {
                            return adapter.getItem(integer);
                        }
                    })
                    .subscribe(spinnerItem.clickObserver()));

        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }

        public static OptionsHolder create(ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new OptionsHolder(inflater.inflate(R.layout.options_item, parent, false));
        }

    }

    @Override
    public BaseItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_MOVIE) {
            return MovieHolder.create(parent, mPicasso, resources);
        }
        throw new RuntimeException("there is no type that matches the type "
                + viewType
                + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(BaseItemHolder holder, int position) {
        holder.bind(baseItems.get(position));
    }


    @Override
    public int getItemViewType(int position) {
        final MainPresenter.BaseItem item = baseItems.get(position);
        if (item instanceof MainPresenter.MovieItem) {
            return TYPE_MOVIE;
        } else {
            throw new IllegalStateException("Cannot find item for position" + position);
        }
    }

    @Override
    public int getItemCount() {
        return baseItems.size();
    }

}
