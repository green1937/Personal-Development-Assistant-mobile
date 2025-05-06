package com.example.assistant.tasks;

import com.example.assistant.wheel.Category;
import com.google.gson.annotations.SerializedName;

public class NewTask {
        @SerializedName("id")
        int id;

        @SerializedName("plan_id")
        Long planId;

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

        @SerializedName("task_category")
        Category taskCategory;


        public NewTask(int id, String name, String description, int estimate, Category taskCategory,
                       String startDate, String stopDate, String startTime,
                       String stopTime, Long planId) {
            this.id = id;
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
        }


}
