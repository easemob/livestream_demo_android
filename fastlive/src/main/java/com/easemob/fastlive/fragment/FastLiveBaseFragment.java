package com.easemob.fastlive.fragment;

import android.os.Bundle;
import android.util.Log;

import com.easemob.fastlive.FastLiveHelper;
import com.easemob.fastlive.rtc.RtcEventHandler;
import com.easemob.fastlive.widgets.VideoGridContainer;

import io.agora.rtc.Constants;

public abstract class FastLiveBaseFragment extends FastBaseFragment implements RtcEventHandler {
    protected String channel;
    protected String roomId;
    protected String hxId;
    protected String hxAppkey;
    protected String rtcToken;
    protected int role = Constants.CLIENT_ROLE_AUDIENCE;
    protected int uid = 0;
    protected FastLiveHelper helper;
    public VideoGridContainer mVideoGridContainer;
    protected boolean preLeave;

    @Override
    protected void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            channel = bundle.getString("channel");
            roomId = bundle.getString("roomId");
            hxId = bundle.getString("hxId");
            hxAppkey = bundle.getString("hxAppkey");
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        helper = FastLiveHelper.getInstance();
        //注册事件回调
        helper.registerRtcHandler(this);
        //给config设置channel
        helper.getEngineConfig().setChannelName(channel);
    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    protected void initData() {
        super.initData();
//        if(BuildConfig.DEBUG) {
//            joinRtcChannel(null);
//        }else {
            getAgoraToken();
//        }
        initRtc();
    }

    public abstract void getAgoraToken();

    private void initRtc() {
        mVideoGridContainer.setStatsManager(FastLiveHelper.getInstance().getStatsManager());
    }

    @Override
    public void onRtcConnectionStateChanged(int state, int reason) {
        Log.i("fast", "onRtcConnectionStateChanged state: "+state + " reason: "+reason);
        //如果token无效
        if(reason == Constants.CONNECTION_CHANGED_INVALID_TOKEN) {
            Log.i("fast", "connection_changed_invalid_token");
        }else if(reason == Constants.CONNECTION_CHANGED_TOKEN_EXPIRED) {
            //token过期
            onTokenExpired();
        }
    }

    @Override
    public void onRtcTokenPrivilegeWillExpire(String token) {
        onTokenPrivilegeWillExpire(token);
    }

    /**
     * token失效
     */
    protected abstract void onTokenPrivilegeWillExpire(String token);

    /**
     * token失效
     */
    protected abstract void onTokenExpired();

    /**
     * 加入channel
     * @param token
     */
    public void joinRtcChannel(String token) {
        Log.i("fast", "joinRtcChannel");
        helper.joinRtcChannel(role, token, uid);
    }

    public void renewToken(String token) {
        helper.renewRtcToken(token);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("fast", "onDestroyView");
        if(!preLeave) {
            helper.onDestroy(this);
        }

    }
}

