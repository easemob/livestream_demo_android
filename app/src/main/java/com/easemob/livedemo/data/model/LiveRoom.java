package com.easemob.livedemo.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wei on 2016/5/27.
 */
public class LiveRoom implements Parcelable {
    private String id;
    private String name;
    private int audienceNum;
    private int cover;
    private String chatroomId;

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

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.audienceNum);
        dest.writeInt(this.cover);
        dest.writeString(this.chatroomId);
    }

    public LiveRoom() {
    }

    protected LiveRoom(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.audienceNum = in.readInt();
        this.cover = in.readInt();
        this.chatroomId = in.readString();
    }

    public static final Parcelable.Creator<LiveRoom> CREATOR = new Parcelable.Creator<LiveRoom>() {
        public LiveRoom createFromParcel(Parcel source) {
            return new LiveRoom(source);
        }

        public LiveRoom[] newArray(int size) {
            return new LiveRoom[size];
        }
    };
}
