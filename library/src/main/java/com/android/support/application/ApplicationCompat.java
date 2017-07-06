package com.android.support.application;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * application获取辅助类
 *
 * @author lizhangqu
 * @version V1.0
 * @since 2017-07-06 20:42
 */
public class ApplicationCompat {
    private static final Object LOCK = new Object();
    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    /**
     * 返回全局Application
     *
     * @return Application
     */
    public static Application getApplication() {
        if (sApplication != null) {
            return sApplication;
        }
        synchronized (ApplicationCompat.class) {
            try {
                if (sApplication == null) {
                    sApplication = getSystemApp();
                    if (sApplication == null && Thread.currentThread().getId() != Looper.getMainLooper().getThread().getId()) {
                        synchronized (LOCK) {
                            //主线程直接获取
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new ApplicationGetter());
                            LOCK.wait();
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return sApplication;
    }

    /**
     * 获得Application
     *
     * @return Application
     * @throws ClassNotFoundException    ClassNotFoundException
     * @throws NoSuchMethodException     NoSuchMethodException
     * @throws NoSuchFieldException      NoSuchFieldException
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException    IllegalAccessException
     */
    private static Application getSystemApp() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        Class<?> cls = Class.forName("android.app.ActivityThread");
        Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
        Field declaredField = cls.getDeclaredField("mInitialApplication");
        declaredField.setAccessible(true);
        Object object = declaredField.get(declaredMethod.invoke(null));
        if (object instanceof Application) {
            return (Application) object;
        }
        return null;
    }

    /**
     * 阻塞子线程，切换到主线程获取
     */
    private static class ApplicationGetter implements Runnable {
        ApplicationGetter() {
        }

        @Override
        public void run() {
            try {
                sApplication = getSystemApp();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            synchronized (LOCK) {
                LOCK.notify();
            }
        }
    }

    /**
     * 获取applicationContext
     *
     * @return
     */
    public static Context getApplicationContext() {
        try {
            return getApplication().getApplicationContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回app名字
     *
     * @return appName
     */
    public static String getAppName() {
        try {
            PackageManager packageManager = getApplication().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getApplication().getPackageName(), 0);
            return packageManager.getApplicationLabel(applicationInfo).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 返回versionName
     *
     * @return versionName
     */
    public static String getVersionName() {
        try {
            return getApplication().getPackageManager().getPackageInfo(getApplication().getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回versionCode
     *
     * @return versionCode
     */
    public static int getVersionCode() {
        try {
            return getApplication().getPackageManager().getPackageInfo(getApplication().getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 是否是debug构建包
     *
     * @return 是否是debug构建包
     */
    public static boolean isDebugAble() {
        try {
            PackageManager packageManager = getApplication().getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getApplication().getPackageName(), 0);
            ApplicationInfo info = packInfo.applicationInfo;
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
