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
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * activity及app前后台生命周期
 *
 * @author lizhangqu
 * @version V1.0
 * @since 2017-07-07 15:26
 */
public class LifecycleCompat {
    public static final String ACTION_ACTIVITY_COUNT_CHANGED = "com.android.support.application.ACTIVITY_COUNT_CHANGED";
    public static final String ACTION_APPLICATION_LIFECYCLE_CHANGED = "com.android.support.application.APP_LIFECYCLE_CHANGED";
    public static final String EXTRA_ACTIVITY_COUNT = "activity_count";
    public static final String EXTRA_LIFECYCLE_STATUS = "lifecycle_status";
    public static final String EXTRA_PACKAGE_NAME = "package_name";


    private final Handler mHandler;
    private final List<LifecycleCallback> mActivityLifecycleCallbacks;

    private final AtomicInteger mCreationCount;
    private final AtomicInteger mStartCount;

    private WeakReference<Activity> mFirstActivity;
    private List<WeakReference<Activity>> mActivities;
    private Application mApplication;

    public interface LifecycleCallback {
        void onCreated(Activity activity);

        void onDestroyed(Activity activity);

        void onStarted(Activity activity);

        void onStopped(Activity activity);
    }


    private static class ActivityLifecycleCallbacksCompatImpl implements Application.ActivityLifecycleCallbacks {
        @Override
        public final void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            WeakReference<Activity> weakReference = new WeakReference<>(activity);
            LifecycleCompat.getInstance().addActivity(activity);
            if (LifecycleCompat.getInstance().mCreationCount.getAndIncrement() == 0 && !LifecycleCompat.getInstance().mActivityLifecycleCallbacks.isEmpty()) {
                LifecycleCompat.getInstance().mFirstActivity = weakReference;
                for (LifecycleCallback onCreated : LifecycleCompat.getInstance().mActivityLifecycleCallbacks) {
                    onCreated.onCreated(activity);
                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (LifecycleCompat.getInstance().mStartCount.getAndIncrement() == 0) {
                LifecycleCompat.getInstance().sendApplicationLifeCycle(false);
                if (!LifecycleCompat.getInstance().mActivityLifecycleCallbacks.isEmpty()) {
                    for (LifecycleCallback onStarted : LifecycleCompat.getInstance().mActivityLifecycleCallbacks) {
                        onStarted.onStarted(activity);
                    }
                }
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (LifecycleCompat.getInstance().mStartCount.decrementAndGet() == 0) {
                LifecycleCompat.getInstance().sendApplicationLifeCycle(true);
                if (!LifecycleCompat.getInstance().mActivityLifecycleCallbacks.isEmpty()) {
                    for (LifecycleCallback onStopped : LifecycleCompat.getInstance().mActivityLifecycleCallbacks) {
                        onStopped.onStopped(activity);
                    }
                }
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            LifecycleCompat.getInstance().removeActivity(activity);
            if (LifecycleCompat.getInstance().mCreationCount.decrementAndGet() == 0 && !LifecycleCompat.getInstance().mActivityLifecycleCallbacks.isEmpty()) {
                for (LifecycleCallback onDestroyed : LifecycleCompat.getInstance().mActivityLifecycleCallbacks) {
                    onDestroyed.onDestroyed(activity);
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }


        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

    }


    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private static final LifecycleCompat INSTANCE = new LifecycleCompat();
    }

    private LifecycleCompat() {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mActivityLifecycleCallbacks = new CopyOnWriteArrayList<>();
        this.mCreationCount = new AtomicInteger();
        this.mStartCount = new AtomicInteger();
        this.mActivities = new CopyOnWriteArrayList<>();
    }

    public static LifecycleCompat getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void onApplicationCreate(Application application) {
        this.mApplication = application;
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksCompatImpl());
    }

    public synchronized void registerActivityLifecycleCallback(LifecycleCallback activityLifecycleCallback) {
        if (activityLifecycleCallback == null) {
            return;
        }
        this.mActivityLifecycleCallbacks.add(activityLifecycleCallback);
        if (this.mCreationCount.get() > 0) {
            mHandler.post(new CallbackRunnable(activityLifecycleCallback, "onCreated"));
        }
        if (this.mStartCount.get() > 0) {
            mHandler.post(new CallbackRunnable(activityLifecycleCallback, "onStarted"));
        }

    }

    public synchronized void unregisterActivityLifecycleCallback(LifecycleCallback activityLifecycleCallback) {
        this.mActivityLifecycleCallbacks.remove(activityLifecycleCallback);
    }

    public int getCreatedCount() {
        return mCreationCount.get();
    }

    public int getStartedCount() {
        return mStartCount.get();
    }

    public int getLaunchedActivityCount() {
        return mActivities.size();
    }

    public List<WeakReference<Activity>> getLaunchedActivityList() {
        return mActivities;
    }

    public Activity getTopActivity() {
        int size = mActivities.size();
        if (null != mActivities && size > 0) {
            return mActivities.get(size - 1).get();
        }
        return null;
    }

    public void showActivity(String activityName) {
        for (WeakReference<Activity> activityWeakReference : mActivities) {
            if (null != activityWeakReference.get()) {
                Activity activity = activityWeakReference.get();
                if (null != activity && !TextUtils.equals(activityName, activity.getClass().getName())) {
                    activity.finish();
                }
            }
        }
    }

    public void showActivity(Class<? extends Activity> activityClass) {
        for (WeakReference<Activity> activityWeakReference : mActivities) {
            if (null != activityWeakReference.get()) {
                Activity activity = activityWeakReference.get();
                if (null != activity && !TextUtils.equals(activityClass.getName(), activity.getClass().getName())) {
                    activity.finish();
                }
            }
        }
    }

    private void addActivity(Activity activity) {
        if (null != activity) {
            mActivities.add(new WeakReference<>(activity));
            sendActivityChangeBroadcast();
        }
    }

    private void removeActivity(Activity removeActivity) {
        for (WeakReference<Activity> activityWeakReference : mActivities) {
            if (null != activityWeakReference) {
                Activity activity = activityWeakReference.get();
                if (null != activity && null != removeActivity) {
                    if (activity == removeActivity) {
                        mActivities.remove(activityWeakReference);
                        sendActivityChangeBroadcast();
                        break;
                    }
                }
            }
        }
    }

    private void sendActivityChangeBroadcast() {
        Intent intent = new Intent();
        intent.setAction(ACTION_ACTIVITY_COUNT_CHANGED);
        intent.putExtra(EXTRA_ACTIVITY_COUNT, mActivities.size());
        intent.putExtra(EXTRA_PACKAGE_NAME, mApplication.getPackageName());
        mApplication.sendBroadcast(intent);
    }


    private void sendApplicationLifeCycle(boolean isBackground) {
        Intent intent = new Intent();
        intent.setAction(ACTION_APPLICATION_LIFECYCLE_CHANGED);
        intent.putExtra(EXTRA_LIFECYCLE_STATUS, isBackground);
        intent.putExtra(EXTRA_PACKAGE_NAME, mApplication.getPackageName());
        mApplication.sendBroadcast(intent);
    }

    private static class CallbackRunnable implements Runnable {
        private LifecycleCallback mActivityLifecycleCallback;
        private String name;

        private CallbackRunnable(LifecycleCallback activityLifecycleCallback, String name) {
            this.mActivityLifecycleCallback = activityLifecycleCallback;
            this.name = name;
        }

        @Override
        public void run() {
            if (LifecycleCompat.getInstance().mFirstActivity != null) {
                Activity activity = LifecycleCompat.getInstance().mFirstActivity.get();
                if (!(activity == null || this.mActivityLifecycleCallback == null)) {
                    if ("onCreated".equals(this.name)) {
                        this.mActivityLifecycleCallback.onCreated(activity);
                    } else if ("onStarted".equals(this.name)) {
                        this.mActivityLifecycleCallback.onStarted(activity);
                    } else if ("onStopped".equals(this.name)) {
                        this.mActivityLifecycleCallback.onStopped(activity);
                    } else if ("onDestroyed".equals(this.name)) {
                        this.mActivityLifecycleCallback.onDestroyed(activity);
                    }
                }
            }
            this.mActivityLifecycleCallback = null;
            this.name = null;
        }
    }


}
