package com.jiaji.cookbook.app;

import android.app.Application;
import android.content.Context;

import im.fir.sdk.FIR;

/**
 * Created by jiana on 16-8-3.
 */
public class App extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        FIR.init(this);
    }

    public static Context getContext() {
        return context;
    }
}
