package com.easemob.livedemo.ui.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.qiniu_sdk.LiveCameraView;
import com.easemob.qiniu_sdk.Util;
import com.easemob.livedemo.ui.live.fragment.LiveAnchorFragment;

import java.net.URI;
import java.util.UUID;

public class LiveAnchorActivity extends LiveBaseActivity implements LiveAnchorFragment.OnCameraListener {
    private static final String GENERATE_STREAM_TEXT = "https://api-demo.qnsdk.com/v1/live/stream/";

    private static final String TAG = LiveAnchorActivity.class.getSimpleName();
    @BindView(R.id.container)
    LiveCameraView cameraView;
    //@BindView(R.id.img_bt_switch_light) ImageButton lightSwitch;
    //@BindView(R.id.img_bt_switch_voice) ImageButton voiceSwitch;

    //protected UEasyStreaming mEasyStreaming;
    protected String rtmpPushStreamDomain = "publish3.cdn.ucloud.com.cn";

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
        ThreadManager.getInstance().runOnIOThread(()->{
            String publishUrl = Util.syncRequest(GENERATE_STREAM_TEXT + UUID.randomUUID());
            Log.e("TAG", "publishUrl = "+publishUrl);
            if(!TextUtils.isEmpty(publishUrl)) {
                // make an unauthorized GENERATE_STREAM_TEXT for effect
                try {
                    URI u = new URI(publishUrl);
                    publishUrl = String.format("rtmp://401.qbox.net%s?%s", u.getPath(), u.getRawQuery());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(!TextUtils.isEmpty(publishUrl)) {
                    String finalPublishUrl = publishUrl;
                    ThreadManager.getInstance().runOnMainThread(()-> {
                        Log.e("TAG", "finalPublishUrl = "+finalPublishUrl);
                        cameraView.init(finalPublishUrl);
                    });

                }
            }
        });


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
        cameraView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mEasyStreaming.onResume();
        cameraView.onResume();
    }

    @Override
    public void onStartCamera() {

    }

    @Override
    public void switchCamera() {
        cameraView.switchCamera();
    }

    @Override
    public void onStopCamera() {
        cameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.onDestroy();
    }
}
