package com.example.assistant.tasks;

import com.example.assistant.model.Category;
import com.google.gson.annotations.SerializedName;

public class Task {
    @SerializedName("plan_id")
    Long planId;

    @SerializedName("name")
    String name;

    @SerializedName("estimate")
    int estimate;

    @SerializedName("repeat")
    Repeat repeat;

    @SerializedName("status")
    int status;

    @SerializedName("timezone")
    String timezone;

    @SerializedName("user_id")
    int userId;

    @SerializedName("description")
    String description;

    @SerializedName("start_date")
    String startDate;

    @SerializedName("stop_date")
    String stopDate;

    @SerializedName("start_time")
    String startTime;

    @SerializedName("stop_time")
    String stopTime;

    @SerializedName("task_category")
    Category taskCategory;



    /*  Задача с повтором */
    public Task(String name, String description, int estimate, Category taskCategory, String startDate, String stopDate, String startTime, String stopTime, Long planId, Repeat repeat) {
        this.userId = 1;
        this.name = name;
        this.description = description;
        this.estimate = estimate;
        this.taskCategory = taskCategory;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.timezone = "Asia/Krasnoyarsk";
        this.status = 0;
        this.planId = planId;
        this.repeat = repeat;
    }

}
