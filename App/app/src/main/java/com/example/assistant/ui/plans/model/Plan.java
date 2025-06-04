package com.example.assistant.ui.plans.model;

import com.google.gson.annotations.SerializedName;

public class Plan {
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;

    @SerializedName("name")
    private String name;

    @SerializedName("details")
    private String details;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("stop_date")
    private String stopDate;

    @SerializedName("status")
    private int status;

    public Plan(int id, int userId, String name, String details, String startDate, String stopDate, int status) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.details = details;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStopDate() {
        return stopDate;
    }

    public void setStopDate(String stopDate) {
        this.stopDate = stopDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
