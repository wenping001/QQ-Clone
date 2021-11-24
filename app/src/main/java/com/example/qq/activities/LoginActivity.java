package com.example.qq.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.qq.databinding.ActivityLoginBinding;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends BaseActivity{

    public ActivityLoginBinding binding;
    private PreferenceManager pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new PreferenceManager(getApplication());

        if(pref.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    public void setListeners(){
        // jump to register page
        binding.createNewAccount.setOnClickListener(v-> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.username.addTextChangedListener(loginTextWatcher);
        binding.password.addTextChangedListener(loginTextWatcher);

        binding.loginBtn.setOnClickListener(v->{
           loading(true);
           login();
        });
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.loginBtn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.loginBtn.setVisibility(View.VISIBLE);
        }
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

    private void login(){
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_NAME,binding.username.getText().toString())
            .whereEqualTo(Constants.KEY_PASSWORD,binding.password.getText().toString())
            .get()
            .addOnCompleteListener(task-> {
                if(task.isSuccessful() && task.getResult()!=null && task.getResult().size() > 0){
                    DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                    pref.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    pref.putString(Constants.KEY_USER_ID,ds.getId());
                    pref.putString(Constants.KEY_NAME,ds.getString(Constants.KEY_NAME));
                    pref.putString(Constants.KEY_IMAGE,ds.getString(Constants.KEY_IMAGE));
                    Intent intent = new Intent(this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_SHORT).show();
                    loading(false);
                }
            });
    }

}
