package com.example.qq;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.qq.dao.UserDao;
import com.example.qq.model.User;

@Database(entities = {User.class},exportSchema = false,version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "qq_db";
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
       if(instance == null){
           instance = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,DB_NAME).allowMainThreadQueries().build();
       }
       return instance;
    }
    public abstract UserDao userDao();
}
