package com.example.assistant;

import com.google.gson.annotations.SerializedName;

public class Task {

    @SerializedName("plan_id")
    Long planId;

    @SerializedName("repeat")
    Repeat repeat;


    @SerializedName("name")
    String name;
    @SerializedName("estimate")
    int estimate;



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

    /* Не понятно как должно быть пока так */
    @SerializedName("task_category")
    Category taskCategory;



    /*  Обычная задача */
    public Task(String name, String description, int estimate, Category taskCategory, String startDate, String stopDate, String startTime, String stopTime) {
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
        this.planId = null;
        this.repeat = null;
    }
    /*


    // Задача с планом
    public Task(String name, String description, int estimate, Category taskCategory, String startDate, String stopDate, String startTime, String stopTime, String timezone, int status, int planId) {
        this.userId = 1;
        this.name = name;
        this.description = description;
        this.estimate = estimate;
        this.taskCategory = taskCategory;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.timezone = timezone;
        this.status = status;
        this.planId = planId;
    }

    public Task(String name, String description, int estimate, Category taskCategory, String startDate, String stopDate, String startTime, String stopTime, Repeat repeat) {
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
        this.repeat = repeat;
    }
    */
}
