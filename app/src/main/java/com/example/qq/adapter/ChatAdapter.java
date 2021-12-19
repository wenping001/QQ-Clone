package com.example.qq.adapter;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qq.databinding.ItemContainerReceivedMessageBinding;
import com.example.qq.databinding.ItemContainerSendMessageBinding;
import com.example.qq.model.Msg;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<Msg> msgList;
    private final Bitmap receiverProfileImage;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<Msg> msgList, Bitmap receiverProfileImage, String senderId) {
        this.msgList = msgList;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d("ChatAdapter", "onCreateViewHolder: " + msgList.toString());

        if(viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(ItemContainerSendMessageBinding.
                    inflate(LayoutInflater.
                    from(parent.getContext()),
                    parent,
                    false));
        }
        else{
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.
                    inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder)holder).setData(msgList.get(position));
        }
        else{
            ((ReceivedMessageViewHolder)holder).setData(msgList.get(position),receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(msgList.get(position).senderId.equals(senderId)) {
           return VIEW_TYPE_SENT;
        }
        else{
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSendMessageBinding binding;

        SentMessageViewHolder(ItemContainerSendMessageBinding itemContainerSendMessageBinding){
            super(itemContainerSendMessageBinding.getRoot());
            binding = itemContainerSendMessageBinding;
        }

        void setData(Msg msg){
            binding.textMessage.setText(msg.message);
            binding.textDateTime.setText(msg.dateTime);
        }
    }
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
           super(itemContainerReceivedMessageBinding.getRoot());
           binding = itemContainerReceivedMessageBinding ;
        }
        void setData(Msg msg,Bitmap receiverAvatar){
            binding.textMessage.setText(msg.message);
            binding.textDateTime.setText(msg.dateTime);
            binding.imageProfile.setImageBitmap(receiverAvatar);
        }
    }
}
