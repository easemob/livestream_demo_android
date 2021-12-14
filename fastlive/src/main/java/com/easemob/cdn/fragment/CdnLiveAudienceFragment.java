package com.easemob.cdn.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.easemob.cdn.presenter.CdnAudiencePresenter;
import com.easemob.cdn.presenter.ICdnAudienceView;
import com.easemob.fastlive.FastLiveHelper;
import com.easemob.fastlive.FastPrefManager;
import com.easemob.fastlive.R;
import com.easemob.fastlive.stats.LocalStatsData;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.video.VideoCanvas;

/**
 * 观看视频直播的流程如下：
 * （1）初始化 RtcEngine。一般放置在程序入口处即可，见DemoApplication中的initAgora()方法，具体调用为{@link FastLiveHelper#init(Context, String)}
 * （2）设置频道场景。本demo中此逻辑在{@link FastLiveHelper#init(Context, String)}中，具体在{@link io.agora.rtc2.RtcEngine#setChannelProfile(int)},
 *      直播场景设置为{@link Constants#CHANNEL_PROFILE_LIVE_BROADCASTING}
 * （3）获取声网token。这个一般调用app server相关接口，从服务器获取。如果在声网console中设置为不校验token可以不进行此步。
 * （4）加入channel并设置角色。这里涉及到channel的生成，本demo中channel是从服务端随房间信息返回的。
 *      加入channel的调用方法为{@link FastLiveHelper#joinRtcChannel(int, String, int)}。
 *      设置用户角色方法{@link io.agora.rtc2.RtcEngine#setClientRole(int)} 。
 * （5）监听{@link IRtcEngineEventHandler#onRemoteVideoStateChanged(int, int, int, int)}方法，在state返回{@link Constants#REMOTE_VIDEO_STATE_STARTING}时，
 *      添加远端视图，调用方法为{@link io.agora.rtc2.RtcEngine#setupRemoteVideo(VideoCanvas)} 。
 *      声网官方API介绍（https://docs.agora.io/cn/live-streaming/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler.html#ac7144e0124c3d8f75e0366b0246fbe3b）
 *  注：调用{@link io.agora.rtc2.RtcEngine#setupRemoteVideo(VideoCanvas)}方法时声网官方API文档中有如下介绍：
 *      如果 App 不能事先知道对方的用户 ID，可以在 APP 收到 onUserJoined 事件时设置。如果启用了视频录制功能，
 *      视频录制服务会做为一个哑客户端加入频道，因此其他客户端也会收到它的 onUserJoined 事件，App 不应给它绑定视图（因为它不会发送视频流），
 *      如果 App 不能识别哑客户端，可以在 onFirstRemoteVideoDecoded 事件时再绑定视图。解除某个用户的绑定视图可以把 view 设置为空。
 *      退出频道后，SDK 会把远程用户的绑定关系清除掉。
 *
 *      这里需要注意的是{@link IRtcEngineEventHandler#onFirstRemoteVideoDecoded(int, int, int, int)}已在2.9.0版本后废弃，
 *      需要调用{@link IRtcEngineEventHandler#onRemoteVideoStateChanged(int, int, int, int)}。具体详见第（5）点用法。
 */
public class CdnLiveAudienceFragment extends CdnLiveBaseFragment implements ICdnAudienceView {
    public CdnAudiencePresenter presenter;
    private View loading;

    public CdnLiveAudienceFragment(CdnAudiencePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //将presenter与生命周期等关联
        if(this.presenter != null) {
            if(context instanceof AppCompatActivity) {
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
        Log.e(TAG, "onRtcWarning warn: "+warn);
    }

    @Override
    public void onRtcError(int err) {
        Log.e(TAG, "onRtcError err: "+err);
    }

    @Override
    public void onRtcJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.i("gaoyuan", "onRtcJoinChannelSuccess channel: "+channel + " uid: "+uid);
        try {
            FastPrefManager.getPreferences(mContext).edit().putInt(roomId, uid).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.presenter.runOnUI(new Runnable() {
            @Override
            public void run() {
                // startCdnPull();
            }
        });
    }

    /**
     * 根据声网官方API介绍（https://docs.agora.io/cn/live-streaming/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler.html#ac7144e0124c3d8f75e0366b0246fbe3b）
     * {@link IRtcEngineEventHandler#onFirstRemoteVideoDecoded(int, int, int, int)}已在2.9.0版本后弃用，官方给的建议是
     * 改用{@link IRtcEngineEventHandler#onRemoteVideoStateChanged(int, int, int, int)}回调中的{@link Constants#REMOTE_VIDEO_STATE_STARTING}
     * 或者{@link Constants#REMOTE_VIDEO_STATE_STARTING}。
     * 本地收到远端第一个视频帧并解码成功后，会触发该回调。有两种情况：
     * （1）远端用户首次上线后发送视频
     * （2）远端用户视频离线再上线后发送视频
     * @param uid       发生视频状态改变的远端用户 ID
     * @param state     远端视频流状态
     * @param reason    远端视频流状态改变的具体原因
     * @param elapsed   从本地用户调用 joinChannel 方法到发生本事件经历的时间，单位为 ms
     */
    @Override
    public void onRtcRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        Log.i("gaoyuan", "onRtcRemoteVideoStateChanged uid: "+uid + " state: "+state);
        if(state == Constants.REMOTE_VIDEO_STATE_STOPPED || state == Constants.REMOTE_VIDEO_STATE_FAILED) {
            if(this.presenter.isActive()) {
                this.presenter.runOnUI(()-> mVideoGridContainer.removeAllVideo());
            }
        }else {
            this.presenter.runOnUI(()-> helper.setupRemoteVideo(uid, mVideoGridContainer, true));
            this.presenter.runOnUI(new Runnable() {
                @Override
                public void run() {
                    // startCdnPull();
                }
            });
        }
    }

    private void startCdnPull() {
        helper.startPullCdn(mVideoGridContainer, this.uid, this.cdnUrl);
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
        if(state == Constants.CONNECTION_STATE_CONNECTING || state == Constants.CONNECTION_STATE_RECONNECTING) {
            if(this.presenter.isActive()) {
                this.presenter.runOnUI(()-> loading.setVisibility(View.VISIBLE));
            }
        }else if(state == Constants.CONNECTION_STATE_CONNECTED) {
            if(this.presenter.isActive()) {
                this.presenter.runOnUI(()-> loading.setVisibility(View.GONE));
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
        Log.i("gaoyuan", "uid: " + uid);
        rtcToken = token;
        if(isRenew) {
            renewToken(token);
        }else {
            presenter.getCdnUrl(channel);
            // joinRtcChannel(token);
        }
    }

    @Override
    public void onGetTokenFail(String message) {
        Log.e(TAG, "onGetTokenFail: "+message);
        joinRtcChannel(null);
    }

    @Override
    public void onGetCdnUrlSuccess(String cdnUrl) {
        Log.i(TAG, "onGetCdnUrlSuccess: " + cdnUrl);
        this.cdnUrl = cdnUrl;
        joinRtcChannel(rtcToken);
    }

    @Override
    public void onGetCdnUrlFail(String msg) {
        Log.e(TAG, "onGetCdnUrlFail: " + msg);
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

    public void setPresenter(CdnAudiencePresenter presenter) {
        this.presenter = presenter;
        if(presenter != null && mContext != null && !mContext.isFinishing()) {
            if(mContext instanceof AppCompatActivity) {
                ((AppCompatActivity) getContext()).getLifecycle().addObserver(presenter);
            }
            presenter.attachView(this);
        }
    }
}

