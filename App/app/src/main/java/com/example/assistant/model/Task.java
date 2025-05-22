package com.example.assistant.model;

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


    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEstimate() {
        return estimate;
    }

    public void setEstimate(int estimate) {
        this.estimate = estimate;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public Category getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(Category taskCategory) {
        this.taskCategory = taskCategory;
    }
}
