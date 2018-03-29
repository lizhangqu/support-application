//Copyright 2017 区长. All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are
//met:
//
//* Redistributions of source code must retain the above copyright
//notice, this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above
//copyright notice, this list of conditions and the following disclaimer
//in the documentation and/or other materials provided with the
//distribution.
//* Neither the name of Google Inc. nor the names of its
//contributors may be used to endorse or promote products derived from
//this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
//"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
//LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
//A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
//OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
//SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
//LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
//THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
//OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
package com.android.support.application;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * application获取辅助类，只能在application的attachBaseContext之后使用
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
     * 获得Application
     *
     * @return Application
     * @throws NotMainThreadLocalException NotMainThreadLocalException
     * @throws ClassNotFoundException      ClassNotFoundException
     * @throws NoSuchMethodException       NoSuchMethodException
     * @throws NoSuchFieldException        NoSuchFieldException
     * @throws InvocationTargetException   InvocationTargetException
     * @throws IllegalAccessException      IllegalAccessException
     */
    private static Application getSystemApp() throws NotMainThreadLocalException, ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        Class<?> cls = Class.forName("android.app.ActivityThread");
        Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
        Field declaredField = cls.getDeclaredField("mInitialApplication");
        declaredField.setAccessible(true);
        Object currentActivityThread = declaredMethod.invoke(null);
        if (currentActivityThread == null && Thread.currentThread().getId() != Looper.getMainLooper().getThread().getId()) {
            //it is in thread local, using main thread to get it
            throw new NotMainThreadLocalException("you should get it from main thread");
        }
        Object object = declaredField.get(currentActivityThread);
        if (object instanceof Application) {
            return (Application) object;
        }
        return null;
    }

    /**
     * 对于4.0-4.1的机型，从子线程中获取currentActivityThread抛出此异常，切换到主线程阻塞获取
     */
    private static class NotMainThreadLocalException extends IllegalStateException {
        NotMainThreadLocalException(String s) {
            super(s);
        }
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
                if (sApplication != null) {
                    return;
                }
                sApplication = getSystemApp();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                synchronized (LOCK) {
                    LOCK.notifyAll();
                }
            }
        }
    }

    /**
     * 返回全局Application
     *
     * @return Application
     */
    public static Application getApplication() {
        if (sApplication != null) {
            return sApplication;
        }
        try {
            sApplication = getSystemApp();
        } catch (NotMainThreadLocalException e) {
            if (sApplication == null) {
                synchronized (LOCK) {
                    if (sApplication != null) {
                        return sApplication;
                    }
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new ApplicationGetter());
                    try {
                        LOCK.wait(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return sApplication;
    }

    /**
     * 获取applicationContext
     *
     * @return Context
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
     * 返回classloader, 不进行缓存
     *
     * @return ClassLoader
     */
    public static ClassLoader getClassLoader() {
        try {
            return getApplication().getClassLoader();
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
    public static boolean isDebuggable() {
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

    /**
     * kill process
     */
    public static void killProcess() {
        int myPid = android.os.Process.myPid();
        Process.killProcess(myPid);
    }

    /**
     * 是否是主进程
     *
     * @return 是否是主进程
     */
    public static boolean isMainProcess() {
        try {
            Context context = getApplicationContext();
            if (context == null) {
                return false;
            }
            ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
            String mainProcessName = context.getPackageName();
            int myPid = Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
                if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
