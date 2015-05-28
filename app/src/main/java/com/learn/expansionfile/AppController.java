package com.learn.expansionfile;

import android.app.Application;

/**
 * Created by randiwaranugraha on 5/28/15.
 */
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private static AppController instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized AppController getInstance() {
        return instance;
    }
}