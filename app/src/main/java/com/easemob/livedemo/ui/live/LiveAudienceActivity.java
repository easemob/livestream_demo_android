package com.easemob.livedemo.ui.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.widget.Toast;

import butterknife.ButterKnife;

import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.qiniu.LiveVideoView;
import com.easemob.livedemo.ui.live.fragment.LiveAudienceFragment;

import java.io.File;
import java.util.Random;

public class LiveAudienceActivity extends LiveBaseActivity implements LiveAudienceFragment.OnLiveListener {
    String rtmpPlayStreamUrl = "rtmp://vlive3.rtmp.cdn.ucloud.com.cn/ucloud/";
    volatile boolean isSteamConnected;
    volatile boolean isReconnecting;
    Thread reconnectThread;
    private LiveAudienceFragment fragment;
    private LiveVideoView videoview;

    public static void actionStart(Context context, LiveRoom liveRoom) {
        Intent intent = new Intent(context, LiveAudienceActivity.class);
        intent.putExtra("liveroom", liveRoom);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.em_activity_live_audience);
        ButterKnife.bind(this);
        setFitSystemForTheme(false, android.R.color.transparent);
    }

    @Override
    protected void initView() {
        super.initView();
        videoview = findViewById(R.id.videoview);
        videoview.attachView();
    }

    @Override
    protected void initListener() {
        super.initListener();

    }

    @Override
    protected void initData() {
        super.initData();
        videoview.setAvOptions();
        File file = getExternalFilesDir("");
        videoview.setVideoPath(file.getAbsolutePath() + "/d.mp4");
        videoview.start();

        fragment = (LiveAudienceFragment) getSupportFragmentManager().findFragmentByTag("live_audience");
        if(fragment == null) {
            fragment = new LiveAudienceFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("liveroom", liveRoom);
            fragment.setArguments(bundle);
        }
        fragment.setOnLiveListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "live_audience").commit();
    }

    /**
     * 开发者可以修改此处替换为自己的直播流
     */
    private void connectLiveStream(){

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 重连到直播server
     */
    private void reconnect(){
        if(isSteamConnected || isReconnecting)
            return;
        if(reconnectThread != null &&reconnectThread.isAlive())
            return;

        reconnectThread = new Thread(){
            @Override public void run() {
                while (!isFinishing() && !isSteamConnected){
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if(!isReconnecting) {
                                isReconnecting = true;
                                connectLiveStream();
                            }
                            //mVideoView.setVideoPath(liveRoom.getLivePullUrl());
                        }
                    });
                    try {
                        // TODO 根据reconnect次数动态改变sleep时间
                        Thread.sleep(3000 + new Random().nextInt(3000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        };
        reconnectThread.setDaemon(true);
        reconnectThread.start();
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
}
