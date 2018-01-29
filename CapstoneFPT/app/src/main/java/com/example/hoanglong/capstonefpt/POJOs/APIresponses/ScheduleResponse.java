package com.example.hoanglong.capstonefpt.POJOs.APIresponses;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.hoanglong.capstonefpt.POJOs.Schedule;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hoanglong on 23-Jan-18.
 */

public class ScheduleResponse implements Parcelable {
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

    @SerializedName("lecture")
    private String lecture;

    public ScheduleResponse() {
    }

    public ScheduleResponse(String date, String room, String courseName, String slot, String startTime, String endTime, String lecture) {
        this.date = date;
        this.room = room;
        this.courseName = courseName;
        this.slot = slot;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lecture = lecture;
    }

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
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

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScheduleResponse> CREATOR = new Creator<ScheduleResponse>() {
        @Override
        public ScheduleResponse createFromParcel(Parcel in) {
            ScheduleResponse instance = new ScheduleResponse();
            instance.startTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.endTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.courseName = ((String) in.readValue((String.class.getClassLoader())));
            instance.lecture = ((String) in.readValue((String.class.getClassLoader())));
            instance.room = ((String) in.readValue((String.class.getClassLoader())));
            instance.slot = ((String) in.readValue((String.class.getClassLoader())));
            instance.date = ((String) in.readValue((String.class.getClassLoader())));

            return instance;

        }

        @Override
        public ScheduleResponse[] newArray(int size) {
            return new ScheduleResponse[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeValue(startTime);
        dest.writeValue(endTime);
        dest.writeValue(courseName);
        dest.writeValue(lecture);
        dest.writeValue(room);
        dest.writeValue(slot);
        dest.writeValue(date);
    }
}
