package com.example.qq.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qq.adapter.ChatAdapter;
import com.example.qq.databinding.ActivityChatBinding;
import com.example.qq.model.Msg;
import com.example.qq.model.User;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.N)
public class ChatActivity extends BaseActivity{

    private static final String TAG = "ChatActivity";
    private ActivityChatBinding binding;
    private User receiveUser;
    private List<Msg> messages;
    private ChatAdapter chatAdapter;
    private PreferenceManager pref;
    private FirebaseFirestore database;
    private String conversationId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        receiveUser = (User)getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.topBarName.setText(receiveUser.name);
        init();
        setListeners();
        listenMessages();
    }

    /*
    * 照片格式转换
    * param: string
    * return: Bitmap
    * */
    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    /*
    * 监听数据变化
    * */
    final EventListener<QuerySnapshot> listener = (value, error) -> {
        if (error != null) {
            Log.w(TAG, "Listen failed.", error);
            return;
        }
        if (value!= null) {
            int count = messages.size();
            for(DocumentChange documentChange:value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    Msg msg = new Msg();
                    msg.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    msg.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    msg.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    msg.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    msg.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    messages.add(msg);
                }
            }
            Collections.sort(messages, Comparator.comparing(obj -> obj.dateObject));
            if(count == 0){
                chatAdapter.notifyDataSetChanged();
            }
            else{
                chatAdapter.notifyItemRangeInserted(messages.size(),messages.size());
                binding.msgRecyclerView.smoothScrollToPosition(messages.size() - 1);
            }
            binding.progressBar.setVisibility(View.INVISIBLE);
            if(conversationId == null){
                checkForConversation();
            }
        } else {
            Log.d(TAG, "Current data: null");
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void listenMessages(){
        final CollectionReference collectionReference = database.collection("chat");
        collectionReference
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiveUser.id)
                .whereEqualTo(Constants.KEY_SENDER_ID,pref.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(listener);
        collectionReference
                .whereEqualTo(Constants.KEY_SENDER_ID,receiveUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,pref.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(listener);
    }

    public void sendMessage(){
        if(binding.inputText.getText().toString().equals("")) return;
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,pref.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID,receiveUser.id);
        message.put(Constants.KEY_MESSAGE,binding.inputText.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversationId != null){
           updateConversation(binding.inputText.getText().toString());
        }
        else{
            HashMap<String,Object> conversation = new HashMap<>();
            conversation.put(Constants.KEY_SENDER_ID,pref.getString(Constants.KEY_USER_ID));
            conversation.put(Constants.KEY_SENDER_NAME,pref.getString(Constants.KEY_NAME));
            conversation.put(Constants.KEY_SENDER_IMAGE,pref.getString(Constants.KEY_IMAGE));
            conversation.put(Constants.KEY_RECEIVER_ID,receiveUser.id);
            conversation.put(Constants.KEY_RECEIVER_NAME,receiveUser.name);
            conversation.put(Constants.KEY_RECEIVER_IMAGE,receiveUser.image);
            conversation.put(Constants.KEY_TIMESTAMP,new Date());
            conversation.put(Constants.KEY_LAST_MESSAGE,binding.inputText.getText().toString());
            addConversation(conversation);
        }
        binding.inputText.setText(null);
    }

    private void updateConversation(String message){
        DocumentReference documentReference = database
                .collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .document(conversationId);
        documentReference.update(Constants.KEY_LAST_MESSAGE,message,Constants.KEY_TIMESTAMP,new Date());
    }
    public void addConversation(HashMap<String,Object> conversation){
        database
                .collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversation)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }
    private void init(){
        pref = new PreferenceManager(getApplicationContext());
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages,getBitmapFromEncodedString(receiveUser.image),pref.getString(Constants.KEY_USER_ID));
        binding.msgRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.msgRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners(){
        binding.back.setOnClickListener(v->onBackPressed());
        binding.send.setOnClickListener(v->sendMessage());
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault()).format(date);
    }

    private void checkForConversation(){
        if(messages.size() != 0){
            checkConversationRemotely(pref.getString(Constants.KEY_USER_ID),receiveUser.id);
            checkConversationRemotely(receiveUser.id,pref.getString(Constants.KEY_USER_ID));
        }
    }

    private void checkConversationRemotely(String senderId, String receiverId){
       database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
               .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
               .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId)
               .get()
               .addOnCompleteListener(conversationOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
        }
    };
}