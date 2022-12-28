package com.easemob.livedemo.data.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class LiveRoom extends BaseBean implements Serializable {
    @SerializedName(value = "liveroom_id", alternate = {"id"})
    private String id;
    private String name;
    @SerializedName("affiliations_count")
    private Integer audienceNum;
    private String cover;
    @SerializedName("chatroom_id")
    private String chatroomId;
    private String owner;
    private String description;
    @SerializedName("mobile_push_url")
    private String livePushUrl;
    @SerializedName("mobile_pull_url")
    private String livePullUrl;
    private String status;
    private ExtBean ext;
    private Integer maxusers;
    @SerializedName("affiliations")
    private List<MemberBean> members;
    //是否持续
    private boolean persistent;
    private String video_type;
    private boolean mute;
    private String channel;
    //@SerializedName("liveshow_id")
    //private String showId;

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
        return audienceNum == null ? 0 : audienceNum;
    }

    public void setAudienceNum(Integer audienceNum) {
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLivePushUrl() {
        return livePushUrl;
    }

    public void setLivePushUrl(String livePushUrl) {
        this.livePushUrl = livePushUrl;
    }

    public String getLivePullUrl() {
        return livePullUrl;
    }

    public void setLivePullUrl(String livePullUrl) {
        this.livePullUrl = livePullUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ExtBean getExt() {
        return ext;
    }

    public void setExt(ExtBean ext) {
        this.ext = ext;
    }

    public int getMaxusers() {
        return maxusers == null ? 0 : maxusers;
    }

    public void setMaxusers(Integer maxusers) {
        this.maxusers = maxusers;
    }

    public List<MemberBean> getMembers() {
        return members;
    }

    public void setMembers(List<MemberBean> members) {
        this.members = members;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public String getVideo_type() {
        return video_type;
    }

    public void setVideo_type(String video_type) {
        this.video_type = video_type;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public LinkedList<String> getMemberList() {
        if (members == null) {
            return null;
        }
        LinkedList<String> list = new LinkedList<>();
        for (int i = 0; i < members.size(); i++) {
            MemberBean memberBean = members.get(i);
            if (!memberBean.isOwner()) {
                list.add(memberBean.getMember());
            }
        }
        return list;
    }

    public boolean isLiving() {
        return !TextUtils.isEmpty(status) && TextUtils.equals(status, "ongoing");
    }

    public enum Type {
        live, vod, agora_speed_live, agora_cdn_live, agora_interaction_live, agora_vod
    }

    @Override
    public String toString() {
        return "LiveRoom{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", audienceNum=" + audienceNum +
                ", cover='" + cover + '\'' +
                ", chatroomId='" + chatroomId + '\'' +
                ", owner='" + owner + '\'' +
                ", description='" + description + '\'' +
                ", livePushUrl='" + livePushUrl + '\'' +
                ", livePullUrl='" + livePullUrl + '\'' +
                ", status='" + status + '\'' +
                ", ext=" + ext +
                ", maxusers=" + maxusers +
                ", members=" + members +
                ", persistent=" + persistent +
                ", video_type='" + video_type + '\'' +
                ", mute=" + mute +
                ", channel='" + channel + '\'' +
                '}';
    }
}
