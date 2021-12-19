package com.example.qq.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qq.UserListener;
import com.example.qq.activities.ChatActivity;
import com.example.qq.databinding.UserItemBinding;
import com.example.qq.model.User;
import com.example.qq.utilities.Constants;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private List<User> userList;
    private Context context;
    private UserListener userListener;

    public UserAdapter(Context context, List<User> userList,UserListener userListener) {
        this.context = context;
        this.userList = userList;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserItemBinding userItemBinding = UserItemBinding
                .inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(userItemBinding,userListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user= userList.get(position);
        holder.setUserData(user);
    }

    public Bitmap getUserImage(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        UserItemBinding binding;
        UserListener userListener;

        MyViewHolder(UserItemBinding contactItemBinding,UserListener onUserListener) {
            super(contactItemBinding.getRoot());
            this.userListener = onUserListener;
            binding = contactItemBinding;
        }

        void setUserData(User user){
            binding.conversationName.setText(user.name);
            binding.userEmail.setText(user.email);
            binding.recentConversationImage.setImageBitmap(getUserImage(user.image));
            binding.recentConversation.setOnClickListener(v -> UserAdapter.this.userListener.onUserClicked(user));
        }
    }
}
