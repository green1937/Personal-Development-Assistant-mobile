package com.example.assistant.model;

import com.google.gson.annotations.SerializedName;

public class Register {

    @SerializedName("email")
    private String email;


    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("repeat_password")
    private String repeatPassword;

    @SerializedName("timezone")
    private String timezone;

    public Register(String email, String username, String password, String repeatPassword) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.timezone = "Asia/Krasnoyarsk";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
