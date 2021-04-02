package com.easemob.fastlive;


public class EngineConfig {
    // private static final int DEFAULT_UID = 0;
    // private int mUid = DEFAULT_UID;

    private String mChannelName;
    private boolean mShowVideoStats;
    private int mDimenIndex = FastConstants.DEFAULT_PROFILE_IDX;
    private int mMirrorLocalIndex;
    private int mMirrorRemoteIndex;
    private int mMirrorEncodeIndex;
    private boolean mIsLowLatency;//设置观众是否低延迟
    private String appId;
    // rtc configurations
    private boolean mVideoMuted;//主播是否停止推流
    private boolean mAudioMuted;//主播是否设置静音


    public int getVideoDimenIndex() {
        return mDimenIndex;
    }

    public void setVideoDimenIndex(int index) {
        mDimenIndex = index;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public void setChannelName(String mChannel) {
        this.mChannelName = mChannel;
    }

    public boolean ifShowVideoStats() {
        return mShowVideoStats;
    }

    public void setIfShowVideoStats(boolean show) {
        mShowVideoStats = show;
    }

    public int getMirrorLocalIndex() {
        return mMirrorLocalIndex;
    }

    public void setMirrorLocalIndex(int index) {
        mMirrorLocalIndex = index;
    }

    public int getMirrorRemoteIndex() {
        return mMirrorRemoteIndex;
    }

    public void setMirrorRemoteIndex(int index) {
        mMirrorRemoteIndex = index;
    }

    public int getMirrorEncodeIndex() {
        return mMirrorEncodeIndex;
    }

    public void setMirrorEncodeIndex(int index) {
        mMirrorEncodeIndex = index;
    }

    public boolean isLowLatency() {
        return mIsLowLatency;
    }

    public void setLowLatency(boolean mIsLowLatency) {
        this.mIsLowLatency = mIsLowLatency;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isVideoMuted() {
        return mVideoMuted;
    }

    public void setVideoMuted(boolean videoMuted) {
        this.mVideoMuted = videoMuted;
    }

    public boolean isAudioMuted() {
        return mAudioMuted;
    }

    public void setAudioMuted(boolean audioMuted) {
        this.mAudioMuted = audioMuted;
    }
}
