package com.keeplive.hln;

import android.app.Application;
import android.content.Context;



public class App extends Application {
    public static Context applicationContext;
    public static String MODEL = null;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        MODEL = android.os.Build.MODEL;

    }
}
