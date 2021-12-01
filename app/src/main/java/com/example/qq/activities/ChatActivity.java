package com.example.qq.activities;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import com.example.qq.adapter.MsgAdapter;
import com.example.qq.databinding.ActivityChatBinding;
import com.example.qq.model.Msg;
import com.example.qq.model.User;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChatActivity extends BaseActivity{

    private ActivityChatBinding binding;

    private PreferenceManager pref;
    private final List<Msg> msgList = new ArrayList<>();
    private MsgAdapter adapter;
    private User receiver;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();

//        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
//        binding.msgRecyclerView.setLayoutManager(layoutManager);
//
//        binding.back.setOnClickListener(v->onBackPressed());
//        binding.send.setOnClickListener(v -> {
//           String content = binding.inputText.getText().toString();
//           if(!"".equals(content)){
//               Msg msg = new Msg(content,Msg.TYPE_SENT);
//               msgList.add(msg);
//               adapter.notifyItemInserted(msgList.size() - 1);
//               binding.msgRecyclerView.scrollToPosition(msgList.size() - 1);
//               binding.inputText.setText("");
//           }
//        });
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    public void sendMessage(){
        HashMap<String,Object> message = new HashMap<>();
//        message.put(Constants.KEY_SENDER_ID,);
    }
    private void init(){
        pref = new PreferenceManager(getApplicationContext());
        adapter = new MsgAdapter(msgList);
        binding.msgRecyclerView.setAdapter(adapter);
        database = FirebaseFirestore.getInstance();
    }
    private void loadReceiverDetails(){
        receiver = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.topBarName.setText(receiver.name);
    }

    private void setListeners(){
        binding.back.setOnClickListener(v->onBackPressed());
    }
}