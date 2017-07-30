package com.android.support.application.sample;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.support.application.ApplicationCompat;
import com.android.support.application.EnvironmentCompat;
import com.android.support.application.RouteCompat;
import com.android.support.application.RouteUri;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.application).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //线程中执行，覆盖部分系统使用ThreadLocal存储
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String name = Thread.currentThread().getName();
                        Application application = ApplicationCompat.getApplication();
                        Context context = ApplicationCompat.getApplicationContext();
                        ClassLoader classLoader = ApplicationCompat.getClassLoader();
                        String appName = ApplicationCompat.getAppName();
                        String versionName = ApplicationCompat.getVersionName();
                        int versionCode = ApplicationCompat.getVersionCode();
                        boolean isDebuggable = ApplicationCompat.isDebuggable();

                        Log.e("TAG", "threadName:" + name);
                        Log.e("TAG", "application:" + application);
                        Log.e("TAG", "applicationContext:" + context);
                        Log.e("TAG", "classLoader:" + classLoader);
                        Log.e("TAG", "appName:" + appName);
                        Log.e("TAG", "versionName:" + versionName);
                        Log.e("TAG", "versionCode:" + versionCode);
                        Log.e("TAG", "isDebuggable:" + isDebuggable);
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

        findViewById(R.id.route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteUri routeUri = RouteUri.scheme("https")
                        .host("support.android.com")
                        .path("support/application/second")
                        .param("key1", "value1")
                        .fragment("1");
                RouteCompat.from(MainActivity.this).toUri(routeUri);
            }
        });

        findViewById(R.id.env).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnvironmentCompat.Env env = EnvironmentCompat.getInstance().getEnv();
                Log.e("TAG", "env:" + env);
            }
        });

        findViewById(R.id.change_env).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] environments = {
                        EnvironmentCompat.Env.RELEASE.name(),
                        EnvironmentCompat.Env.PRE.name(),
                        EnvironmentCompat.Env.DEVELOP.name()
                };
                EnvironmentCompat.Env env = EnvironmentCompat.getInstance().getEnv();
                int ordinal = env.ordinal();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Please Select Environment")
                        .setCancelable(true)
                        .setSingleChoiceItems(environments, ordinal, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EnvironmentCompat.Env env = EnvironmentCompat.Env.values()[which];
                                EnvironmentCompat.getInstance().changeEnv(getApplicationContext(), env);
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        findViewById(R.id.lifecycle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteUri routeUri = RouteUri.scheme("https")
                        .host("support.android.com")
                        .path("support/application/third");
                RouteCompat.from(MainActivity.this).toUri(routeUri);
            }
        });

        findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        TagCompat.setTag(view, "key1", "value1");
//        TagCompat.getTag(view, "key1", "defaultValue");
//        TagCompat.containsTag(view, "key1");
    }

}
