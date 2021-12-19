package com.example.qq.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.example.qq.databinding.ActivityDisplayImageBinding;

public class DisplayImageActivity extends AppCompatActivity {

    private ActivityDisplayImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String imageString= getIntent().getStringExtra("image");
        binding.image.setImageBitmap(decodeImageString(imageString));
        binding.image.setOnClickListener(v->{
            onBackPressed();
        });
    }
    public Bitmap decodeImageString(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}