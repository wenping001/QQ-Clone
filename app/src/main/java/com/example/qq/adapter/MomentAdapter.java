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

import com.example.qq.activities.DisplayImageActivity;
import com.example.qq.activities.HomePageActivity;
import com.example.qq.databinding.MomentsItemBinding;
import com.example.qq.model.Moment;
import com.example.qq.utilities.Constants;

import java.util.List;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.ViewHolder>{

    private List<Moment> moments;
    public static Context context;

    public MomentAdapter(Context context, List<Moment> moments) {
        this.context = context;
        this.moments = moments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MomentsItemBinding binding = MomentsItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Moment moment = moments.get(position);
        holder.setMomentData(moment);
    }

    @Override
    public int getItemCount() {
        return moments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        MomentsItemBinding binding;
        public ViewHolder(MomentsItemBinding momentsItemBinding) {
            super(momentsItemBinding.getRoot());
            binding = momentsItemBinding;
        }

        void setMomentData(Moment moment){
            binding.name.setText(moment.name);
            binding.postedTime.setText(moment.time);
            binding.postText.setText(moment.text);
            binding.avatar.setImageBitmap(getMomentImage(moment.image));
            binding.image.setImageBitmap(getMomentImage(moment.postImage));
        }

        public Bitmap getMomentImage(String encodedImage){
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
    }

}
