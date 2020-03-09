package com.easemob.livedemo.ui.live;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.widget.Toast;

import butterknife.ButterKnife;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.live.fragment.LiveAudienceFragment;
import com.ucloud.uvod.UMediaProfile;
import com.ucloud.uvod.UPlayerStateListener;
import com.ucloud.uvod.widget.UVideoView;
import java.util.Random;

public class LiveAudienceActivity extends LiveBaseActivity implements UPlayerStateListener, LiveAudienceFragment.OnLiveListener {
    String rtmpPlayStreamUrl = "rtmp://vlive3.rtmp.cdn.ucloud.com.cn/ucloud/";
    private UVideoView mVideoView;
    private UMediaProfile profile;
    volatile boolean isSteamConnected;
    volatile boolean isReconnecting;
    Thread reconnectThread;
    private LiveAudienceFragment fragment;

    @Override
    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.em_activity_live_audience);
        ButterKnife.bind(this);
        setFitSystemForTheme(false, android.R.color.transparent);
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = (UVideoView) findViewById(R.id.videoview);
    }

    @Override
    protected void initListener() {
        super.initListener();

    }

    @Override
    protected void initData() {
        super.initData();
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
        profile = new UMediaProfile();
        profile.setInteger(UMediaProfile.KEY_START_ON_PREPARED, 1);
        profile.setInteger(UMediaProfile.KEY_ENABLE_BACKGROUND_PLAY, 0);
        profile.setInteger(UMediaProfile.KEY_LIVE_STREAMING, 1);
        profile.setInteger(UMediaProfile.KEY_MEDIACODEC, 1);

        profile.setInteger(UMediaProfile.KEY_PREPARE_TIMEOUT, 1000 * 5);
        profile.setInteger(UMediaProfile.KEY_MIN_READ_FRAME_TIMEOUT_RECONNECT_INTERVAL, 3);

        profile.setInteger(UMediaProfile.KEY_READ_FRAME_TIMEOUT, 1000 * 5);
        profile.setInteger(UMediaProfile.KEY_MIN_PREPARE_TIMEOUT_RECONNECT_INTERVAL, 3);

        if (mVideoView != null && mVideoView.isInPlaybackState()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
        }

        mVideoView.setMediaPorfile(profile);//set before setVideoPath
        mVideoView.setOnPlayerStateListener(this);//set before setVideoPath
//        mVideoView.setVideoPath(liveRoom.getLivePullUrl());

    }


    @Override protected void onResume() {
        super.onResume();
        mVideoView.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        mVideoView.onPause();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mVideoView.onDestroy();
    }

    @Override public void onPlayerStateChanged(State state, int i, Object o) {
        switch (state) {
            case START:
                isSteamConnected = true;
                isReconnecting = false;
                mVideoView.applyAspectRatio(UVideoView.VIDEO_RATIO_FILL_PARENT);//set after start
                break;
            case VIDEO_SIZE_CHANGED:
                break;
            case COMPLETED:
                Toast.makeText(this, "直播已结束", Toast.LENGTH_LONG).show();
                break;
            case RECONNECT:
                isReconnecting = true;
                break;
        }
    }

    @Override public void onPlayerInfo(Info info, int extra1, Object o) {
    }

    @Override public void onPlayerError(Error error, int extra1, Object o) {
        isSteamConnected = false;
        isReconnecting = false;
        switch (error) {
            case IOERROR:
                reconnect();
                break;
            case PREPARE_TIMEOUT:
                break;
            case READ_FRAME_TIMEOUT:
                System.out.println();
                break;
            case UNKNOWN:
                Toast.makeText(this, "Error: " + extra1, Toast.LENGTH_SHORT).show();
                break;
        }
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
