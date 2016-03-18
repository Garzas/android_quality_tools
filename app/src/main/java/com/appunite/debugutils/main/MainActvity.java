package com.appunite.debugutils.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.appunite.debugutils.App;
import com.appunite.debugutils.BaseActivity;
import com.appunite.debugutils.R;
import com.appunite.debugutils.dagger.ActivityModule;
import com.appunite.debugutils.dagger.BaseActivityComponent;
import com.appunite.debugutils.details.InfoActivity;
import com.appunite.debugutils.util.KeyboardHelper;
import com.appunite.rx.android.widget.RxToolbarMore;
import com.appunite.rx.functions.BothParams;
import com.appunite.rx.functions.Functions1;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxToolbar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.view.ViewActions;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class MainActvity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        MainActivityComponent component = (MainActivityComponent) getActivityComponent();
        component.inject(this);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
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
                        KeyboardHelper.hideSoftKeyboard(MainActvity.this);
                    }
                });

        presenter.showProgressObservable()
                .compose(this.<Boolean>bindToLifecycle())
                .subscribe(ViewActions.setVisibility(progressView));

        presenter.openDetailsObservable()
                .debounce(500, TimeUnit.MILLISECONDS)
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        startActivity(InfoActivity.newIntent(MainActvity.this, s));
                    }
                });

        presenter.getShowSearchObservable()
                .compose(this.<Boolean>bindToLifecycle())
                .subscribe(ViewActions.setVisibility(searchText));

        presenter.getToolbarSearchObservable()
                .compose(this.<BothParams<String, Boolean>>bindToLifecycle())
                .subscribe(new Action1<BothParams<String, Boolean>>() {
                    @Override
                    public void call(BothParams<String, Boolean> bothParams) {
                        final String title = bothParams.param1();
                        final Boolean show = bothParams.param2();
                        if (!show) {
                            getSupportActionBar().setTitle(title);
                        }
                        else {
                            getSupportActionBar().setTitle(null);
                        }

                    }
                });


        WidgetObservable.text(searchText)
                .map(mapToString())
                .compose(this.<String>bindToLifecycle())
                .subscribe(presenter.titleObserver());

        presenter.getMovieListObservable()
                .compose(this.bindToLifecycle())
                .map(Functions1.returnFalse())
                .subscribe(ViewActions.setVisibility(introView));
    }

    @NonNull
    private Func1<OnTextChangeEvent, String> mapToString() {
        return new Func1<OnTextChangeEvent, String>() {
            @Override
            public String call(OnTextChangeEvent onTextChangeEvent) {
                return onTextChangeEvent.text().toString();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.titleObservable()
                .compose(this.<String>bindToLifecycle())
                .subscribe(presenter.resumeEditTextObserver());

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
                presenter.showSearchObserver().onNext(new Object());
                return true;
            case R.id.search_options:
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
