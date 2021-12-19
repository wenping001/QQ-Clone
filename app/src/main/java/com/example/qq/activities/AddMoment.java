package com.example.qq.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.qq.R;
import com.example.qq.databinding.ActivityAddMomentBinding;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddMoment extends AppCompatActivity {

    private ActivityAddMomentBinding binding;
    private PreferenceManager pref;
    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMomentBinding.inflate(getLayoutInflater());
        pref = new PreferenceManager(this);
        loadUserDetail();
        setContentView(binding.getRoot());
        setListeners();
    }

    public void setListeners(){
        binding.cancel.setOnClickListener(v->{
            onBackPressed();
        });
        binding.post.setOnClickListener(v->{
            createNewPost();
        });
        binding.selectImage.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result ->{
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.selectedImage.setImageBitmap(bitmap);
                            binding.selectedImage.setVisibility(View.VISIBLE);
                            binding.selectImage.setVisibility(View.GONE);
                            encodedImage = encodedImage(bitmap);
                        } catch(FileNotFoundException e){
                            e.printStackTrace();

                        }
                    }
                }
            }
    );

    private void createNewPost(){
        String name = binding.name.getText().toString();
        String text = binding.inputText.getText().toString();
        String decodedString = pref.getString(Constants.KEY_IMAGE);
        String timestamps = DateFormat.getDateTimeInstance().format(new Date());

        Map<String,Object> moment = new HashMap<>();
        moment.put(Constants.KEY_USER_ID,pref.getString(Constants.KEY_USER_ID));
        moment.put(Constants.KEY_NAME,name);
        moment.put(Constants.KEY_AVATAR,decodedString);
        moment.put(Constants.KEY_CONTENT_IMAGE,encodedImage);
        moment.put(Constants.KEY_MOMENT_CONTENT_TEXT,text);
        moment.put(Constants.KEY_CONTENT_POSTED_TIME,timestamps);
        moment.put(Constants.KEY_TIMESTAMP,new Date());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_MOMENT).add(moment).addOnCompleteListener(documentReferenceTask->{
           onBackPressed();
        }).addOnFailureListener(e-> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public String encodedImage(Bitmap bitmap){
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private void loadUserDetail(){
        binding.name.setText(pref.getString(Constants.KEY_NAME));
        byte[] decodedString = Base64.decode(pref.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        binding.avatar.setImageBitmap(decodedByte);
    }

}