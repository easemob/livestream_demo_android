package com.easemob.livedemo.data.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String pwd;
    private String nickName;
    private int avatarDefaultResource;
    private String avatarUrl;
    private String birthday;
    private String gender;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getAvatarDefaultResource() {
        return avatarDefaultResource;
    }

    public void setAvatarDefaultResource(int avatarDefaultResource) {
        this.avatarDefaultResource = avatarDefaultResource;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", pwd='" + pwd + '\'' +
                ", nickName='" + nickName + '\'' +
                ", avatarDefaultResource=" + avatarDefaultResource +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", birthday='" + birthday + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
