package com.easemob.livedemo.ui.live;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.qiniu_sdk.LiveCameraView;
import com.easemob.qiniu_sdk.PushStreamHelper;
import com.easemob.qiniu_sdk.Util;
import com.easemob.livedemo.ui.live.fragment.LiveAnchorFragment;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.AudioSourceCallback;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

public class LiveAnchorActivity extends LiveBaseActivity implements LiveAnchorFragment.OnCameraListener {

    @BindView(R.id.container)
    LiveCameraView cameraView;
    //@BindView(R.id.img_bt_switch_light) ImageButton lightSwitch;
    //@BindView(R.id.img_bt_switch_voice) ImageButton voiceSwitch;

    private LiveAnchorFragment fragment;

    private PushStreamHelper streamHelper;
    private boolean isStartCamera;

    public static void actionStart(Context context, LiveRoom liveRoom) {
        Intent starter = new Intent(context, LiveAnchorActivity.class);
        starter.putExtra("liveroom", liveRoom);
        context.startActivity(starter);
    }

    public static void actionStart(Context context, LiveRoom liveRoom, String url) {
        Intent starter = new Intent(context, LiveAnchorActivity.class);
        starter.putExtra("liveroom", liveRoom);
        starter.putExtra("publishUrl", url);
        context.startActivity(starter);
    }

    //203138620012364216img_bt_close
    @Override
    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.em_activity_live_anchor);
        ButterKnife.bind(this);
        setFitSystemForTheme(false, android.R.color.transparent);
    }

    @Override
    protected void initView() {
        super.initView();
        coverImage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    protected void initData() {
        super.initData();
        initLiveEnv();
        initFragment();
    }

    private void initFragment() {
        fragment = (LiveAnchorFragment) getSupportFragmentManager().findFragmentByTag("live_anchor");
        if(fragment == null) {
            fragment = new LiveAnchorFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("liveroom", liveRoom);
            fragment.setArguments(bundle);
        }
        fragment.setOnCameraListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "live_anchor").commit();
    }

    public void initLiveEnv() {
        String publishUrl = getIntent().getStringExtra("publishUrl");
        streamHelper = PushStreamHelper.getInstance();
        streamHelper.initPublishVideo(cameraView, publishUrl);
    }

//    /**
//     * 关闭直播显示直播成果
//     */
//    @OnClick(R.id.img_bt_close)
//    void closeLive() {
//        //mEasyStreaming.stopRecording();
//        cameraView.onPause();
//        stopPreview();
//
//        if (!isStarted) {
//            finish();
//            return;
//        }
//        showConfirmCloseLayout();
//    }


    @Override
    protected void onPause() {
        super.onPause();
        streamHelper.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isStartCamera) {
            streamHelper.resume();
        }

    }

    @Override
    public void onStartCamera() {
        streamHelper.resume();
        isStartCamera = true;
    }

    @Override
    public void switchCamera() {
        streamHelper.switchCamera();
    }

    @Override
    public void onStopCamera() {
        streamHelper.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        streamHelper.destroy();
    }

}
