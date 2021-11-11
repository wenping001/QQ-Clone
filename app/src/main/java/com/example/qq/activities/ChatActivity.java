package com.example.qq.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.qq.R;
import com.example.qq.adapter.MsgAdapter;
import com.example.qq.model.Msg;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends BaseActivity{

    private final List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
                initMsgs();
        inputText = findViewById(R.id.input_text);
        send = findViewById(R.id.send);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(v -> {
           String content = inputText.getText().toString();
           if(!"".equals(content)){
               Msg msg = new Msg(content,Msg.TYPE_SENT);
               msgList.add(msg);
               adapter.notifyItemInserted(msgList.size() - 1);
               msgRecyclerView.scrollToPosition(msgList.size() - 1);
               inputText.setText("");
           }
        });
    }

    private void initMsgs(){
        Msg msg1 = new Msg("Hello guy.",Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("Hello Who is that.",Msg.TYPE_SENT);
        msgList.add(msg2);
        Msg msg3 = new Msg("This is Tom. Nice talking to you.",Msg.TYPE_RECEIVED);
        msgList.add(msg3);
    }
}