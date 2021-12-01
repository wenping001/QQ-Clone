package com.example.qq.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qq.R;
import com.example.qq.databinding.FragmentFavBinding;
import com.example.qq.databinding.FragmentProfileBinding;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.PreferenceManager;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private PreferenceManager pref;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        pref = new PreferenceManager(getActivity().getApplicationContext());
        loadUserDetail();
        return view;
    }
    private void loadUserDetail(){
        binding.name.setText(pref.getString(Constants.KEY_NAME));
        byte[] decodedString = Base64.decode(pref.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        binding.avatar.setImageBitmap(decodedByte);
    }
}