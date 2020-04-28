package com.easemob.livedemo.ui.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import butterknife.ButterKnife;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.LiveDataBus;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.live.viewmodels.StreamViewModel;
import com.easemob.qiniu_sdk.LiveVideoView;
import com.easemob.livedemo.ui.live.fragment.LiveAudienceFragment;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

public class LiveAudienceActivity extends LiveBaseActivity implements LiveAudienceFragment.OnLiveListener, LiveVideoView.OnVideoListener {
    private LiveAudienceFragment fragment;
    private LiveVideoView videoview;
    private View llStreamLoading;
    private String url;
    private boolean isPrepared;
    private StreamViewModel viewModel;

    public static void actionStart(Context context, LiveRoom liveRoom) {
        Intent intent = new Intent(context, LiveAudienceActivity.class);
        intent.putExtra("liveroom", liveRoom);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, LiveRoom liveRoom, String url) {
        Intent intent = new Intent(context, LiveAudienceActivity.class);
        intent.putExtra("liveroom", liveRoom);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.em_activity_live_audience);
        setFitSystemForTheme(false, android.R.color.transparent);
    }

    @Override
    protected void initView() {
        super.initView();
        url = getIntent().getStringExtra("url");
        llStreamLoading = findViewById(R.id.ll_stream_loading);
        videoview = findViewById(R.id.videoview);
        videoview.attachView();
    }

    @Override
    protected void initListener() {
        super.initListener();
        videoview.setOnVideoListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        videoview.setAvOptions();
        //提供了设置加载动画的接口，在播放器进入缓冲状态时，自动显示加载界面，缓冲结束后，自动隐藏加载界面
        videoview.setLoadingView(llStreamLoading);
        //在调用播放器的控制接口之前，必须先设置好播放地址.传入播放地址，可以是 /path/to/local.mp4 本地文件绝对路径，或 HLS URL，或 RTMP URL
        videoview.setVideoPath(url);

        fragment = (LiveAudienceFragment) getSupportFragmentManager().findFragmentByTag("live_audience");
        if(fragment == null) {
            fragment = new LiveAudienceFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("liveroom", liveRoom);
            fragment.setArguments(bundle);
        }
        fragment.setOnLiveListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "live_audience").commit();

        initViewModel();
    }


    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(StreamViewModel.class);
        viewModel.getPublishUrl(liveRoom.getId());

        viewModel.getPublishUrlObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    getStreamUrlSuccess(data);
                }
            });
        });

        LiveDataBus.get().with(DemoConstants.EVENT_ANCHOR_FINISH_LIVE, Boolean.class).observe(mContext, event -> {
            stopVideo();
        });

        LiveDataBus.get().with(DemoConstants.EVENT_ANCHOR_JOIN, Boolean.class).observe(mContext, event -> {
            videoview.setVisibility(View.VISIBLE);
            videoview.attachView();
            videoview.setOnVideoListener(this);
            videoview.setAvOptions();
            videoview.setLoadingView(llStreamLoading);
            viewModel.getPublishUrl(liveRoom.getId());
        });
    }

    protected void getStreamUrlSuccess(String url) {
        Log.e("TAG", "play url = "+url);
        videoview.setVideoPath(url);
    }

    /**
     * 开发者可以修改此处替换为自己的直播流
     */
    private void connectLiveStream(){

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG", "onResume");
        if(isPrepared) {
            videoview.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoview.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mContext.isFinishing()) {
            stopVideo();
        }
    }

    private void stopVideo() {
        Log.e("TAG", "stopVideo");
        videoview.stopPlayback();
        videoview.setVisibility(View.GONE);
        llStreamLoading.setVisibility(View.GONE);
    }

    @Override
    public void onLiveOngoing() {
        connectLiveStream();
    }

    @Override
    public void onLiveClosed() {
        showLongToast("直播间已关闭");
        finish();
    }

    @Override
    public void onPrepared(int preparedTime) {
        Log.e("TAG", "onPrepared");
        isPrepared = true;
        videoview.start();
    }

    @Override
    public void onCompletion() {
        Log.e("TAG", "onCompletion");
    }

    @Override
    public boolean onError(int errorCode) {
        Log.e("TAG", "onError = "+errorCode);
        return false;
    }

    @Override
    public void onVideoSizeChanged(int width, int height) {

    }

    @Override
    public void onStopVideo() {
        runOnUiThread(this::stopVideo);
    }
}
