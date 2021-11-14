package com.example.qq.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.qq.AppDatabase;
import com.example.qq.databinding.ActivityRegisterBinding;
import com.example.qq.model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    public ActivityRegisterBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.backToLogin.setOnClickListener(v->onBackPressed());
        binding.registerBtn.setOnClickListener(v->{
            String username = binding.usernameInput.getText().toString().trim();
            String password = binding.passwordInput.getText().toString().trim();
            String email = binding.emailInput.getText().toString().trim();
            Log.d("TEST","register button clicked.");
            register(username,email,password);
        });

        binding.emailInput.addTextChangedListener(registerTextWatcher);
        binding.passwordInput.addTextChangedListener(registerTextWatcher);
        binding.confirmPassword.addTextChangedListener(registerTextWatcher);
        binding.usernameInput.addTextChangedListener(registerTextWatcher);

        binding.layoutImage.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

    }
    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String email = binding.emailInput.getText().toString().trim();
            String password = binding.passwordInput.getText().toString().trim();
            String confirmPassword= binding.confirmPassword.getText().toString().trim();
            String username = binding.usernameInput.getText().toString().trim();

            binding.registerBtn.setEnabled(!username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && !email.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    public void register(String username,String email,String password){
        AppDatabase db =  AppDatabase.getInstance(this);
        User user = new User(username,password,email);
        db.userDao().insertUser(user);
        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
           new ActivityResultContracts.StartActivityForResult(), result ->{

               if(result.getResultCode() == RESULT_OK){
                   if(result.getData() != null){
                       Uri imageUri = result.getData().getData();
                       try{
                           InputStream inputStream = getContentResolver().openInputStream(imageUri);
                           Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                           binding.imageProfile.setImageBitmap(bitmap);
                           binding.addImage.setVisibility(View.GONE);
                           encodedImage(bitmap);
                       } catch(FileNotFoundException e){
                           e.printStackTrace();

                       }
                   }
               }
            }
    );

    public String encodedImage(Bitmap bitmap){
       int previewWidth = 150;
       int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
       Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
       previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
       byte[] bytes = byteArrayOutputStream.toByteArray();
       return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    public Boolean isConflict(String username,String email){
        AppDatabase db = AppDatabase.getInstance(this);
        User user = db.userDao().userIsRegistered(email,username);
        return true;
    }
}