package com.example.qq.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.qq.adapter.MomentAdapter;
import com.example.qq.databinding.FragmentFavBinding;
import com.example.qq.model.Moment;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class FavFragment extends Fragment {
    private FragmentFavBinding binding;
    private PreferenceManager pref;
    public FavFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavBinding.inflate(inflater,container,false);
        pref = new PreferenceManager(getActivity().getApplicationContext());
        binding.momentRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        View view = binding.getRoot();
        loadUserDetail();
        getMoments();
        return view;
    }

    private void loadUserDetail(){
        binding.name.setText(pref.getString(Constants.KEY_NAME));
        byte[] decodedString = Base64.decode(pref.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        binding.avatar.setImageBitmap(decodedByte);
    }

    public void getMoments(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        isLoading(true);
        db.collection(Constants.KEY_COLLECTION_MOMENT).get().addOnCompleteListener(task->{
            if(task.isSuccessful() && task.getResult()!=null){
                isLoading(false);
                List<Moment> moments = new ArrayList<>();
                for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                    Moment moment = new Moment();
                    moment.name = queryDocumentSnapshot.getString(Constants.KEY_MOMENT_NAME);
                    moment.image = queryDocumentSnapshot.getString(Constants.KEY_AVATAR);
                    moment.text = queryDocumentSnapshot.getString(Constants.KEY_MOMENT_CONTENT_TEXT);
                    moment.postImage= queryDocumentSnapshot.getString(Constants.KEY_CONTENT_IMAGE);
                    moment.time = queryDocumentSnapshot.getString(Constants.KEY_CONTENT_POSTED_TIME);
                    moments.add(moment);
                    moments.add(moment);
                }
                if(moments.size() > 0){
                    MomentAdapter momentAdapter = new MomentAdapter(getActivity(),moments);
                    binding.momentRecyclerview.setAdapter(momentAdapter);
                }
                else{
                    Toast.makeText(getActivity(), "No Moment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void isLoading(Boolean isLoading){
        if(isLoading){
            binding.progressBarInFav.setVisibility(View.VISIBLE);
        }
        else{
            binding.progressBarInFav.setVisibility(View.INVISIBLE);
        }
    }
}