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
 * activity生命周期
 *
 * @author lizhangqu
 * @version V1.0
 * @since 2017-07-07 15:26
 */
public class LifecycleCompat implements Application.ActivityLifecycleCallbacks {
    public static final String ACTION_ACTIVITY_LIST_CHANGED = "com.android.support.application.ACTIVITY_LIST_CHANGED";
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

        void onResumed(Activity activity);

        void onPaused(Activity activity);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        this.addActivity(activity);
        if (this.mCreationCount.getAndIncrement() == 0 && !this.mActivityLifecycleCallbacks.isEmpty()) {
            this.mFirstActivity = weakReference;
            for (LifecycleCallback onCreated : this.mActivityLifecycleCallbacks) {
                onCreated.onCreated(activity);
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (this.mStartCount.getAndIncrement() == 0) {
            this.sendApplicationLifeCycle(false);
            if (!this.mActivityLifecycleCallbacks.isEmpty()) {
                for (LifecycleCallback onStarted : this.mActivityLifecycleCallbacks) {
                    onStarted.onStarted(activity);
                }
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (this.mStartCount.decrementAndGet() == 0) {
            this.sendApplicationLifeCycle(true);
            if (!this.mActivityLifecycleCallbacks.isEmpty()) {
                for (LifecycleCallback onStopped : this.mActivityLifecycleCallbacks) {
                    onStopped.onStopped(activity);
                }
            }
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        this.removeActivity(activity);
        if (this.mCreationCount.decrementAndGet() == 0 && !this.mActivityLifecycleCallbacks.isEmpty()) {
            for (LifecycleCallback onDestroyed : this.mActivityLifecycleCallbacks) {
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
        application.registerActivityLifecycleCallbacks(this);
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
                if (null != activity && !TextUtils.equals(activityName, activity.getLocalClassName())) {
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
        intent.setAction(ACTION_ACTIVITY_LIST_CHANGED);
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
                    } else if ("onResumed".equals(this.name)) {
                        this.mActivityLifecycleCallback.onResumed(activity);
                    } else if ("onPaused".equals(this.name)) {
                        this.mActivityLifecycleCallback.onPaused(activity);
                    }
                }
            }
            this.mActivityLifecycleCallback = null;
            this.name = null;
        }
    }


}
