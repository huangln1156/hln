package com.fix.hln_hotfix;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.fix.hln_hotfix.utils.FileDexUtils;

public class BaseApplication extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        FileDexUtils.loadFixedDex(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
