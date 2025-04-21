package com.example.assistant;

import com.google.gson.annotations.SerializedName;


public class Record {

    @SerializedName("id")
    private int recordId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("assigned_day")
    private String assignedDay;

    @SerializedName("text")
    private String text;

    public Record(int recordId, int userId, String assignedDay, String text) {
        this.recordId = recordId;
        this.userId = userId;
        this.assignedDay = assignedDay;
        this.text = text;
    }
}
