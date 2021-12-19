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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qq.RecentConversationListener;
import com.example.qq.activities.ChatActivity;
import com.example.qq.activities.LoginActivity;
import com.example.qq.adapter.RecentConversationAdapter;
import com.example.qq.databinding.FragmentMessageBinding;
import com.example.qq.model.Msg;
import com.example.qq.model.User;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MessageFragment extends Fragment implements RecentConversationListener{

    public FragmentMessageBinding messageBinding;
    private PreferenceManager pref;
    private List<Msg> recentConversations;
    private RecentConversationAdapter recentConversationAdapter;
    private FirebaseFirestore database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MessageFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        messageBinding = FragmentMessageBinding.inflate(inflater, container, false);
        View view = messageBinding.getRoot();
        pref = new PreferenceManager(container.getContext());
        init();
        setClickListener();
        loadUserDetail();
        listenConversation();
        return view;
    }

    public void init() {
        recentConversations = new ArrayList<>();
        recentConversationAdapter = new RecentConversationAdapter(recentConversations, this);
        messageBinding.recentMessages.setAdapter(recentConversationAdapter);
        database = FirebaseFirestore.getInstance();
    }

    public void listenConversation() {
        database
                .collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, pref.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database
                .collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, pref.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }


    public void setClickListener() {
        messageBinding.logout.setOnClickListener(v -> {
            logout();
        });
    }

    private void loadUserDetail() {
        messageBinding.name.setText(pref.getString(Constants.KEY_NAME));
        byte[] decodedString = Base64.decode(pref.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        messageBinding.avatar.setImageBitmap(decodedByte);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) return;
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    Msg msg = new Msg();
                    msg.senderId = senderId;
                    msg.receiverId = receiverId;
                    if (pref.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        msg.conversationImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        msg.conversationName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        msg.conversationId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    } else {
                        msg.conversationImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        msg.conversationName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        msg.conversationId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    msg.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    msg.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    recentConversations.add(msg);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < recentConversations.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (recentConversations.get(i).senderId.equals(senderId) && recentConversations.get(i).receiverId.equals(receiverId)) {
                            recentConversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            recentConversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(recentConversations, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            recentConversationAdapter.notifyDataSetChanged();
            messageBinding.recentMessages.setAdapter(recentConversationAdapter);
            messageBinding.recentMessages.smoothScrollToPosition(0);
            messageBinding.progressBar.setVisibility(View.GONE);
        }
    };

    private void logout() {
        Toast.makeText(getActivity(), "Logging out....", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dr = db.collection(Constants.KEY_COLLECTION_USERS).document(pref.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        dr.update(updates)
                .addOnSuccessListener(unused -> {
                    pref.clear();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "unable to log out.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onClickConversation(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
    }
}