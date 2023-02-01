package com.easemob.livedemo.ui.cdn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.easemob.live.cdn.fragment.CdnLiveHostFragment;
import com.easemob.live.widgets.VideoGridContainer;
import com.easemob.livedemo.common.inf.OnConfirmClickListener;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.fast.FastLiveAudienceActivity;
import com.easemob.livedemo.ui.live.LiveBaseActivity;
import com.easemob.livedemo.ui.live.fragment.LiveAnchorFragment;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.util.EMLog;

import com.easemob.live.FastLiveHelper;

import com.easemob.livedemo.R;

import com.easemob.livedemo.ui.cdn.presenter.CdnLiveHostPresenterImpl;

import io.agora.rtc2.Constants;

/**
 * 1. The process for the host to start the live broadcast is as follows:
 * (1) Initialize RtcEngine. Generally, it can be placed at the entrance of the program. See the initAgora() method in DemoApplication. The specific call is {@link FastLiveHelper#init(Context, String)}
 * (2) Set the channel scene. This logic in this demo is in {@link FastLiveHelper#init(Context, String)}, specifically in {@link io.agora.rtc2.RtcEngine#setChannelProfile(int)},
 * Live scene set to {@link Constants#CHANNEL_PROFILE_LIVE_BROADCASTING}
 * (3) Obtain agora token. This generally calls the app server related interface and obtains it from the server. This step can be omitted if it is set to not verify the token in the sound network console.
 * (4) Join the channel and set the user role. This involves the generation of the channel. In this demo, the channel is returned from the server with the room information.
 * The calling method for joining a channel is {@link FastLiveHelper#joinRtcChannel(int, String, int)}, and the method for setting user roles is {@link io.agora.rtc2.RtcEngine#setClientRole(int)}
 * (5) After the two conditions below are met, the live broadcast can be started {@link FastLiveHelper#startBroadcast(VideoGridContainer, int)}.
 * The above method has the following logic: (1) Set the user role. (2) Set the local view.
 * 2. Two conditions for starting live broadcast:
 * (1) Join the live room and set the state to the live state, the callback method is {@link #onStartCamera()}
 * (2) Obtain the sound network token (if necessary) successfully and join the channel
 */
public class CdnLiveHostActivity extends LiveBaseActivity implements LiveAnchorFragment.OnCameraListener, OnConfirmClickListener {
    private static final String TAG = CdnLiveHostActivity.class.getSimpleName();

    private LiveAnchorFragment fragment;
    private CdnLiveHostFragment fastFragment;
    private CdnLiveHostPresenterImpl presenter;

    public static void actionStart(Context context, LiveRoom liveRoom) {
        Intent starter = new Intent(context, CdnLiveHostActivity.class);
        starter.putExtra("liveroom", liveRoom);
        context.startActivity(starter);
    }

    @Override
    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_cdn_live_host);
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
        fragment = (LiveAnchorFragment) getSupportFragmentManager().findFragmentByTag("cdn_live_host");
        if (fragment == null) {
            fragment = new LiveAnchorFragment();
            fragment.setOnStopLiveClickListener(this);
            Bundle bundle = new Bundle();
            bundle.putSerializable("liveroom", liveRoom);
            fragment.setArguments(bundle);
        }
        fragment.setOnCameraListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "cdn_live_host").commit();
    }

    private void initVideoFragment() {
        fastFragment = (CdnLiveHostFragment) getSupportFragmentManager().findFragmentByTag("cdn_live_host_video");
        presenter = new CdnLiveHostPresenterImpl();
        if (fastFragment == null) {
            EMLog.d(TAG, "not have CdnLiveHostFragment");
            fastFragment = new CdnLiveHostFragment(presenter);
            Bundle bundle = new Bundle();
            bundle.putString("channel", liveRoom.getChannel());
            bundle.putString("roomId", liveRoom.getId());
            bundle.putString("hxId", EMClient.getInstance().getCurrentUser());
            bundle.putString("hxAppkey", EMClient.getInstance().getOptions().getAppKey());
            fastFragment.setArguments(bundle);
        } else {
            EMLog.d(TAG, "already have CdnLiveHostFragment");
            fastFragment.setPresenter(presenter);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_video_fragment, fastFragment, "cdn_live_host_video").commit();
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
        EMLog.d(TAG, "onRoomOwnerChangedToOtherUser newOwner: " + newOwner + " current user: " + EMClient.getInstance().getCurrentUser());
        if (presenter != null) {
            presenter.leaveChannel();
        }
        FastLiveAudienceActivity.actionStart(mContext, liveRoom);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null && liveRoom != null) {
            presenter.deleteRoom(liveRoom.getChatroomId());
        }
    }

    @Override
    public void onConfirmClick(View view, Object bean) {
        if (presenter != null) {
            presenter.leaveChannel();
        }
    }
}

