package com.easemob.livedemo.data.model;

public class AgoraTokenBean {
    private String code;
    private String accessToken;
    private double expireTime;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public double getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(double expireTime) {
        this.expireTime = expireTime;
    }
}

