package com.easemob.livedemo.ui.fast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.easemob.fastlive.FastLiveHelper;
import com.easemob.fastlive.fragment.FastLiveAudienceFragment;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.LiveDataBus;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.fast.presenter.FastLiveAudiencePresenterImpl;
import com.easemob.livedemo.ui.live.LiveBaseActivity;
import com.easemob.livedemo.ui.live.fragment.LiveAudienceFragment;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.video.VideoCanvas;

/**
 * 观看视频直播的流程如下：
 * （1）初始化 RtcEngine。一般放置在程序入口处即可，见DemoApplication中的initAgora()方法，具体调用为{@link FastLiveHelper#init(Context, String)}
 * （2）设置频道场景。本demo中此逻辑在{@link FastLiveHelper#init(Context, String)}中，具体在{@link io.agora.rtc2.RtcEngine#setChannelProfile(int)},
 *      直播场景设置为{@link Constants#CHANNEL_PROFILE_LIVE_BROADCASTING}
 * （3）获取声网token。这个一般调用app server相关接口，从服务器获取。如果在声网console中设置为不校验token可以不进行此步。
 * （4）加入channel并设置角色。这里涉及到channel的生成，本demo中channel是从服务端随房间信息返回的。
 *      加入channel的调用方法为{@link FastLiveHelper#joinRtcChannel(int, String, int)}。
 *      设置用户角色方法{@link io.agora.rtc2.RtcEngine#setClientRole(int)}。
 * （5）监听{@link IRtcEngineEventHandler#onRemoteVideoStateChanged(int, int, int, int)}方法，在state返回{@link Constants#REMOTE_VIDEO_STATE_STARTING}时，
 *      添加远端视图，调用方法为{@link io.agora.rtc2.RtcEngine#setupRemoteVideo(VideoCanvas)}。
 *      声网官方API介绍（https://docs.agora.io/cn/live-streaming/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler.html#ac7144e0124c3d8f75e0366b0246fbe3b）
 *  注：调用{@link io.agora.rtc2.RtcEngine#setupRemoteVideo(VideoCanvas)}方法时声网官方API文档中有如下介绍：
 *      如果 App 不能事先知道对方的用户 ID，可以在 APP 收到 onUserJoined 事件时设置。如果启用了视频录制功能，
 *      视频录制服务会做为一个哑客户端加入频道，因此其他客户端也会收到它的 onUserJoined 事件，App 不应给它绑定视图（因为它不会发送视频流），
 *      如果 App 不能识别哑客户端，可以在 onFirstRemoteVideoDecoded 事件时再绑定视图。解除某个用户的绑定视图可以把 view 设置为空。
 *      退出频道后，SDK 会把远程用户的绑定关系清除掉。
 *
 *      这里需要注意的是{@link IRtcEngineEventHandler#onFirstRemoteVideoDecoded(int, int, int, int)}已在2.9.0版本后废弃，
 *      需要调用{@link IRtcEngineEventHandler#onRemoteVideoStateChanged(int, int, int, int)}。具体详见第（5）点用法。
 */
public class FastLiveAudienceActivity extends LiveBaseActivity implements LiveAudienceFragment.OnLiveListener {
    private static final String TAG = FastLiveAudienceActivity.class.getSimpleName();
    private LiveAudienceFragment fragment;
    private FastLiveAudienceFragment fastFragment;
    private FastLiveAudiencePresenterImpl presenter;

    public static void actionStart(Context context, LiveRoom liveRoom) {
        Intent starter = new Intent(context, FastLiveAudienceActivity.class);
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
        initFragment();
        initVideoFragment();
    }

    @Override
    protected void initData() {
        super.initData();
        LiveDataBus.get().with(DemoConstants.EVENT_ANCHOR_FINISH_LIVE, Boolean.class).observe(mContext, event -> {
            //只有非点播模式下，才会去关闭播放器
            if(liveRoom != null
                    && !TextUtils.isEmpty(liveRoom.getVideo_type())
                    && !DemoHelper.isVod(liveRoom.getVideo_type())) {
                if(presenter != null) {
                    presenter.onLiveClosed();
                }
            }
        });
    }

    private void initFragment() {
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

    private void initVideoFragment() {
        fastFragment = (FastLiveAudienceFragment) getSupportFragmentManager().findFragmentByTag("fast_live_audience_video");
        presenter = new FastLiveAudiencePresenterImpl();
        if(fastFragment == null) {
            EMLog.d(TAG, "not have FastLiveAudienceFragment");
            fastFragment = new FastLiveAudienceFragment(presenter);
            Bundle bundle = new Bundle();
            bundle.putString("channel", liveRoom.getChannel());
            bundle.putString("roomId", liveRoom.getId());
            bundle.putString("hxId", EMClient.getInstance().getCurrentUser());
            bundle.putString("hxAppkey", EMClient.getInstance().getOptions().getAppKey());
            fastFragment.setArguments(bundle);
        }else {
            EMLog.d(TAG, "already have FastLiveAudienceFragment");
            fastFragment.setPresenter(presenter);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_video_fragment, fastFragment, "fast_live_audience_video").commit();
    }

    @Override
    public void onLiveOngoing(LiveRoom data) {

    }

    @Override
    public void onLiveClosed() {
        if(presenter != null) {
            presenter.onLiveClosed();
        }
        showLongToast("直播间已关闭");
        finish();
    }

    @Override
    public void onRoomOwnerChangedToCurrentUser(String chatRoomId, String newOwner) {
        // 如果直播间主播被调整为自己
        //切换直播场景之前先调用离开频道的逻辑，否则会造成当前用户还没有离开频道，又在新的页面加入频道的问题
        if(presenter != null) {
            presenter.leaveChannel();
        }
        EMLog.d(TAG, "onRoomOwnerChangedToCurrentUser newOwner: "+newOwner);
        FastLiveHostActivity.actionStart(mContext, liveRoom);
        finish();
    }
}

