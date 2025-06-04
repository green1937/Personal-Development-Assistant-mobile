package com.example.assistant.ui.diary.model;

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


    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAssignedDay() {
        return assignedDay;
    }

    public void setAssignedDay(String assignedDay) {
        this.assignedDay = assignedDay;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
