package com.example.qq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qq.adapter.MomentAdapter;
import com.example.qq.databinding.ActivityHomePageBinding;
import com.example.qq.model.Moment;
import com.example.qq.model.User;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.DecodeImage;
import com.example.qq.utilities.PreferenceManager;
import com.example.qq.utilities.SetUserDetail;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends BaseActivity {

    private ActivityHomePageBinding binding;
    private PreferenceManager pref;
    private List<Moment> momentList = new ArrayList<>();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new PreferenceManager(this);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.back.setOnClickListener(v -> onBackPressed());
        binding.myMoments.setLayoutManager(new LinearLayoutManager(this));
        binding.avatar.setImageBitmap(DecodeImage.decoder(pref.getString(Constants.KEY_IMAGE)));
        binding.name.setText(pref.getString(Constants.KEY_NAME));
        binding.email.setText(pref.getString(Constants.KEY_EMAIL));
    }

    public void getMoments() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        isLoading(true);
        db.collection(Constants.KEY_COLLECTION_MOMENT)
                .whereEqualTo(Constants.KEY_USER_ID, user.id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        isLoading(false);
                        List<Moment> moments = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Moment moment = new Moment();
                            moment.userId = queryDocumentSnapshot.getString(Constants.KEY_USER_ID);
                            moment.name = queryDocumentSnapshot.getString(Constants.KEY_MOMENT_NAME);
                            moment.image = queryDocumentSnapshot.getString(Constants.KEY_AVATAR);
                            moment.text = queryDocumentSnapshot.getString(Constants.KEY_MOMENT_CONTENT_TEXT);
                            moment.postImage = queryDocumentSnapshot.getString(Constants.KEY_CONTENT_IMAGE);
                            moment.time = queryDocumentSnapshot.getString(Constants.KEY_CONTENT_POSTED_TIME);
                            if (moment.name.equals(pref.getString(Constants.KEY_NAME))) {
                                moments.add(moment);
                            }
                        }
                        if (moments.size() > 0) {
                            MomentAdapter momentAdapter = new MomentAdapter(this, moments);
                            binding.myMoments.setAdapter(momentAdapter);
                        } else {
                            Toast.makeText(this, "No Moment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void isLoading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBarInHomePage.setVisibility(View.VISIBLE);
        } else {
            binding.progressBarInHomePage.setVisibility(View.INVISIBLE);
        }
    }

    public void getUser(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_USER_ID, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0) {
                        DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                        user = new User();
                        user.id = ds.getId();
                        user.name = ds.getString(Constants.KEY_NAME);
                        user.image = ds.getString(Constants.KEY_IMAGE);
                        user.email = ds.getString(Constants.KEY_EMAIL);
                    }
                });
    }
}
