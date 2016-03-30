package com.appunite.debugutils.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.appunite.debugutils.App;
import com.appunite.debugutils.BaseActivity;
import com.appunite.debugutils.R;
import com.appunite.debugutils.dagger.ActivityModule;
import com.appunite.debugutils.dagger.BaseActivityComponent;
import com.appunite.debugutils.details.InfoActivity;
import com.appunite.debugutils.dialog.TypeDialog;
import com.appunite.debugutils.util.KeyboardHelper;
import com.appunite.debugutils.view.ColoredSnackBar;
import com.appunite.rx.functions.BothParams;
import com.appunite.rx.functions.Functions1;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends BaseActivity {

    private static final int CHANGE_TYPE = 0;
    private static final String TYPE_VALUE = "type_value";

    @Inject
    MainAdapter adapter;

    @Inject
    MainPresenter presenter;

    @InjectView(R.id.main_progress)
    View progressView;
    @InjectView(R.id.main_recyclerview)
    RecyclerView recyclerView;
    @InjectView(R.id.main_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.search_view)
    EditText searchText;
    @InjectView(R.id.intro_layout)
    View introView;
    @InjectView(R.id.search_options_view)
    View searchOptionsView;
    @InjectView(R.id.select_type_button)
    TextView selectTypeView;
    @InjectView(R.id.main_content)
    View content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        MainActivityComponent component = (MainActivityComponent) getActivityComponent();
        component.inject(this);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        presenter.getObservableList()
                .compose(this.<List<MainPresenter.BaseItem>>bindToLifecycle())
                .subscribe(adapter);

        presenter.getObservableList()
                .compose(this.bindToLifecycle())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        recyclerView.scrollToPosition(0);
                    }
                });

        presenter.hideKeyboardObservable()
                .compose(this.bindToLifecycle())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        KeyboardHelper.hideSoftKeyboard(MainActivity.this);
                    }
                });

        presenter.showProgressObservable()
                .compose(this.<Boolean>bindToLifecycle())
                .subscribe(RxView.visibility(progressView));

        presenter.openDetailsObservable()
                .debounce(500, TimeUnit.MILLISECONDS)
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        startActivity(InfoActivity.newIntent(MainActivity.this, s));
                    }
                });

        presenter.getToolbarSearchObservable()
                .compose(this.<BothParams<String, Boolean>>bindToLifecycle())
                .subscribe(new Action1<BothParams<String, Boolean>>() {
                    @Override
                    public void call(BothParams<String, Boolean> bothParams) {
                        final String title = bothParams.param1();
                        final Boolean show = bothParams.param2();
                        if (show) {
                            getSupportActionBar().setTitle(String.format("Search results for %s", title));
                        } else {
                            getSupportActionBar().setTitle(null);
                        }

                    }
                });

        presenter.getShowOptionsObservable()
                .compose(this.<Boolean>bindToLifecycle())
                .subscribe(showOptionsView());


        RxTextView.textChangeEvents(searchText)
                .map(textToString())
                .compose(this.<String>bindToLifecycle())
                .subscribe(presenter.titleObserver());

        presenter.getMovieListObservable()
                .compose(this.bindToLifecycle())
                .map(Functions1.returnFalse())
                .subscribe(RxView.visibility(introView));

        RxView.clicks(selectTypeView)
                .compose(this.bindToLifecycle())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        transitionToActivity(selectTypeView);
                    }
                });

        presenter.movieTypeObservable()
                .startWith("all")
                .compose(this.<String>bindToLifecycle())
                .subscribe(RxTextView.text(selectTypeView));

    }

    @Nonnull
    private Action1<Boolean> showOptionsView() {
        return new Action1<Boolean>() {
            @Override
            public void call(Boolean show) {
                if (show) {
                    searchOptionsView.setTranslationY(-toolbar.getHeight());
                    searchOptionsView
                            .animate()
                            .translationY(0.0f)
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    searchOptionsView.setVisibility(View.VISIBLE);
                                }
                            });
                } else {

                    searchOptionsView
                            .animate()
                            .translationY(-toolbar.getHeight())
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    searchOptionsView.setVisibility(View.GONE);
                                }
                            });

                }
            }
        };
    }

    private void transitionToActivity(View view) {
        TextView textType = (TextView) view;

        final Pair<View, String> pair = new Pair<>(view, textType.getText().toString());
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair);
        startActivityForResult(TypeDialog.newIntent(MainActivity.this, ((TextView) view).getText().toString()), CHANGE_TYPE, transitionActivityOptions.toBundle());
    }


    @Nonnull
    private Func1<TextViewTextChangeEvent, String> textToString() {
        return new Func1<TextViewTextChangeEvent, String>() {
            @Override
            public String call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return textViewTextChangeEvent.text().toString();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                searchText.setVisibility(searchText.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                if (searchText.getVisibility() == View.VISIBLE) {
                    searchText.requestFocus();
                    KeyboardHelper.showSoftKeyboard(this);
                }
                if (searchText.length() < 2 && searchText.getVisibility() != View.VISIBLE) {
                    ColoredSnackBar
                            .error(content, "Enter at least 2 characters", Snackbar.LENGTH_SHORT)
                            .show();
                    searchText.setVisibility(View.VISIBLE);
                    searchText.requestFocus();
                } else {
                    presenter.searchViewVisibilityObserver().onNext(searchText.getVisibility());
                }


                return true;
            case R.id.search_options:
                presenter.optionsViewVisiblityObserver().onNext(searchOptionsView.getVisibility());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHANGE_TYPE) {
            if (resultCode == Activity.RESULT_OK) {
                String typeValue = data.getStringExtra(TYPE_VALUE);
                presenter.movieTypeObserver().onNext(typeValue);
            }
        }
    }


    @Nonnull
    @Override
    public BaseActivityComponent createActivityComponent(@Nullable Bundle savedInstanceState) {
        return DaggerMainActivityComponent
                .builder()
                .activityModule(new ActivityModule(this))
                .appComponent(App.getAppComponent(getApplication()))
                .build();
    }

}
