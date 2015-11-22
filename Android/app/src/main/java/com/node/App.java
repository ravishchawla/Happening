package com.node;

import android.app.Application;

/**
 * Created by Ravish on 10/29/2014.
 */
public class App extends Application {

    private static App instance;
    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
