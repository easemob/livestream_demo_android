package com.easemob.livedemo.data.model;

public class LoginBean {
    private String accessToken;
    private String userNickname;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "accessToken='" + accessToken + '\'' +
                ", userNickname='" + userNickname + '\'' +
                '}';
    }
}
