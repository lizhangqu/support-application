package com.android.support.application.sample;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Uri data = getIntent().getData();
        String param = data.getQueryParameter("key1");
        Log.e("TAG", "param:" + param);
        String fragment = data.getFragment();
        Log.e("TAG", "fragment:" + fragment);
    }
}
