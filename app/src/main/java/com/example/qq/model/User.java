package com.example.qq.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

//    @ColumnInfo(name = "gender" )
//    private enum gender

//    @ColumnInfo(name = "avatar_image")
//    private String avatar;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "password")
    private String Password;

    @PrimaryKey
    @ColumnInfo(name = "email")
    @NonNull
    private String Email;

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        Password = password;
        Email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
