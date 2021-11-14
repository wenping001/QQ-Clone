package com.example.qq.dao;

import androidx.room.Dao;

import androidx.room.Insert;
import androidx.room.Query;

import com.example.qq.model.User;


@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE username = :username and password = :password" )
    User authenticateUser(String username, String password);
    @Query("SELECT * FROM user WHERE username = :username and email = :email " )
    User userIsRegistered(String email, String username);
    @Insert
    void insertUser(User user);
}

