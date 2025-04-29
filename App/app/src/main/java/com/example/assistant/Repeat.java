package com.example.assistant;

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
}
