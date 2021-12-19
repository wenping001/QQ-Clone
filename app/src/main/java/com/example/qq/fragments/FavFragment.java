package com.example.qq.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qq.activities.AddMoment;
import com.example.qq.activities.HomePageActivity;
import com.example.qq.adapter.MomentAdapter;
import com.example.qq.databinding.FragmentFavBinding;
import com.example.qq.model.Moment;
import com.example.qq.model.Msg;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.DecodeImage;
import com.example.qq.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FavFragment extends Fragment {
    private FragmentFavBinding binding;
    private PreferenceManager pref;
    private List<Moment> moments;

    public FavFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavBinding.inflate(inflater, container, false);
        pref = new PreferenceManager(getActivity().getApplicationContext());
        binding.momentRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        View view = binding.getRoot();
        moments = new ArrayList<>();
        loadUserDetail();
        getMoments();
        binding.add.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMoment.class);
            startActivity(intent);
        });
        return view;
    }

    private void loadUserDetail() {
        String imageString = pref.getString(Constants.KEY_IMAGE);
        binding.avatar.setImageBitmap(DecodeImage.decoder(imageString));
        binding.name.setText(pref.getString(Constants.KEY_NAME));
    }

    public void getMoments() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        isLoading(true);
        db.collection(Constants.KEY_COLLECTION_MOMENT).addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                if (value.getDocumentChanges().isEmpty()) return;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Moment moment = new Moment();
                        moment.name = documentChange.getDocument().getString(Constants.KEY_NAME);
                        moment.userId = documentChange.getDocument().getString(Constants.KEY_USER_ID);
                        moment.time = documentChange.getDocument().getString(Constants.KEY_CONTENT_POSTED_TIME);
                        moment.image = documentChange.getDocument().getString(Constants.KEY_AVATAR);
                        moment.postImage = documentChange.getDocument().getString(Constants.KEY_CONTENT_IMAGE);
                        moment.text = documentChange.getDocument().getString(Constants.KEY_MOMENT_CONTENT_TEXT);
                        moment.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                        moments.add(moment);
                    }
                }
                Collections.sort(moments, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
                MomentAdapter adapter = new MomentAdapter(getContext(), moments);
                binding.momentRecyclerview.setAdapter(adapter);
                isLoading(false);
            }
        });
    }

    private void isLoading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBarInFav.setVisibility(View.VISIBLE);
        } else {
            binding.progressBarInFav.setVisibility(View.INVISIBLE);
        }
    }
}