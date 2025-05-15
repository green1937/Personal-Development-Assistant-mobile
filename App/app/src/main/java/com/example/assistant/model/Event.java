package com.example.assistant.model;

import com.google.gson.annotations.SerializedName;


public class Event {
    //private Long id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("week_num")
    private int weekNum;

    @SerializedName("day_of_week")
    private int dayOfWeek;

    @SerializedName("name")
    private String eventName;

    @SerializedName("place")
    private String place;

    @SerializedName("format")
    private String eventFormat;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("stop_time")
    private String stopTime;


    public Event(int userId, int weekNum, int dayOfWeek, String eventName, String place, String eventFormat, String startTime, String stopTime) {
        this.userId = userId;
        this.weekNum = weekNum;
        this.dayOfWeek = dayOfWeek;
        this.eventName = eventName;
        this.place = place;
        this.eventFormat = eventFormat;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public int getUserId() {
        return userId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStopTime() {
        return stopTime;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int weekNum) {
        this.weekNum = weekNum;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getEventFormat() {
        return eventFormat;
    }

    public void setEventFormat(String eventFormat) {
        this.eventFormat = eventFormat;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }
}
