package com.android.support.application.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        if (intent != null) {
            Uri data = getIntent().getData();
            if (data != null) {
                String host = data.getHost();
                String path = data.getPath();
                String param = data.getQueryParameter("key1");
                String fragment = data.getFragment();
                Log.e("TAG", "host:" + host);
                Log.e("TAG", "path:" + path);
                Log.e("TAG", "param:" + param);
                Log.e("TAG", "fragment:" + fragment);
            }
        }
    }
}
