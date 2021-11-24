package com.example.qq.activities;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.example.qq.fragments.ContactsFragment;
import com.example.qq.fragments.FavFragment;
import com.example.qq.fragments.MessageFragment;
import com.example.qq.R;
import com.example.qq.fragments.ProfileFragment;
import com.example.qq.utilities.Constants;
import com.example.qq.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends BaseActivity{

    BottomNavigationView bottomNavigationView;
    private PreferenceManager pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = new PreferenceManager(getApplicationContext());
        getToken();
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
                    fragment = new ProfileFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        });

    }
    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    private void updateToken(String token){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dr = db.collection(Constants.KEY_COLLECTION_USERS)
                .document(pref.getString(Constants.KEY_USER_ID));
        dr.update(Constants.KEY_FCM_TOKEN,token)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Token updated.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e->Toast.makeText(this, "Error update token.", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
