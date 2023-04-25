package com.tedu.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import java.util.ArrayList;
import java.util.List;

/**
 * Application
 * Created by huangyx on 2018/3/5.
 */
public abstract class BaseApplication extends Application {

    private static BaseApplication sContext;

    public static BaseApplication getApplication() {
        return sContext;
    }

    private static List<Activity> sActivities = new ArrayList<Activity>();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    abstract public boolean isDebug();
}
