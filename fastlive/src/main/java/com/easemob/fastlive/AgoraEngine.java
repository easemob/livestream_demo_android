package com.easemob.fastlive;

import android.content.Context;

import androidx.annotation.NonNull;

import com.easemob.fastlive.rtc.AgoraRtcHandler;
import com.easemob.fastlive.rtc.RtcEventHandler;

import io.agora.rtc2.Constants;
import io.agora.rtc2.RtcEngine;

public class AgoraEngine {
    private static final String TAG = AgoraEngine.class.getSimpleName();

    private RtcEngine mRtcEngine;
    private AgoraRtcHandler mRtcEventHandler = new AgoraRtcHandler();

    public AgoraEngine(@NonNull Context context, String appId) {
        try {
            mRtcEngine = RtcEngine.create(context, appId, mRtcEventHandler);
            mRtcEngine.enableVideo();
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableDualStreamMode(false);
            //mRtcEngine.setLogFile(UserUtil.rtcLogFilePath(application));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }


    public void registerRtcHandler(RtcEventHandler handler) {
        if (mRtcEventHandler != null) mRtcEventHandler.registerEventHandler(handler);
    }

    public void removeRtcHandler(RtcEventHandler handler) {
        if (mRtcEventHandler != null) mRtcEventHandler.removeEventHandler(handler);
    }

    public void release() {
        if (mRtcEngine != null) RtcEngine.destroy();
    }
}
