package com.easemob.livedemo.ui.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.LiveDataBus;
import com.easemob.livedemo.common.OnConfirmClickListener;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.data.model.ExtBean;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.model.LiveRoomUrlBean;
import com.easemob.livedemo.ui.live.viewmodels.LivingViewModel;
import com.easemob.livedemo.ui.live.viewmodels.StreamViewModel;
import com.easemob.livedemo.ui.other.fragment.SimpleDialogFragment;
import com.easemob.qiniu_sdk.LiveVideoView;
import com.easemob.livedemo.ui.live.fragment.LiveAudienceFragment;
import com.pili.pldroid.player.PLOnErrorListener;

import java.util.Map;

public class LiveAudienceActivity extends LiveBaseActivity implements LiveAudienceFragment.OnLiveListener, LiveVideoView.OnVideoListener {
    private static final int RESTART_VIDEO = 10;
    private static final int MAX_RESTART_TIMES = 5;
    private LiveAudienceFragment fragment;
    private LiveVideoView videoview;
    private View llStreamLoading;
    private String url;
    private boolean isPrepared;
    private StreamViewModel viewModel;
    private int videoWidth;
    private int videoHeight;
    private LivingViewModel livingViewModel;
    private int restart_video_times;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESTART_VIDEO :
                    restart_video_times++;
                    startVideo();
                    break;
            }
        }
    };

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
        livingViewModel = new ViewModelProvider(this).get(LivingViewModel.class);

        viewModel.getPlayUrlObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoomUrlBean>() {
                @Override
                public void onSuccess(LiveRoomUrlBean data) {
                    getStreamUrlSuccess(data.getData());
                }
            });
        });

        livingViewModel.getRoomDetailObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>() {
                @Override
                public void onSuccess(LiveRoom data) {
                    liveRoom = data;
                    getStreamUrl();
                }
            });
        });

        LiveDataBus.get().with(DemoConstants.EVENT_ANCHOR_FINISH_LIVE, Boolean.class).observe(mContext, event -> {
            //只有非点播模式下，才会去关闭播放器
            if(liveRoom != null
                    && !TextUtils.isEmpty(liveRoom.getVideo_type())
                    && !liveRoom.getVideo_type().equalsIgnoreCase(LiveRoom.Type.vod.name())) {
                stopVideo();
            }
        });

        LiveDataBus.get().with(DemoConstants.NETWORK_CONNECTED, Boolean.class).observe(this, event -> {
            if(event != null && event && !isPrepared) {
                Log.e("TAG", "断网重连后");
                if(isPrepared) {
                    videoview.start();
                }else {
                    startVideo();
                }

            }
        });

        LiveDataBus.get().with(DemoConstants.EVENT_ANCHOR_JOIN, Boolean.class).observe(mContext, event -> {
            Log.e("TAG", "主播加入");
            startVideo();
        });

        LiveDataBus.get().with(DemoConstants.LIVING_STATUS, String.class).observe(mContext, event -> {
            Log.e("TAG", "live status = "+event);
            if(DemoHelper.isLiving(event) && !isPrepared) {
                startVideo();
            }
        });

        livingViewModel.getLiveRoomDetails(liveRoom.getId());
    }

    private void getStreamUrl() {
        if(liveRoom == null) {
            return;
        }
        if(DemoHelper.isLiving(liveRoom.getStatus())) {
            String videoType = liveRoom.getVideo_type();
            if(!TextUtils.isEmpty(videoType) && videoType.equalsIgnoreCase(LiveRoom.Type.vod.name())) {
                ExtBean ext = liveRoom.getExt();
                if(ext != null && ext.getPlay() != null && ext.getPlay() != null && ext.getPlay().size() > 0) {
                    //隐藏背景图
                    coverImage.setVisibility(View.GONE);
                    //设置videoView模式为适应父布局
                    videoview.setDisplayFitParent();
                    String playUrl = getPlayUrl(ext.getPlay());
                    getStreamUrlSuccess(playUrl);
                }else {
                    viewModel.getPlayUrl(liveRoom.getId());
                }
            }else {
                viewModel.getPlayUrl(liveRoom.getId());
            }
        }
    }

    /**
     * 选择相应协议的拉流地址
     * @param mapUrl
     * @return
     */
    private String getPlayUrl(Map<String, String> mapUrl) {
        if(mapUrl.containsKey("m3u8")) {
            return mapUrl.get("m3u8");
        }
        return mapUrl.values().iterator().next();
    }

    private void startVideo() {
        Log.e("TAG", "startVideo");
        videoview.attachView();
        videoview.setOnVideoListener(this);
        videoview.setAvOptions();
        videoview.setLoadingView(llStreamLoading);
        getStreamUrl();
    }

    protected void getStreamUrlSuccess(String url) {
        this.url = url;
        Log.e("TAG", "play url = "+url);
        videoview.setVideoPath(this.url);
    }

    /**
     * 开发者可以修改此处替换为自己的直播流
     * @param data
     */
    private void connectLiveStream(LiveRoom data){

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
        isPrepared = false;
        videoview.stopPlayback();
        videoview.setVisibility(View.GONE);
        llStreamLoading.setVisibility(View.GONE);
    }

    @Override
    public void onLiveOngoing(LiveRoom data) {
        connectLiveStream(data);
    }

    @Override
    public void onLiveClosed() {
        showLongToast("直播间已关闭");
        finish();
    }

    @Override
    public void onPrepared(int preparedTime) {
        Log.e("TAG", "onPrepared");
        videoview.setVisibility(View.VISIBLE);
        isPrepared = true;
        videoview.start();
        restart_video_times = 0;
    }

    @Override
    public void onCompletion() {
        Log.e("TAG", "onCompletion");
        if(liveRoom != null) {
            String videoType = liveRoom.getVideo_type();
            if(!TextUtils.isEmpty(videoType) && videoType.equalsIgnoreCase(LiveRoom.Type.vod.name())) {
                stopVideo();
                startVideo();
            }
        }
    }

    @Override
    public boolean onError(int errorCode) {
        Log.e("TAG", "onError = "+errorCode);
        if(errorCode == PLOnErrorListener.ERROR_CODE_OPEN_FAILED || errorCode == PLOnErrorListener.ERROR_CODE_IO_ERROR) {
            //如果播放器打开失败，则轮询5次，5次后弹框结束页面
            if(restart_video_times >= MAX_RESTART_TIMES) {
                runOnUiThread(() -> showDialogFragment(R.string.em_live_open_video_fail_title));
                return false;
            }
            Log.e("TAG", "restart_video_times = "+restart_video_times);
            handler.sendEmptyMessageDelayed(RESTART_VIDEO, restart_video_times == 0 ? 0 : 15000);
        }
        return false;
    }

    @Override
    public void onVideoSizeChanged(int width, int height) {
        //正常的直播不进行视频尺寸的调节
        Log.e("TAG", "width = "+width + " height = "+height);
    }

    @Override
    public void onStopVideo() {
        //runOnUiThread(()-> showDialogFragment(R.string.em_live_disconnect_title));
    }

    private void showDialogFragment(int title) {
        DialogFragment fragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag("disconnected");
        if(fragment != null && fragment.isAdded()) {
            return;
        }
        fragment = new SimpleDialogFragment.Builder(mContext)
                .setTitle(title)
                .setConfirmButtonTxt(R.string.em_live_dialog_quit_btn_title)
                .setOnConfirmClickListener(new OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, Object bean) {
                        stopVideo();
                        finish();
                    }
                })
                .build();
        fragment.show(getSupportFragmentManager(), "disconnected");
    }
}
