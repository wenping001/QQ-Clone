package com.example.qq.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qq.RecentConversationListener;
import com.example.qq.databinding.ItemRecentConversionBinding;
import com.example.qq.model.Msg;
import com.example.qq.model.User;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversationViewHolder> {

    private final List<Msg> msgList;
    private final RecentConversationListener recentConversationListener;

    public RecentConversationAdapter(List<Msg> msgList, RecentConversationListener recentConversationListener) {
        this.msgList = msgList;
        this.recentConversationListener = recentConversationListener;
    }

    private static Bitmap getConversationBitmap(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentConversionBinding itemRecentConversionBinding = ItemRecentConversionBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ConversationViewHolder(itemRecentConversionBinding, recentConversationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.setData(msgList.get(position));
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        private ItemRecentConversionBinding binding;
        private RecentConversationListener recentConversationListener;

        ConversationViewHolder(ItemRecentConversionBinding binding, RecentConversationListener recentConversationListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.recentConversationListener = recentConversationListener;
        }

        void setData(Msg msg) {
            binding.recentConversationImage.setImageBitmap(getConversationBitmap(msg.conversationImage));
            binding.conversationName.setText(msg.conversationName);
            binding.lastMessage.setText(msg.message);
            binding.recentConversation.setOnClickListener(v ->
            {
                User user = new User();
                user.id = msg.conversationId;
                user.name = msg.conversationName;
                user.image = msg.conversationImage;
                recentConversationListener.onClickConversation(user);
            });
        }
    }
}
