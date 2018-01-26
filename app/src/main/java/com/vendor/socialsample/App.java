package com.vendor.socialsample;

import android.app.Application;

import com.vendor.social.Social;

/**
 * application
 * Created by ljfan on 2018/1/26.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Social.init(this);
    }
}
