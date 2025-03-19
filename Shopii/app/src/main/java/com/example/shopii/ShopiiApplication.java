package com.example.shopii;

import android.app.Application;

public class ShopiiApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new AppLifecycleTracker());
    }
} 