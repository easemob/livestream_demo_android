package com.easemob.livedemo.data.model;

import java.io.Serializable;

public class GiftBean implements Serializable {
    private String id;
    private User user;
    private int type;
    private String gift;
    private int num;
    private int resource;
    private String name;
    private boolean isChecked;
    private int value;
    private int leftTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getLeftTime() {
        return leftTime;
    }

    public void setLeftTime(int leftTime) {
        this.leftTime = leftTime;
    }

    @Override
    public String toString() {
        return "GiftBean{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", type=" + type +
                ", gift='" + gift + '\'' +
                ", num=" + num +
                ", resource=" + resource +
                ", name='" + name + '\'' +
                ", isChecked=" + isChecked +
                ", value=" + value +
                ", leftTime=" + leftTime +
                '}';
    }
}
