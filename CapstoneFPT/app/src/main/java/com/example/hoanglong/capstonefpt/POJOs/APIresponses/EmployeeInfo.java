package com.example.hoanglong.capstonefpt.POJOs.APIresponses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hoanglong on 23-Jan-18.
 */

public class EmployeeInfo {
    @SerializedName("employee")
    private Employee emp;

    @SerializedName("scheduleList")
    private List<ScheduleResponse> scheduleList;

    public EmployeeInfo(Employee emp, List<ScheduleResponse> scheduleList) {
        this.emp = emp;
        this.scheduleList = scheduleList;
    }

    public Employee getEmp() {
        return emp;
    }

    public void setEmp(Employee emp) {
        this.emp = emp;
    }

    public List<ScheduleResponse> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<ScheduleResponse> scheduleList) {
        this.scheduleList = scheduleList;
    }
}
