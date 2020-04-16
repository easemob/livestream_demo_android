package com.easemob.livedemo.ui.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ucloud.AVOption;
import com.easemob.livedemo.ucloud.LiveCameraView;
import com.easemob.livedemo.ui.live.fragment.LiveAnchorFragment;
import com.ucloud.ulive.UFilterProfile;
import com.ucloud.ulive.UNetworkListener;
import com.ucloud.ulive.UStreamStateListener;
import com.ucloud.ulive.UVideoProfile;

public class LiveAnchorActivity extends LiveBaseActivity implements LiveAnchorFragment.OnCameraListener {
    private static final String TAG = LiveAnchorActivity.class.getSimpleName();
    @BindView(R.id.container)
    LiveCameraView cameraView;
    //@BindView(R.id.img_bt_switch_light) ImageButton lightSwitch;
    //@BindView(R.id.img_bt_switch_voice) ImageButton voiceSwitch;

    //protected UEasyStreaming mEasyStreaming;
    protected String rtmpPushStreamDomain = "publish3.cdn.ucloud.com.cn";
    //private LiveSettings mSettings;
    //private UStreamingProfile mStreamingProfile;
    //UEasyStreaming.UEncodingType encodingType;

    boolean isStarted;

    private AVOption mAVOption;

    private LiveAnchorFragment fragment;

    public static void actionStart(Context context, LiveRoom liveRoom) {
        Intent starter = new Intent(context, LiveAnchorActivity.class);
        starter.putExtra("liveroom", liveRoom);
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
        initFragment();
        initLiveEnv();
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
        mAVOption = new AVOption();
        mAVOption.streamUrl = liveRoom.getLivePushUrl();
        mAVOption.videoFilterMode = UFilterProfile.FilterMode.GPU;
        mAVOption.videoCodecType = UVideoProfile.CODEC_MODE_HARD;
        mAVOption.videoCaptureOrientation = UVideoProfile.ORIENTATION_PORTRAIT;
        mAVOption.videoFramerate = 20;
        mAVOption.videoBitrate = UVideoProfile.VIDEO_BITRATE_NORMAL;
        mAVOption.videoResolution = UVideoProfile.Resolution.RATIO_AUTO.ordinal();
    }

    private void startPreview() {
//        cameraView.init(mAVOption);
    }

    private void stopPreview() {
        cameraView.stopRecordingAndDismissPreview();
    }

    @Override
    public void onBackPressed() {
        //mEasyStreaming.();
        stopPreview();
        super.onBackPressed();
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
        //mEasyStreaming.onPause();
        cameraView.onPause();
        stopPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mEasyStreaming.onResume();
        startPreview();
        if (isStarted) {
            cameraView.startRecording();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mEasyStreaming.onDestroy();
        try {
            cameraView.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    UStreamStateListener mStreamStateListener = new UStreamStateListener() {
        //stream state
        @Override public void onStateChanged(UStreamStateListener.State state, Object o) {
        }

        @Override public void onStreamError(UStreamStateListener.Error error, Object extra) {
            switch (error) {
                case IOERROR:
                    if (isStarted && cameraView.isPreviewed()) {
                        LiveCameraView.getInstance().restart();
                    }
                    break;
            }
        }
    };

    UNetworkListener mNetworkListener = new UNetworkListener() {
        @Override public void onNetworkStateChanged(State state, Object o) {
            switch (state) {
                case NETWORK_SPEED:
                    break;
                case PUBLISH_STREAMING_TIME:
                    break;
                case DISCONNECT:
                    break;
                case RECONNECT:
                    //网络重新连接
                    if (isStarted && cameraView.isPreviewed()) {
                        LiveCameraView.getInstance().restart();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onStartCamera() {
        cameraView.startRecording();
        isStarted = true;
        cameraView.addStreamStateListener(mStreamStateListener);
        cameraView.addNetworkListener(mNetworkListener);
    }

    @Override
    public void switchCamera() {
        cameraView.switchCamera();
    }

    @Override
    public void onStopCamera() {
        cameraView.onPause();
        stopPreview();

        if (!isStarted) {
            finish();
        }
    }
}
