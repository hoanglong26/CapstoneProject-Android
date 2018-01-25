package com.example.hoanglong.capstonefpt.POJOs.APIresponses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hoanglong on 23-Jan-18.
 */

public class ScheduleResponse {
    @SerializedName("date")
    private String date;

    @SerializedName("room")
    private String room;

    @SerializedName("courseName")
    private String courseName;

    @SerializedName("slot")
    private String slot;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;


    public ScheduleResponse(String date, String room, String courseName, String slot, String startTime, String endTime) {
        this.date = date;
        this.room = room;
        this.courseName = courseName;
        this.slot = slot;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
