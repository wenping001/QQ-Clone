package com.example.qq;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public class LoginActivity extends BaseActivity{

    public Button login;
    public EditText username;
    public EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login_btn);
        username = findViewById(R.id.username);
        password= findViewById(R.id.password);
        login.setOnClickListener(v->{
            if(username.getText().toString().equals("admin") && password.getText().toString().equals("123456")){
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            }
            else{
                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Warning");

                builder.setMessage("wrong username or password!");
                builder.show();
            }
        });

    }
}