package com.example.qq.model;

public class Contact {
    private int imageId;
    private String name;
    private String date;
    private String last_message;

    public Contact(int imageId, String name, String date, String last_message) {
        this.imageId = imageId;
        this.name = name;
        this.date = date;
        this.last_message = last_message;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }
}
