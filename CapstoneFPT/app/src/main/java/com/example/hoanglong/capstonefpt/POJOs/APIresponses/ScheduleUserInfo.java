package com.example.hoanglong.capstonefpt.POJOs.APIresponses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hoanglong on 23-Jan-18.
 */

public class ScheduleUserInfo {
    @SerializedName("user")
    private UserResponse emp;

    @SerializedName("scheduleList")
    private List<ScheduleResponse> scheduleList;

    public ScheduleUserInfo(UserResponse emp, List<ScheduleResponse> scheduleList) {
        this.emp = emp;
        this.scheduleList = scheduleList;
    }

    public UserResponse getEmp() {
        return emp;
    }

    public void setEmp(UserResponse emp) {
        this.emp = emp;
    }

    public List<ScheduleResponse> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<ScheduleResponse> scheduleList) {
        this.scheduleList = scheduleList;
    }
}
