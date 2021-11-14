package com.example.qq.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.qq.activities.LoginActivity;
import com.example.qq.adapter.ContactAdapter;
import com.example.qq.databinding.FragmentMessageBinding;
import com.example.qq.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    public FragmentMessageBinding messageBinding;
    public ContactAdapter contactAdapter;
    public List<Contact> contacts = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MessageFragment() {}

    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        messageBinding = FragmentMessageBinding.inflate(inflater, container, false);
        initContacts();
        View view = messageBinding.getRoot();
        messageBinding.contactsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Recycler view
        contactAdapter = new ContactAdapter(getContext(),contacts);
        messageBinding.contactsRecyclerview.setAdapter(contactAdapter);

        // logout
        messageBinding.logout.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("LOGOUT");
            builder.setTitle("Confirm logout?");
            builder.setPositiveButton("confirm", (dialog, which) ->{
                startActivity(new Intent(getActivity(),LoginActivity.class));
                dialog.dismiss();
            });
            builder.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        return view;
    }

    public void initContacts(){
        Contact a1 = new Contact("a1");
        Contact a2 = new Contact("a2");
        Contact a3 = new Contact("a3");
        Contact a4 = new Contact("a4");
        contacts.add(a1);
        contacts.add(a2);
        contacts.add(a3);
        contacts.add(a4);
    }
}