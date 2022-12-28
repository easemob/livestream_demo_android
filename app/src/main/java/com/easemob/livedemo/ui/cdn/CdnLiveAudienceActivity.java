package com.easemob.livedemo.ui.cdn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.easemob.live.FastLiveHelper;
import com.easemob.live.cdn.fragment.CdnLiveAudienceFragment;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.common.livedata.LiveDataBus;
import com.easemob.livedemo.common.utils.DemoHelper;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.live.LiveBaseActivity;
import com.easemob.livedemo.ui.live.fragment.LiveAudienceFragment;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

import com.easemob.livedemo.R;

import com.easemob.livedemo.ui.cdn.presenter.CdnLiveAudiencePresenterImpl;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.video.VideoCanvas;

/**
 * The process of watching live video is as follows:
 * (1) Initialize RtcEngine. Generally, it can be placed at the entrance of the program. See the initAgora() method in DemoApplication. The specific call is {@link FastLiveHelper#init(Context, String)}
 * (2) Set the channel scene. This logic in this demo is in {@link FastLiveHelper#init(Context, String)}, specifically in {@link io.agora.rtc2.RtcEngine#setChannelProfile(int)},
 * Live scene set to {@link Constants#CHANNEL_PROFILE_LIVE_BROADCASTING}
 * (3) Obtain agora token. This generally calls the app server related interface and obtains it from the server. This step can be omitted if it is set to not verify the token in the sound network console.
 * (4) Join the channel and set the role. This involves the generation of the channel. In this demo, the channel is returned from the server with the room information.
 * The calling method to join a channel is {@link FastLiveHelper#joinRtcChannel(int, String, int)}.
 * Set user role method {@link io.agora.rtc2.RtcEngine#setClientRole(int)}.
 * (5) Monitor {@link IRtcEngineEventHandler#onRemoteVideoStateChanged(int, int, int, int)} method, when state returns {@link Constants#REMOTE_VIDEO_STATE_STARTING},
 * To add a remote view, the call method is {@link io.agora.rtc2.RtcEngine#setupRemoteVideo(VideoCanvas)}.
 * Introduction to the official API of SoundNet (https://docs.agora.io/cn/live-streaming/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler.html#ac7144e0124c3d8f75e0366b0246fbe3b)
 * Note: When calling the {@link io.agora.rtc2.RtcEngine#setupRemoteVideo(VideoCanvas)} method, the official API documentation of SoundNet has the following introduction:
 * If the app cannot know the user ID of the other party in advance, it can be set when the app receives the onUserJoined event. If video recording is enabled,
 * The video recording service will join the channel as a dumb client, so other clients will also receive its onUserJoined event, the app should not bind a view to it (because it will not send video streams),
 * If the App does not recognize the dumb client, you can bind the view in the onFirstRemoteVideoDecoded event. To unbind a user from a view can set the view to be empty.
 * After exiting the channel, the SDK will clear the binding relationship of the remote user.
 * <p>
 * It should be noted here that {@link IRtcEngineEventHandler#onFirstRemoteVideoDecoded(int, int, int, int)} has been deprecated after version 2.9.0,
 * Need to call {@link IRtcEngineEventHandler#onRemoteVideoStateChanged(int, int, int, int)}. For details, please refer to the usage of point (5).
 */
public class CdnLiveAudienceActivity extends LiveBaseActivity implements LiveAudienceFragment.OnLiveListener {
    private static final String TAG = CdnLiveAudienceActivity.class.getSimpleName();
    private LiveAudienceFragment fragment;
    private CdnLiveAudienceFragment cdnFragment;
    private CdnLiveAudiencePresenterImpl presenter;

    public static void actionStart(Context context, LiveRoom liveRoom) {
        Intent starter = new Intent(context, CdnLiveAudienceActivity.class);
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initFragment();
        initVideoFragment();
    }

    @Override
    protected void initData() {
        super.initData();
        LiveDataBus.get().with(DemoConstants.EVENT_ANCHOR_FINISH_LIVE, Boolean.class).observe(mContext, event -> {
            if (liveRoom != null
                    && !TextUtils.isEmpty(liveRoom.getVideo_type())
                    && !DemoHelper.isVod(liveRoom.getVideo_type())) {
                if (presenter != null) {
                    presenter.onLiveClosed();
                }
            }
        });
    }

    private void initFragment() {
        fragment = (LiveAudienceFragment) getSupportFragmentManager().findFragmentByTag("live_audience");
        if (fragment == null) {
            fragment = new LiveAudienceFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("liveroom", liveRoom);
            fragment.setArguments(bundle);
        }
        fragment.setOnLiveListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "live_audience").commit();
    }

    private void initVideoFragment() {
        cdnFragment = (CdnLiveAudienceFragment) getSupportFragmentManager().findFragmentByTag("fast_live_audience_video");
        presenter = new CdnLiveAudiencePresenterImpl();
        if (cdnFragment == null) {
            EMLog.d(TAG, "not have CdnLiveAudienceFragment");
            cdnFragment = new CdnLiveAudienceFragment(presenter);
            Bundle bundle = new Bundle();
            bundle.putString("channel", liveRoom.getChannel());
            bundle.putString("roomId", liveRoom.getId());
            bundle.putString("hxId", EMClient.getInstance().getCurrentUser());
            bundle.putString("hxAppkey", EMClient.getInstance().getOptions().getAppKey());
            cdnFragment.setArguments(bundle);
        } else {
            EMLog.d(TAG, "already have CdnLiveAudienceFragment");
            cdnFragment.setPresenter(presenter);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_video_fragment, cdnFragment, "fast_live_audience_video").commit();
    }

    @Override
    public void onLiveOngoing(LiveRoom data) {

    }

    @Override
    public void onLiveClosed() {
        if (presenter != null) {
            presenter.onLiveClosed();
        }
        showLongToast("Live stream end!");
        finish();
    }

    @Override
    public void onRoomOwnerChangedToCurrentUser(String chatRoomId, String newOwner) {
        if (presenter != null) {
            presenter.leaveChannel();
        }
        EMLog.d(TAG, "onRoomOwnerChangedToCurrentUser newOwner: " + newOwner);
        CdnLiveHostActivity.actionStart(mContext, liveRoom);
        finish();
    }
}

