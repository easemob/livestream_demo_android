package com.easemob.livedemo.data.model;

public class AttentionBean {
    //show time seconds,-1 means always
    private int showTime;
    //empty means cancel attention
    private String showContent;

    private boolean isAlert;

    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    public String getShowContent() {
        return showContent;
    }

    public void setShowContent(String showContent) {
        this.showContent = showContent;
    }

    public boolean isAlert() {
        return isAlert;
    }

    public void setAlert(boolean alert) {
        isAlert = alert;
    }

    @Override
    public String toString() {
        return "AttentionBean{" +
                "showTime=" + showTime +
                ", showContent='" + showContent + '\'' +
                ", isAlert=" + isAlert +
                '}';
    }
}