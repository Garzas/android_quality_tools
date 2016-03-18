package com.appunite.debugutils;

import android.app.Application;

import com.appunite.debugutils.dagger.AppComponent;
import com.appunite.debugutils.dagger.AppModule;
import com.appunite.debugutils.dagger.BaseModule;
import com.appunite.debugutils.dagger.DaggerAppComponent;

public class App extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        setupGraph();
    }


    private void setupGraph() {
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .baseModule(new BaseModule())
                .build();
        component.inject(this);
    }

    public static AppComponent getAppComponent(Application app) {
        return ((App) app).component;
    }
}
