package com.example.qq.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qq.activities.LoginActivity;
import com.example.qq.adapter.UserAdapter;
import com.example.qq.databinding.FragmentMessageBinding;
import com.example.qq.model.Contact;
import com.example.qq.model.User;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageFragment extends Fragment {

    private static final String TAG = "MessageFragment";

    public FragmentMessageBinding messageBinding;
    private PreferenceManager pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MessageFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        messageBinding = FragmentMessageBinding.inflate(inflater, container, false);
        View view = messageBinding.getRoot();
        pref = new PreferenceManager(getActivity().getApplicationContext());
        loadUserDetail();
        // Recycler view
        messageBinding.messageRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        getUsers();
        // logout
        messageBinding.logout.setOnClickListener(v->{logout();});
        return view;
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
                    userList.add(user);
                }
                if(userList.size() > 0){
                    UserAdapter userAdapter = new UserAdapter(getActivity(),userList);
                    messageBinding.messageRecyclerview.setAdapter(userAdapter);
                }
                else{
                    Toast.makeText(getActivity(), "No user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void isLoading(Boolean isLoading){
        if(isLoading){
            messageBinding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            messageBinding.progressBar.setVisibility(View.INVISIBLE);
        }
    }


    private void loadUserDetail(){
        messageBinding.name.setText(pref.getString(Constants.KEY_NAME));
        byte[] decodedString = Base64.decode(pref.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        messageBinding.avatar.setImageBitmap(decodedByte);
    }

    private void logout(){
        Toast.makeText(getActivity(), "Logging out....", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dr = db.collection(Constants.KEY_COLLECTION_USERS).document(pref.getString(Constants.KEY_USER_ID));
        HashMap<String,Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        dr.update(updates)
                .addOnSuccessListener(unused -> {
                    pref.clear();
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                    getActivity().finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "unable to log out.", Toast.LENGTH_SHORT).show();
                });
    }

}