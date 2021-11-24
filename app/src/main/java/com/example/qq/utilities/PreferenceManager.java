package com.example.qq.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private final SharedPreferences pref;

    public PreferenceManager(Context context) {
        pref = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    public void putBoolean(String key,Boolean value){
       SharedPreferences.Editor editor = pref.edit();
       editor.putBoolean(key,value);
       editor.apply();
    }
    public Boolean getBoolean(String key){
        return pref.getBoolean(key,false);
    }

    public void putString(String key,String value){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getString(String key){
        return pref.getString(key,"");
    }

    public void clear(){
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
