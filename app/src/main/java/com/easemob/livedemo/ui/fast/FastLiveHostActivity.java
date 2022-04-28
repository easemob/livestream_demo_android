package com.easemob.livedemo.ui.fast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.easemob.fastlive.fragment.FastLiveHostFragment;
import com.easemob.fastlive.widgets.VideoGridContainer;
import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.fast.presenter.FastLiveHostPresenterImpl;
import com.easemob.livedemo.ui.live.LiveBaseActivity;
import com.easemob.livedemo.ui.live.fragment.LiveAnchorFragment;
import com.easemob.fastlive.FastLiveHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

import io.agora.rtc2.Constants;

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
 * （1）加入直播间并将状态置为直播状态，回调方法为{@link #onStartCamera()}
 *  (2) 获取声网token(如果需要的话)成功，并加入channel
 */
public class FastLiveHostActivity extends LiveBaseActivity implements LiveAnchorFragment.OnCameraListener {
    private static final String TAG = FastLiveHostActivity.class.getSimpleName();

    private LiveAnchorFragment fragment;
    private FastLiveHostFragment fastFragment;
    private FastLiveHostPresenterImpl presenter;

    public static void actionStart(Context context, LiveRoom liveRoom) {
        Intent starter = new Intent(context, FastLiveHostActivity.class);
        starter.putExtra("liveroom", liveRoom);
        context.startActivity(starter);
    }

    @Override
    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_fast_live_host);
        setFitSystemForTheme(false, android.R.color.transparent);
    }

    @Override
    protected void initView() {
        super.initView();
        coverImage.setVisibility(View.VISIBLE);
        initFragment();
        initVideoFragment();
    }

    private void initFragment() {
        fragment = (LiveAnchorFragment) getSupportFragmentManager().findFragmentByTag("fast_live_host");
        if (fragment == null) {
            fragment = new LiveAnchorFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("liveroom", liveRoom);
            fragment.setArguments(bundle);
        }
        fragment.setOnCameraListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "fast_live_host").commit();
    }

    private void initVideoFragment() {
        fastFragment = (FastLiveHostFragment) getSupportFragmentManager().findFragmentByTag("fast_live_host_video");
        presenter = new FastLiveHostPresenterImpl();
        if(fastFragment == null) {
            EMLog.d(TAG, "not have CdnLiveHostFragment");
            fastFragment = new FastLiveHostFragment(presenter);
            Bundle bundle = new Bundle();
            bundle.putString("channel", liveRoom.getChannel());
            bundle.putString("roomId", liveRoom.getId());
            bundle.putString("hxId", EMClient.getInstance().getCurrentUser());
            bundle.putString("hxAppkey", EMClient.getInstance().getOptions().getAppKey());
            fastFragment.setArguments(bundle);
        }else {
            EMLog.d(TAG, "already have CdnLiveHostFragment");
            fastFragment.setPresenter(presenter);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_video_fragment, fastFragment, "fast_live_host_video").commit();
    }

    @Override
    public void onStartCamera() {
        this.presenter.onStartCamera();
    }

    @Override
    public void switchCamera() {
        this.presenter.switchCamera();
    }

    @Override
    public void onStopCamera() {

    }

    @Override
    public void onRoomOwnerChangedToOtherUser(String chatRoomId, String newOwner) {
        EMLog.d(TAG, "onRoomOwnerChangedToOtherUser newOwner: "+newOwner + " current user: "+EMClient.getInstance().getCurrentUser());
        if(presenter != null) {
            presenter.leaveChannel();
        }
        FastLiveAudienceActivity.actionStart(mContext, liveRoom);
        finish();
    }

}

