package com.example.qq.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qq.databinding.ActivityRegisterBinding;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private PreferenceManager preferenceManager;

    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        View view = binding.getRoot();
        setContentView(view);

        binding.backToLogin.setOnClickListener(v->onBackPressed());
        binding.registerBtn.setOnClickListener(v->{
            register();
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

            binding.registerBtn.setEnabled(!username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && !email.isEmpty() && encodedImage !=null);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    public void register(){
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME,binding.usernameInput.getText().toString());
        user.put(Constants.KEY_EMAIL,binding.emailInput.getText().toString());
        user.put(Constants.KEY_PASSWORD,binding.passwordInput.getText().toString());
        user.put(Constants.KEY_IMAGE,encodedImage);

        db.collection(Constants.KEY_COLLECTION_USERS).add(user).addOnSuccessListener(
            documentReference ->{
                loading(false);
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                preferenceManager.putString(Constants.KEY_NAME,binding.usernameInput.getText().toString());
                preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        ).addOnFailureListener(
            e->{
                loading(false);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );
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
                           binding.addImage.setVisibility(View.INVISIBLE);
                           encodedImage = encodedImage(bitmap);
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

    private void loading(Boolean isLoading){
        if (isLoading) {
            binding.prgressBar.setVisibility(View.INVISIBLE);
            binding.registerBtn.setVisibility(View.VISIBLE);
        } else {
            binding.prgressBar.setVisibility(View.VISIBLE);
            binding.registerBtn.setVisibility(View.INVISIBLE);
        }
    }
}