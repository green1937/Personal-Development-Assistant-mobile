package com.example.assistant.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Storage {

    @SerializedName("entity_types")
    private ArrayList<String> EntityTypes;

    @SerializedName("status")
    private Long status;

    @SerializedName("text")
    private String text;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("stop_date")
    private String stopDate;

    @SerializedName("done_start_date")
    private String doneStartDate;

    @SerializedName("done_stop_date")
    private String doneStopDate;

    @SerializedName("is_repeated")
    private Boolean isRepeated;

    @SerializedName("belongs_to_plan")
    private Boolean belongsToPlan;

    @SerializedName("categories")
    private ArrayList<Integer> categories;

    @SerializedName("min_points")
    private Integer minPoints;

    @SerializedName("max_points")
    private Integer maxPoints;


    public Storage(ArrayList<String> entityTypes, Long status, String text, String startDate, String stopDate, String doneStartDate, String doneStopDate, Boolean isRepeated, Boolean belongsToPlan, ArrayList<Integer> categories, Integer minPoints, Integer maxPoints) {
        EntityTypes = entityTypes;
        this.status = status;
        this.text = text;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.doneStartDate = doneStartDate;
        this.doneStopDate = doneStopDate;
        this.isRepeated = isRepeated;
        this.belongsToPlan = belongsToPlan;
        this.categories = categories;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
    }


    public ArrayList<String> getEntityTypes() {
        return EntityTypes;
    }

    public void setEntityTypes(ArrayList<String> entityTypes) {
        EntityTypes = entityTypes;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getDoneStartDate() {
        return doneStartDate;
    }

    public void setDoneStartDate(String doneStartDate) {
        this.doneStartDate = doneStartDate;
    }

    public String getDoneStopDate() {
        return doneStopDate;
    }

    public void setDoneStopDate(String doneStopDate) {
        this.doneStopDate = doneStopDate;
    }

    public Boolean getRepeated() {
        return isRepeated;
    }

    public void setRepeated(Boolean repeated) {
        isRepeated = repeated;
    }

    public Boolean getBelongsToPlan() {
        return belongsToPlan;
    }

    public void setBelongsToPlan(Boolean belongsToPlan) {
        this.belongsToPlan = belongsToPlan;
    }

    public ArrayList<Integer> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Integer> categories) {
        this.categories = categories;
    }

    public Integer getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(Integer minPoints) {
        this.minPoints = minPoints;
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }
}
