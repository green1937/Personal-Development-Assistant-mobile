package com.example.assistant.model;

import com.google.gson.annotations.SerializedName;


public class Category {
    @SerializedName("id")
    int id;

    // Для создания
    public Category(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
