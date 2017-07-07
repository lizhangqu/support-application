package com.android.support.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 环境切换
 *
 * @author lizhangqu
 * @version V1.0
 * @since 2017-07-07 16:18
 */
public class EnvironmentCompat {
    private static final String KEY = "env";

    private static class EnvironmentCompatHolder {
        private static final EnvironmentCompat INSTANCE = new EnvironmentCompat();
    }

    private EnvironmentCompat() {
    }

    public static EnvironmentCompat getInstance() {
        return EnvironmentCompatHolder.INSTANCE;
    }

    private Env env = Env.RELEASE;

    public enum Env {
        RELEASE, //线上环境
        PRE,     //预发布环境
        DEVELOP  //开发环境
    }

    /**
     * application onCreate最前面调用
     *
     * @param application Application
     * @param defaultEnv  默认环境
     */
    public void onApplicationCreate(Application application, Env defaultEnv) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        String persistEnv = sharedPreferences.getString(KEY, defaultEnv.name());
        this.env = Env.valueOf(persistEnv);
    }


    /**
     * 改变环境，改变完成后建议杀进程
     *
     * @param context Context
     * @param env     需要切换到的环境
     * @return 改变后的环境
     */
    public Env changeEnv(Context context, Env env) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().putString(KEY, env.name()).apply();
        this.env = env;
        return env;
    }

    /**
     * 获取当前环境
     *
     * @return
     */
    public Env getEnv() {
        return env;
    }
}
