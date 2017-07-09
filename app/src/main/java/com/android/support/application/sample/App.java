package com.android.support.application.sample;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.support.application.EnvironmentCompat;
import com.android.support.application.LifecycleCompat;

/**
 * 功能介绍
 *
 * @author lizhangqu
 * @version V1.0
 * @since 2017-07-06 20:59
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //can't use it here

//        String name = Thread.currentThread().getName();
//        Application application = ApplicationCompat.getApplication();
//        Context context = ApplicationCompat.getApplicationContext();
//        ClassLoader classLoader = ApplicationCompat.getClassLoader();
//        String appName = ApplicationCompat.getAppName();
//        String versionName = ApplicationCompat.getVersionName();
//        int versionCode = ApplicationCompat.getVersionCode();
//        boolean isDebugAble = ApplicationCompat.isDebugAble();
//
//        Log.e("TAG", "threadName:" + name);
//        Log.e("TAG", "application:" + application);
//        Log.e("TAG", "applicationContext:" + context);
//        Log.e("TAG", "classLoader:" + classLoader);
//        Log.e("TAG", "appName:" + appName);
//        Log.e("TAG", "versionName:" + versionName);
//        Log.e("TAG", "versionCode:" + versionCode);
//        Log.e("TAG", "isDebugAble:" + isDebugAble);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EnvironmentCompat.getInstance().onApplicationCreate(this, EnvironmentCompat.Env.RELEASE);
        LifecycleCompat.getInstance().onApplicationCreate(this);

        LifecycleCompat.LifecycleCallback lifecycleCallback = new LifecycleCompat.LifecycleCallback() {
            @Override
            public void onCreated(Activity activity) {
                Log.e("TAG", "onCreated:" + activity);
            }

            @Override
            public void onDestroyed(Activity activity) {
                Log.e("TAG", "onDestroyed:" + activity);
            }

            @Override
            public void onStarted(Activity activity) {
                Log.e("TAG", "onStarted:" + activity);
            }

            @Override
            public void onStopped(Activity activity) {
                Log.e("TAG", "onStopped:" + activity);
            }
        };
        LifecycleCompat.getInstance().registerActivityLifecycleCallback(lifecycleCallback);
//        LifecycleCompat.getInstance().unregisterActivityLifecycleCallback(lifecycleCallback);
        //this is ok

//        String name = Thread.currentThread().getName();
//        Application application = ApplicationCompat.getApplication();
//        Context context = ApplicationCompat.getApplicationContext();
//        ClassLoader classLoader = ApplicationCompat.getClassLoader();
//        String appName = ApplicationCompat.getAppName();
//        String versionName = ApplicationCompat.getVersionName();
//        int versionCode = ApplicationCompat.getVersionCode();
//        boolean isDebugAble = ApplicationCompat.isDebugAble();
//
//        Log.e("TAG", "threadName:" + name);
//        Log.e("TAG", "application:" + application);
//        Log.e("TAG", "applicationContext:" + context);
//        Log.e("TAG", "classLoader:" + classLoader);
//        Log.e("TAG", "appName:" + appName);
//        Log.e("TAG", "versionName:" + versionName);
//        Log.e("TAG", "versionCode:" + versionCode);
//        Log.e("TAG", "isDebugAble:" + isDebugAble);
    }
}
