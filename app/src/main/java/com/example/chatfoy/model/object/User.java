package com.example.chatfoy.model.object;

import java.io.Serializable;

public class User implements Serializable {
    private String userId;
    private String email;
    private String fullName;
    private String urlAvatar;
    private String sex;
    private boolean isOnline;

    public User() {
    }

    public User(String userId, String email, String fullName, String urlAvatar, String sex, boolean isOnline) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.urlAvatar = urlAvatar;
        this.sex = sex;
        this.isOnline = isOnline;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
