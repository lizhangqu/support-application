package com.android.support.application.sample;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.support.application.ApplicationCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //线程中执行，覆盖部分系统使用ThreadLocal存储
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String name = Thread.currentThread().getName();
                        Application application = ApplicationCompat.getApplication();
                        Context context = ApplicationCompat.getApplicationContext();
                        String appName = ApplicationCompat.getAppName();
                        String versionName = ApplicationCompat.getVersionName();
                        int versionCode = ApplicationCompat.getVersionCode();
                        boolean isDebugAble = ApplicationCompat.isDebugAble();

                        Log.e("TAG", "threadName:" + name);
                        Log.e("TAG", "application:" + application);
                        Log.e("TAG", "applicationContext:" + context);
                        Log.e("TAG", "appName:" + appName);
                        Log.e("TAG", "versionName:" + versionName);
                        Log.e("TAG", "versionCode:" + versionCode);
                        Log.e("TAG", "isDebugAble:" + isDebugAble);
                    }
                };
                ExecutorService executorService = Executors.newFixedThreadPool(8);
                executorService.submit(runnable);
                executorService.submit(runnable);
                executorService.submit(runnable);
                executorService.submit(runnable);
                executorService.submit(runnable);
                executorService.submit(runnable);
                executorService.submit(runnable);
                executorService.submit(runnable);
                executorService.submit(runnable);
                executorService.submit(runnable);

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runnable.run();
            }
        });
    }
}
