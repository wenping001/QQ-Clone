package com.example.qq.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.qq.R;

public class LoginActivity extends BaseActivity{
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    public Button login;
    public EditText usernameEdit;
    public EditText passwordEdit;
    private CheckBox rememberPass;
    private TextView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        login = findViewById(R.id.login_btn);
        usernameEdit = findViewById(R.id.username);
        passwordEdit= findViewById(R.id.password);

        rememberPass = findViewById(R.id.remember_password);
        boolean isRemember = pref.getBoolean("remember_password",false);

        if(isRemember){
            String username = pref.getString("username","");
            String password = pref.getString("password","");
            usernameEdit.setText(username);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }

        login.setOnClickListener(v->{
            String usernameStr = usernameEdit.getText().toString();
            String passwordStr = passwordEdit.getText().toString();
            if(usernameStr.equals("admin") && passwordStr.equals("123456")){
                editor = pref.edit();
                if(rememberPass.isChecked()){
                    editor.putBoolean("remember_password",true);
                    editor.putString("username",usernameStr);
                    editor.putString("password",passwordStr);
                }
                else{
                    editor.clear();
                }
                editor.apply();

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

        register = findViewById(R.id.create_new_account);

        register.setOnClickListener(v-> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

    }
}