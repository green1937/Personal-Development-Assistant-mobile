package com.example.assistant.ui.home.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Repeat implements Serializable {

    @SerializedName("repeat_interval")
    int repeatInterval;

    @SerializedName("term")
    String term;

    @SerializedName("days")
    int[] days;

    @SerializedName("start")
    String start;

    @SerializedName("end")
    String stop;

    @SerializedName("number_of_repeats")
    int numberOfRepeat;


    public Repeat(int repeatInterval, String term, int[] days, String start, String stop, int numberOfRepeat) {
        this.repeatInterval = repeatInterval;
        this.term = term;
        this.days = days;
        this.start = start;
        this.stop = stop;
        this.numberOfRepeat = numberOfRepeat;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int[] getDays() {
        return days;
    }

    public void setDays(int[] days) {
        this.days = days;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public int getNumberOfRepeat() {
        return numberOfRepeat;
    }

    public void setNumberOfRepeat(int numberOfRepeat) {
        this.numberOfRepeat = numberOfRepeat;
    }
}
