package com.example.qq.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qq.R;
import com.example.qq.activities.ChatActivity;
import com.example.qq.model.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private List<Contact> contactList;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        holder.layout.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),ChatActivity.class);
            intent.putExtra("username", holder.name.getText().toString().trim());
            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layout;
        ImageView avatar;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.contact_item);
            name = itemView.findViewById(R.id.contact_name);
            layout.setOnClickListener(v->{
            });
        }
    }
}
