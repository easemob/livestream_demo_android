package com.easemob.fastlive.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.easemob.fastlive.FastLiveHelper;
import com.easemob.fastlive.FastPrefManager;
import com.easemob.fastlive.R;
import com.easemob.fastlive.presenter.FastHostPresenter;
import com.easemob.fastlive.presenter.IFastHostView;
import com.easemob.fastlive.stats.LocalStatsData;
import com.easemob.fastlive.widgets.VideoGridContainer;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;

/**
 * 一、主播开始直播的流程如下：
 * （1）初始化 RtcEngine。一般放置在程序入口处即可，见DemoApplication中的initAgora()方法，具体调用为{@link FastLiveHelper#init(Context, String)}
 * （2）设置频道场景。本demo中此逻辑在{@link FastLiveHelper#init(Context, String)}中，具体在{@link io.agora.rtc2.RtcEngine#setChannelProfile(int)},
 *      直播场景设置为{@link Constants#CHANNEL_PROFILE_LIVE_BROADCASTING}
 * （3）获取声网token。这个一般调用app server相关接口，从服务器获取。如果在声网console中设置为不校验token可以不进行此步。
 * （4）加入channel并设置用户角色。这里涉及到channel的生成，本demo中channel是从服务端随房间信息返回的。
 *      加入channel的调用方法为{@link FastLiveHelper#joinRtcChannel(int, String, int)}，设置用户角色方法{@link io.agora.rtc2.RtcEngine#setClientRole(int)}
 * （5）在满足下面的开播的两个条件后，可以开始直播{@link FastLiveHelper#startBroadcast(VideoGridContainer, int)} 。
 *      上述方法中的有如下逻辑：（1）设置用户角色。（2）设置本地视图。
 * 二、开始直播的两个条件：
 * （1）加入直播间并将状态置为直播状态，回调方法为{@link #onStartBroadcast()}
 *  (2) 获取声网token(如果需要的话)成功，并加入channel，具体方法为{@link #joinRtcChannel(String)}
 */
public class FastLiveHostFragment extends FastLiveBaseFragment implements IFastHostView {
    private static final int RESTART_VIDEO = 10;
    private static final int MAX_RESTART_TIMES = 5;
    private static final int RETRY_TIME = 15000;
    private int restart_video_times;
    public FastHostPresenter presenter;
    private boolean isRoomReady = false;
    private boolean isTokenReady = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESTART_VIDEO :
                    restart_video_times++;
                    startBroadcast();
                    break;
            }
        }
    };
    private AlertDialog dialog;
    private AlertDialog localVideoDialog;

    public FastLiveHostFragment(FastHostPresenter presenter) {
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
        return R.layout.fragment_fast_live_host;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mVideoGridContainer = findViewById(R.id.vg_container);
        role = Constants.CLIENT_ROLE_BROADCASTER;
        int fastUid = FastPrefManager.getPreferences(mContext).getInt(roomId, -1);
        if(fastUid >= 1) {
            uid = fastUid;
        }
    }

    @Override
    public void getAgoraToken() {
        presenter.getFastToken(hxId, channel, hxAppkey, uid, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        helper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        helper.onPause();
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
    public void onRtcLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        Log.i(TAG, "onRtcLocalVideoStats stats: "+stats.sentBitrate + " sentFrameRate: "+stats.sentFrameRate);
    }

    @Override
    public void onRtcJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.i(TAG, "onRtcJoinChannelSuccess channel: "+channel + " uid: "+uid);
        try {
            FastPrefManager.getPreferences(mContext).edit().putInt(roomId, uid).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //加入channel后，开始进行直播
        synchronized (this) {
            if(!isTokenReady) {
                isTokenReady = true;
            }
            if(isRoomReady) {
                startBroadcast();
            }
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
        Log.e("TAG", "onRtcConnectionStateChanged state: " + state + " reason: "+reason);
        //如果是连接失败，那么需要进行重试
        if(state == Constants.CONNECTION_STATE_FAILED) {
            if(restart_video_times >= MAX_RESTART_TIMES) {
                handler.removeCallbacksAndMessages(null);
                this.presenter.runOnUI(() -> showDialog(R.string.fast_live_host_fail));
                return;
            }
            Log.e("TAG", "restart_video_times = "+restart_video_times);
            handler.sendEmptyMessageDelayed(RESTART_VIDEO, restart_video_times == 0 ? 0 : RETRY_TIME);
        }
    }

    @Override
    public void onRtcLocalVideoStateChanged(int localVideoState, int error) {
        Log.e("TAG", "onRtcLocalVideoStateChanged localVideoState: " + localVideoState + " error: "+error);
        // 出错原因不明确
        if(error == Constants.LOCAL_VIDEO_STREAM_ERROR_FAILURE) {
            this.presenter.runOnUI(() -> showRestartVideoDialog(R.string.fast_live_host_local_video_fail));
        }
    }

    @Override
    protected void onTokenPrivilegeWillExpire(String token) {
        presenter.getFastToken(hxId, channel, hxAppkey, uid, true);
    }

    @Override
    protected void onTokenExpired() {
        presenter.getFastToken(hxId, channel, hxAppkey, uid, true);
    }

    @Override
    public void onGetTokenSuccess(String token, int uid, boolean isRenew) {
        this.uid = uid;
        rtcToken = token;
        if(isRenew) {
            renewToken(token);
        }else {
            joinRtcChannel(token);
        }
    }

    @Override
    public void onGetTokenFail(String message) {
        Log.e(TAG, "onGetTokenFail: "+message);
        joinRtcChannel(null);
    }

    @Override
    public void onStartBroadcast() {
        synchronized (this) {
            if(!isRoomReady) {
                isRoomReady = true;
            }
            if(isTokenReady) {
                startBroadcast();
            }
        }
    }

    @Override
    public void switchCamera() {
        helper.switchCamera();
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

    /**
     * 开始广播
     */
    private void startBroadcast() {
        isRoomReady = false;
        isTokenReady = false;
        helper.startBroadcast(mVideoGridContainer, uid);
    }

    public void setPresenter(FastHostPresenter presenter) {
        this.presenter = presenter;
        if(presenter != null && mContext != null && !mContext.isFinishing()) {
            if(mContext instanceof AppCompatActivity) {
                ((AppCompatActivity) getContext()).getLifecycle().addObserver(presenter);
            }
            presenter.attachView(this);
        }
    }

    private void showDialog(int title) {
        if(this.presenter != null && this.presenter.isActive()) {
            if(dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage(title)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //退出房间
                            mContext.finish();
                        }
                    })
                    .setNegativeButton("重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.removeCallbacksAndMessages(null);
                            restart_video_times = 0;
                            handler.sendEmptyMessageDelayed(RESTART_VIDEO, 0);
                        }
                    })
                    .setCancelable(false)
                    .create();
            dialog.show();
        }
    }

    private void showRestartVideoDialog(int title) {
        if(this.presenter != null && this.presenter.isActive()) {
            if(localVideoDialog != null && localVideoDialog.isShowing()) {
                localVideoDialog.dismiss();
            }
            localVideoDialog = new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage(title)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startBroadcast();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //什么也不做
                        }
                    })
                    .setCancelable(false)
                    .create();
            localVideoDialog.show();
        }
    }
}

