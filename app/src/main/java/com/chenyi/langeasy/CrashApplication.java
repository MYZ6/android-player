package com.chenyi.langeasy;

/**
 * Created by liyzh on 2016/9/12.
 */

import android.app.Application;

public class CrashApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}

