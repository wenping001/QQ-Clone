package com.example.qq.activities;


import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
        finish();
    }
}