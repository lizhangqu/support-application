package com.android.support.application.sample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.support.application.LifecycleCompat;
import com.android.support.application.RouteCompat;
import com.android.support.application.RouteUri;

import java.lang.ref.WeakReference;
import java.util.List;

public class ThirdActivity extends AppCompatActivity {

    private class ApplicationLifecycleChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isBackground = intent.getBooleanExtra(LifecycleCompat.EXTRA_LIFECYCLE_STATUS, false);
            String packageName = intent.getStringExtra(LifecycleCompat.EXTRA_PACKAGE_NAME);
            if (TextUtils.equals(packageName, getApplicationContext().getPackageName())) {
                if (isBackground) {
                    Log.e("TAG", "app退到后台");
                } else {
                    Log.e("TAG", "app进入前台");
                }
            }
        }
    }

    private class ActivityCountChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getStringExtra(LifecycleCompat.EXTRA_PACKAGE_NAME);
            int activityCount = intent.getIntExtra(LifecycleCompat.EXTRA_ACTIVITY_COUNT, -1);
            if (TextUtils.equals(packageName, getApplicationContext().getPackageName())) {
                Log.e("TAG", "activityCountChanged:" + activityCount);
            }
        }
    }

    private ApplicationLifecycleChangedBroadcastReceiver mApplicationLifecycleChangedBroadcastReceiver = new ApplicationLifecycleChangedBroadcastReceiver();
    private ActivityCountChangeBroadcastReceiver mActivityCountChangeBroadcastReceiver = new ActivityCountChangeBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        registerReceiver(mApplicationLifecycleChangedBroadcastReceiver, new IntentFilter(LifecycleCompat.ACTION_APPLICATION_LIFECYCLE_CHANGED));
        registerReceiver(mActivityCountChangeBroadcastReceiver, new IntentFilter(LifecycleCompat.ACTION_ACTIVITY_COUNT_CHANGED));

        findViewById(R.id.lifecycle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int launchedActivityCount = LifecycleCompat.getInstance().getLaunchedActivityCount();
                Activity topActivity = LifecycleCompat.getInstance().getTopActivity();
                List<WeakReference<Activity>> launchedActivityList = LifecycleCompat.getInstance().getLaunchedActivityList();

                Log.e("TAG", "launchedActivityCount:" + launchedActivityCount);
                Log.e("TAG", "topActivity:" + topActivity);

                if (launchedActivityList != null) {
                    for (WeakReference<Activity> activityWeakReference : launchedActivityList) {
                        if (activityWeakReference != null && activityWeakReference.get() != null) {
                            Log.e("TAG", "launchedActivity:" + activityWeakReference.get());
                        }

                    }
                }
            }
        });

        findViewById(R.id.show_activity_class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LifecycleCompat.getInstance().showActivity(MainActivity.class);
            }
        });

        findViewById(R.id.show_activity_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LifecycleCompat.getInstance().showActivity("com.android.support.application.sample.MainActivity");
            }
        });

        findViewById(R.id.open_new_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteUri routeUri = RouteUri.scheme("https")
                        .host("support.android.com")
                        .path("support/application/second");
                RouteCompat.from(ThirdActivity.this).toUri(routeUri);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mApplicationLifecycleChangedBroadcastReceiver);
        unregisterReceiver(mActivityCountChangeBroadcastReceiver);
    }
}
