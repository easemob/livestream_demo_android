package com.easemob.livedemo.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by wei on 2016/5/27.
 */
public class LiveRoom implements Serializable {
    @SerializedName("liveroom_id")
    private String id;
    @SerializedName("title")
    private String name;
    @SerializedName("current_user_count")
    private int audienceNum;
    @SerializedName("cover_picture_url")
    private String cover;
    @SerializedName("chatroom_id")
    private String chatroomId;
    @SerializedName("anchor")
    private String anchorId;
    @SerializedName("desc")
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAudienceNum() {
        return audienceNum;
    }

    public void setAudienceNum(int audienceNum) {
        this.audienceNum = audienceNum;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
