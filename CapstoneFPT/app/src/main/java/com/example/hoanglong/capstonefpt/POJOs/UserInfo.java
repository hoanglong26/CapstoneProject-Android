package com.example.hoanglong.capstonefpt.POJOs;

/**
 * Created by hoanglong on 23-Jan-18.
 */

public class UserInfo {
    private int id;
    private String code;
    private String name;
    private String role;


    public UserInfo(int id, String code, String name, String role) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.role = role;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
