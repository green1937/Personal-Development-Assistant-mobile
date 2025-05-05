package com.example.assistant.diary;

import com.google.gson.annotations.SerializedName;


public class NewRecord {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("assigned_day")
    private String assignedDay;

    @SerializedName("text")
    private String text;

    public NewRecord(int userId, String assignedDay, String text) {
        this.userId = userId;
        this.assignedDay = assignedDay;
        this.text = text;
    }
}
