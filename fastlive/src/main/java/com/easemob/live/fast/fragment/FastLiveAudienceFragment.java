package com.easemob.live.fast.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.easemob.live.rtc.RtcEventHandler;
import com.easemob.fastlive.R;

import com.easemob.live.FastLiveHelper;
import com.easemob.live.FastPrefManager;
import com.easemob.live.fast.presenter.FastAudiencePresenter;
import com.easemob.live.fast.presenter.IFastAudienceView;
import com.easemob.live.stats.LocalStatsData;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.video.VideoCanvas;

/**
 * The process of watching live video is as follows:
 * (1) Initialize RtcEngine. Generally, it can be placed at the entrance of the program. See the initAgora() method in DemoApplication. The specific call is {@link FastLiveHelper#init(Context, String)}
 * (2) Set the channel scene. This logic in this demo is in {@link FastLiveHelper#init(Context, String)}, specifically in {@link io.agora.rtc2.RtcEngine#setChannelProfile(int)},
 * Live scene set to {@link Constants#CHANNEL_PROFILE_LIVE_BROADCASTING}
 * (3) Obtain agora token. This generally calls the app server related interface and obtains it from the server. This step can be omitted if it is set to not verify the token in agora console.
 * (4) Join the channel and set the role. This involves the generation of the channel. In this demo, the channel is returned from the server with the room information.
 * The calling method to join a channel is {@link FastLiveHelper#joinRtcChannel(int, String, int)}.
 * Set user role method {@link io.agora.rtc2.RtcEngine#setClientRole(int)} .
 * (5) Monitor {@link IRtcEngineEventHandler#onRemoteVideoStateChanged(int, int, int, int)} method, when state returns {@link Constants#REMOTE_VIDEO_STATE_STARTING},
 * To add a remote view, the call method is {@link io.agora.rtc2.RtcEngine#setupRemoteVideo(VideoCanvas)} .
 * Introduction to the official API of agora (https://docs.agora.io/cn/live-streaming/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler.html#ac7144e0124c3d8f75e0366b0246fbe3b)
 * Note: When calling the {@link io.agora.rtc2.RtcEngine#setupRemoteVideo(VideoCanvas)} method, the official API documentation of agora has the following introduction:
 * If the app cannot know the user ID of the other party in advance, it can be set when the app receives the onUserJoined event. If video recording is enabled,
 * The video recording service will join the channel as a dumb client, so other clients will also receive its onUserJoined event, the app should not bind a view to it (because it will not send video streams),
 * If the App does not recognize the dumb client, you can bind the view in the onFirstRemoteVideoDecoded event. To unbind a user from a view can set the view to be empty.
 * After exiting the channel, the SDK will clear the binding relationship of the remote user.
 * <p>
 * It should be noted here that {@link IRtcEngineEventHandler#onFirstRemoteVideoDecoded(int, int, int, int)} has been deprecated after version 2.9.0,
 * Need to call {@link IRtcEngineEventHandler#onRemoteVideoStateChanged(int, int, int, int)}. For details, please refer to the usage of point (5).
 */
public class FastLiveAudienceFragment extends FastLiveBaseFragment implements IFastAudienceView {
    public FastAudiencePresenter presenter;
    private View loading;

    public FastLiveAudienceFragment(FastAudiencePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (this.presenter != null) {
            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) getContext()).getLifecycle().addObserver(presenter);
            }
            this.presenter.attachView(this);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fast_live_audience;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mVideoGridContainer = findViewById(R.id.vg_container);
        loading = findViewById(R.id.ll_stream_loading);
        role = Constants.CLIENT_ROLE_AUDIENCE;
    }

    @Override
    public void getAgoraToken() {
        this.presenter.getFastToken(hxId, channel, hxAppkey, uid, false);
    }

    @Override
    public void onRtcWarning(int warn) {
        Log.e(RtcEventHandler.TAG, "onRtcWarning warn: " + warn);
    }

    @Override
    public void onRtcError(int err) {
        Log.e(TAG, "onRtcError err: " + err);
    }

    @Override
    public void onRtcJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.i(TAG, "onRtcJoinChannelSuccess channel: " + channel + " uid: " + uid);
        try {
            FastPrefManager.getPreferences(mContext).edit().putInt(roomId, uid).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * According to the official API introduction of agora (https://docs.agora.io/cn/live-streaming/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler.html#ac7144e0124c3d8f75e0366b0246fbe3b)
     * {@link IRtcEngineEventHandler#onFirstRemoteVideoDecoded(int, int, int, int)} has been deprecated after version 2.9.0, the official suggestion is
     * Use {@link Constants#REMOTE_VIDEO_STATE_STARTING} in {@link IRtcEngineEventHandler#onRemoteVideoStateChanged(int, int, int, int)} callback instead
     * or {@link Constants#REMOTE_VIDEO_STATE_STARTING}.
     * This callback will be triggered when the first video frame from the remote end is received locally and decoded successfully. There are two cases:
     * (1) Send video after remote user goes online for the first time
     * (2) Send the video after the remote user video goes offline and then goes online again
     *
     * @param uid     the remote user ID of the video state change
     * @param state   remote video stream state
     * @param reason  The specific reason for the state change of the remote video stream
     * @param elapsed The time from when the local user calls the joinChannel method to the occurrence of this event, in ms
     */
    @Override
    public void onRtcRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        Log.i(TAG, "onRtcRemoteVideoStateChanged uid: " + uid + " state: " + state);
        if (state == Constants.REMOTE_VIDEO_STATE_STOPPED || state == Constants.REMOTE_VIDEO_STATE_FAILED) {
            if (this.presenter.isActive()) {
                this.presenter.runOnUI(() -> mVideoGridContainer.removeAllVideo());
            }
        } else {
            this.presenter.runOnUI(() -> helper.setupRemoteVideo(uid, mVideoGridContainer, true));
        }
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        Log.i(TAG, "onRtcStats");
        if (!helper.getStatsManager().isEnabled()) {
            return;
        }

        LocalStatsData data = (LocalStatsData) helper.getStatsManager().getStatsData(0);
        if (data == null) {
            return;
        }

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    @Override
    public void onRtcConnectionStateChanged(int state, int reason) {
        super.onRtcConnectionStateChanged(state, reason);
        if (state == Constants.CONNECTION_STATE_CONNECTING || state == Constants.CONNECTION_STATE_RECONNECTING) {
            if (this.presenter.isActive()) {
                this.presenter.runOnUI(() -> loading.setVisibility(View.VISIBLE));
            }
        } else if (state == Constants.CONNECTION_STATE_CONNECTED) {
            if (this.presenter.isActive()) {
                this.presenter.runOnUI(() -> loading.setVisibility(View.GONE));
            }
        }
    }

    @Override
    public void onRtcUserJoined(int uid, int elapsed) {
        Log.i(TAG, "onUserJoined->" + uid);
    }

    @Override
    public void onRtcUserOffline(int uid, int reason) {
        Log.i(TAG, "onUserJoined->" + uid);
    }

    @Override
    public void onGetTokenSuccess(String token, int uid, boolean isRenew) {
        this.uid = uid;
        Log.i(TAG, "uid: " + uid);
        rtcToken = token;
        if (isRenew) {
            renewToken(token);
        } else {
            joinRtcChannel(token);
        }
    }

    @Override
    public void onGetTokenFail(String message) {
        Log.e(TAG, "onGetTokenFail: " + message);
        joinRtcChannel(null);
    }

    @Override
    protected void onTokenPrivilegeWillExpire(String token) {
        this.presenter.getFastToken(hxId, channel, hxAppkey, uid, true);
    }

    @Override
    protected void onTokenExpired() {
        this.presenter.getFastToken(hxId, channel, hxAppkey, uid, true);
    }

    @Override
    public void onLiveClosed() {

    }

    @Override
    public void onLeaveChannel() {
        preLeave = true;
        helper.onDestroy(this);
    }

    @Override
    public Context context() {
        return mContext;
    }

    public void setPresenter(FastAudiencePresenter presenter) {
        this.presenter = presenter;
        if (presenter != null && mContext != null && !mContext.isFinishing()) {
            if (mContext instanceof AppCompatActivity) {
                ((AppCompatActivity) getContext()).getLifecycle().addObserver(presenter);
            }
            presenter.attachView(this);
        }
    }
}

