package com.example.hoanglong.capstonefpt.POJOs.APIresponses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hoanglong on 23-Jan-18.
 */

public class UserResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("code")
    private String code;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("position")
    private String position;

    @SerializedName("emailEDU")
    private String emailEdu;

    public UserResponse(int id, String code, String fullName, String position, String emailEdu) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.position = position;
        this.emailEdu = emailEdu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmailEdu() {
        return emailEdu;
    }

    public void setEmailEdu(String emailEdu) {
        this.emailEdu = emailEdu;
    }

}
