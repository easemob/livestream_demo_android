package com.easemob.livedemo.ui.live;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.live.viewmodels.StreamViewModel;
import com.easemob.qiniu_sdk.LiveCameraView;
import com.easemob.qiniu_sdk.PushStreamHelper;
import com.easemob.livedemo.ui.live.fragment.LiveAnchorFragment;

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
        initViewModel();
    }

    private void initViewModel() {
        StreamViewModel viewModel = new ViewModelProvider(this).get(StreamViewModel.class);
        viewModel.getPublishUrl(liveRoom.getId());

        viewModel.getPublishUrlObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    getStreamUrlSuccess(data);
                }
            });
        });
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
        Log.e("TAG", "publishUrl = "+publishUrl);
        streamHelper = PushStreamHelper.getInstance();
        streamHelper.initPublishVideo(cameraView, publishUrl);
    }

    protected void getStreamUrlSuccess(String url) {
        Log.e("TAG", "url = "+url);
        streamHelper.setPublishUrl(url);
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
        //如果需要页面不可见后停止推流调用此方法
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
