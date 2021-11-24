package com.example.qq.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qq.databinding.FragmentFavBinding;


public class FavFragment extends Fragment {
    private FragmentFavBinding binding;
    public FavFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }
}