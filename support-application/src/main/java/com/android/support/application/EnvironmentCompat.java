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

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;

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
     */
    public void changeEnv(Context context, Env env) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().putString(KEY, env.name()).apply();
        int myPid = android.os.Process.myPid();
        Process.killProcess(myPid);
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
