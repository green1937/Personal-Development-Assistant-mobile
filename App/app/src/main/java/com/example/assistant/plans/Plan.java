package com.example.assistant.plans;

import com.google.gson.annotations.SerializedName;

public class Plan {

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

    public Plan(int userId, String name, String details, String startDate, String stopDate) {
        this.userId = userId;
        this.name = name;
        this.details = details;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.status = 0;
    }
}

