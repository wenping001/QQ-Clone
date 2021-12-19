package com.example.qq.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.qq.UserListener;
import com.example.qq.activities.ChatActivity;
import com.example.qq.adapter.UserAdapter;
import com.example.qq.databinding.FragmentContactsBinding;
import com.example.qq.model.User;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.PreferenceManager;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactsFragment extends Fragment implements UserListener{

    private FragmentContactsBinding binding;
    private PreferenceManager pref;

    public ContactsFragment() { }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        pref = new PreferenceManager(requireActivity());
        binding.contactsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadUserDetail();
        getUsers();
        return view;
    }
    private void loadUserDetail(){
        binding.name.setText(pref.getString(Constants.KEY_NAME));
        byte[] decodedString = Base64.decode(pref.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        binding.avatar.setImageBitmap(decodedByte);
    }

    public void getUsers(){
        isLoading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task->{
            isLoading(false);
            String currentUserId =  pref.getString(Constants.KEY_USER_ID);
            if(task.isSuccessful() && task.getResult()!=null){
                List<User> userList = new ArrayList<>();
                for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                    if(currentUserId.equals(queryDocumentSnapshot.getId())){
                        continue;
                    }
                    User user = new User();
                    user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                    user.image= queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                    user.email= queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                    user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                    user.id = queryDocumentSnapshot.getId();
                    userList.add(user);
                }
                if(userList.size() > 0){
                    UserAdapter userAdapter = new UserAdapter(getActivity(), userList, this);
                    binding.contactsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                    binding.contactsRecyclerview.setAdapter(userAdapter);
                }
                else{
                    Toast.makeText(getActivity(), "No user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void isLoading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getActivity(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
    }
}