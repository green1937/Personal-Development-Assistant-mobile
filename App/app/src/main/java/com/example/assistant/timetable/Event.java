package com.example.assistant.timetable;

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
}
