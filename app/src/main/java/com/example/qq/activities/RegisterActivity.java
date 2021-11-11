package com.example.qq.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.qq.R;
import com.example.qq.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    public ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.login.setOnClickListener(v->onBackPressed());
    }
}