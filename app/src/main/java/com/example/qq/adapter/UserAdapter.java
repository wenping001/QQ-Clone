package com.example.qq.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qq.activities.ChatActivity;
import com.example.qq.databinding.UserItemBinding;
import com.example.qq.model.User;
import com.example.qq.utilities.Constants;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private List<User> userList;
    private Context context;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserItemBinding userItemBinding = UserItemBinding
                .inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(userItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user= userList.get(position);
        holder.setUserData(user);
        holder.binding.userItem.setOnClickListener(v->{
            Intent intent =new Intent(context, ChatActivity.class);
            intent.putExtra(Constants.KEY_NAME,user.name);
            context.startActivity(intent);
        });

    }

    public Bitmap getUserImage(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        UserItemBinding binding;

        MyViewHolder(UserItemBinding contactItemBinding) {
            super(contactItemBinding.getRoot());
            binding = contactItemBinding;
        }

        void setUserData(User user){
            binding.username.setText(user.name);
            binding.userEmail.setText(user.email);
            binding.userImage.setImageBitmap(getUserImage(user.image));
        }
    }
}
