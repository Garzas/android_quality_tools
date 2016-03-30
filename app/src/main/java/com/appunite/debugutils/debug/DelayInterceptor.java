package com.appunite.debugutils.debug;

import android.util.Log;

import java.io.IOException;

import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Response;

@Singleton
public class DelayInterceptor implements Interceptor {

    private int delay = 1;

    public DelayInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        this.sleep();
        Log.d("NetworkSlowdown", "Network slowdown done. Proceeding chain");

        return chain.proceed(chain.request());
    }

    private void sleep() {
        try {
            Log.d("NetworkSlowdown", String.format("Sleeping for %d seconds", delay));
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Log.e("NetworkSlowdown", "Interrupted", e);
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}