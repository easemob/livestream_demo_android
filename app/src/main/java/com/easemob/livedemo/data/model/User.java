package com.easemob.livedemo.data.model;

import com.hyphenate.chat.EMContact;

import java.io.Serializable;

/**
 * Created by wei on 2017/2/14.
 */

public class User implements Serializable {
    private String username;
    private String nick;
    private String avatar;
    private int avatarResource;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNick() {
        return nick;
    }

    public String getNickname() {
        return nick;
    }

    public void setNickname(String nick) {
        this.nick = nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAvatarResource() {
        return avatarResource;
    }

    public void setAvatarResource(int avatarResource) {
        this.avatarResource = avatarResource;
    }
}
