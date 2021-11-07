package com.example.qq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;


import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends BaseActivity{

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.nav_message);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessageFragment()).commit();
        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            switch(item.getItemId()){
                case R.id.nav_message:
                    fragment = new MessageFragment();
                    break;
                case R.id.nav_fav:
                    fragment = new FavFragment();
                    break;
                case R.id.nav_contacts:
                    fragment = new ContactsFragment();
                    break;
                case R.id.nav_view:
                    fragment = new ViewFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        });

    }
}
