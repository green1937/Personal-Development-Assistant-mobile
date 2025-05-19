package com.example.assistant.model;

import com.google.gson.annotations.SerializedName;


public class Category {
    @SerializedName("id")
    int id;
    /*
    @SerializedName("title")
    String title;
    @SerializedName("color")
    String color;
    @SerializedName("user_id")
    int userId;
    @SerializedName("activate")
    boolean active;

    public Category(int id, String title, String color, int userId, boolean active) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.userId = userId;
        this.active = active;
    }*/

    // Для создания
    public Category(int id) {
        this.id = id;
    }
}
