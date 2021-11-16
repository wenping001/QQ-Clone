package com.example.qq.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.qq.AppDatabase;
import com.example.qq.databinding.ActivityLoginBinding;
import com.example.qq.model.User;


public class LoginActivity extends BaseActivity{

    public ActivityLoginBinding binding;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        if(pref.getBoolean("is_logged",false)){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        boolean isRemember = pref.getBoolean("remember_password",false);

        if(isRemember){
           isRememberThenRead();
        }

        binding.loginBtn.setOnClickListener(v->{
            String username = binding.username.getText().toString().trim();
            String password = binding.password.getText().toString().trim();
            User user = login(username,password);
            if (user == null) {
                Toast.makeText(this, "Wrong username or password", Toast.LENGTH_SHORT).show();
            }
            else{
                writeToPref();
                Intent intent = new Intent(this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        binding.createNewAccount.setOnClickListener(v-> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.username.addTextChangedListener(loginTextWatcher);
        binding.password.addTextChangedListener(loginTextWatcher);

        String username = binding.username.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        binding.loginBtn.setEnabled(!username.isEmpty() && !password.isEmpty());
    }

    private TextWatcher loginTextWatcher = new TextWatcher(){
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String username = binding.username.getText().toString().trim();
            String password = binding.password.getText().toString().trim();
            binding.loginBtn.setEnabled(!username.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private User login(String username,String password){
        AppDatabase db = AppDatabase.getInstance(this);
        return db.userDao().authenticateUser(username,password);
    }

    private void isRememberThenRead(){
       String username = pref.getString("username","");
       String password = pref.getString("password","");
       binding.username.setText(username);
       binding.password.setText(password);
       binding.rememberPassword.setChecked(true);
    }

    private void writeToPref(){
        String username = binding.username.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        editor = pref.edit();
        if(binding.rememberPassword.isChecked()) {
            editor.putBoolean("is_logged",true);
            editor.putBoolean("remember_password", true);
            editor.putString("username", username);
            editor.putString("password", password);
        }
        else{
            editor.clear();
        }
        editor.apply();
    }
}
