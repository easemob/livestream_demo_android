package com.easemob.livedemo.ui.live;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.common.OnConfirmClickListener;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.data.restapi.LiveManager;
import com.easemob.livedemo.data.restapi.LiveException;
import com.easemob.livedemo.data.restapi.model.LiveStatusModule;
import com.easemob.livedemo.data.restapi.model.StatisticsType;
import com.easemob.livedemo.ui.live.fragment.LiveGiftDialog;
import com.easemob.livedemo.ui.live.fragment.LiveMemberListDialog;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.exceptions.HyphenateException;
import com.ucloud.uvod.UMediaProfile;
import com.ucloud.uvod.UPlayerStateListener;
import com.ucloud.uvod.widget.UVideoView;
import java.util.Random;

public class LiveAudienceActivity extends LiveBaseActivity implements UPlayerStateListener, View.OnClickListener {
    @BindView(R.id.loading_layout)
    RelativeLayout loadingLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.loading_text)
    TextView loadingText;
    @BindView(R.id.cover_image)
    ImageView coverView;
    @BindView(R.id.group_ui)
    ConstraintLayout groupUi;

    String rtmpPlayStreamUrl = "rtmp://vlive3.rtmp.cdn.ucloud.com.cn/ucloud/";
    private UVideoView mVideoView;
    private UMediaProfile profile;
    volatile boolean isSteamConnected;
    volatile boolean isReconnecting;
    Thread reconnectThread;

    @Override
    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.em_activity_live_audience);
        ButterKnife.bind(this);
        setFitSystemForTheme(true, R.color.black);
    }

    @Override
    protected void initView() {
        super.initView();
        switchCameraView.setVisibility(View.GONE);
        likeImageView.setVisibility(View.VISIBLE);
        Glide.with(this).load(liveRoom.getCover()).placeholder(R.color.placeholder).into(coverView);
        mVideoView = (UVideoView) findViewById(R.id.videoview);
    }

    @Override
    protected void initListener() {
        super.initListener();
        tvAttention.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        connect();
    }

    @Override
    protected void skipToListDialog() {
        super.skipToListDialog();
        LiveMemberListDialog newInstance = LiveMemberListDialog.getNewInstance(chatroomId);
        newInstance.setOnItemClickListener(new LiveMemberListDialog.OnMemberItemClickListener() {
            @Override
            public void OnMemberItemClick(View view, int position, String member) {
                showUserDetailsDialog(member);
            }
        });
        newInstance.show(getSupportFragmentManager(), "liveMember");
    }

    @Override
    protected void AnchorClick() {
        super.AnchorClick();
        showUserDetailsDialog(chatroom.getOwner());
    }

    @Override
    protected void onGiftClick() {
        super.onGiftClick();
        showGiftDialog();
    }

    private void showGiftDialog() {
        LiveGiftDialog dialog = LiveGiftDialog.getNewInstance();
        dialog.show(getSupportFragmentManager(), "live_gift");
        dialog.setOnConfirmClickListener(new OnConfirmClickListener() {
            @Override
            public void onConfirmClick(View view, Object bean) {
                if(bean instanceof GiftBean) {
                    barrageLayout.showGift((GiftBean) bean);
                }
            }
        });
    }

    private void connect(){
        connectChatServer();
    }

    private void connectChatServer(){
        executeTask(new ThreadPoolManager.Task<LiveStatusModule.LiveStatus>() {
            @Override public LiveStatusModule.LiveStatus onRequest() throws HyphenateException {
                return LiveManager.getInstance().getLiveRoomStatus(liveId);
            }

            @Override public void onSuccess(LiveStatusModule.LiveStatus status) {
                loadingLayout.setVisibility(View.INVISIBLE);
                switch (status){
                    case completed: //complete状态允许用户加入聊天室
                        showLongToast("直播已结束");
                    case ongoing:
                        connectLiveStream();
                        joinChatRoom();
                        break;
                    case closed:
                        showLongToast("直播间已关闭");
                        finish();
                        break;
                    case not_start:
                        showLongToast("直播尚未开始");
                        break;
                }

            }

            @Override public void onError(HyphenateException exception) {
                loadingLayout.setVisibility(View.INVISIBLE);
                showToast("加载失败");
            }
        });
    }

    private void joinChatRoom() {
        //loadingLayout.setVisibility(View.INVISIBLE);
        EMClient.getInstance()
                .chatroomManager()
                .joinChatRoom(chatroomId, new EMValueCallBack<EMChatRoom>() {
                    @Override public void onSuccess(EMChatRoom emChatRoom) {
                        chatroom = emChatRoom;
                        addChatRoomChangeListener();
                        onMessageListInit();
                        //postUserChangeEvent(StatisticsType.JOIN, EMClient.getInstance().getCurrentUser());
                    }

                    @Override public void onError(int i, String s) {
                        if(i == EMError.GROUP_PERMISSION_DENIED || i == EMError.CHATROOM_PERMISSION_DENIED){
                            showLongToast("你没有权限加入此房间");
                            finish();
                        }else if(i == EMError.CHATROOM_MEMBERS_FULL){
                            showLongToast("房间成员已满");
                            finish();
                        }
                        showLongToast("加入聊天室失败: " +s);
                    }
                });
    }

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
        mVideoView.setVideoPath(liveRoom.getLivePullUrl());

        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //if(getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE){
            messageView.getInputView().requestFocus();
            messageView.getInputView().requestFocusFromTouch();
        //}
    }

    @Override
    protected void slideToLeft(int startX, float endX) {
        super.slideToLeft(startX, endX);
        startAnimation(groupUi, startX, endX);
    }

    @Override
    protected void slideToRight(float startX, float endX) {
        super.slideToRight(startX, endX);
        startAnimation(groupUi, startX, endX);
    }

    @Override protected void onResume() {
        super.onResume();
        mVideoView.onResume();
        if (isMessageListInited) messageView.refresh();
        EaseUI.getInstance().pushActivity(this);
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override protected void onPause() {
        super.onPause();
        mVideoView.onPause();
    }

    @Override public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);

        // 把此activity 从foreground activity 列表里移除
        EaseUI.getInstance().popActivity(this);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if(isMessageListInited) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(chatroomId);

            //postUserChangeEvent(StatisticsType.LEAVE, EMClient.getInstance().getCurrentUser());
        }

        if (chatRoomChangeListener != null) {
            EMClient.getInstance()
                    .chatroomManager()
                    .removeChatRoomChangeListener(chatRoomChangeListener);
        }

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



    @OnClick(R.id.img_bt_close) void close() {
        finish();
    }

    int praiseCount;
    final int praiseSendDelay = 4 * 1000;
    private Thread sendPraiseThread;
    /**
     * 点赞
     */
    @OnClick(R.id.like_image) void Praise() {
        periscopeLayout.addHeart();
        synchronized (this) {
            ++praiseCount;
        }
        if(sendPraiseThread == null){
            sendPraiseThread = new Thread(new Runnable() {
                @Override public void run() {
                    while(!isFinishing()){
                        int count = 0;
                        synchronized (LiveAudienceActivity.this){
                            count = praiseCount;
                            praiseCount = 0;
                        }
                        if(count > 0) {
                            sendPraiseMessage(count);
                            try {
                                LiveManager.getInstance().postStatistics(StatisticsType.PRAISE, liveId, count);
                            } catch (LiveException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(praiseSendDelay + new Random().nextInt(2000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            });
            sendPraiseThread.setDaemon(true);
            sendPraiseThread.start();
        }
    }

    private void sendPraiseMessage(int praiseCount) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setTo(chatroomId);
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(DemoConstants.CMD_PRAISE);
        message.addBody(cmdMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(DemoConstants.EXTRA_PRAISE_COUNT, praiseCount);
        EMClient.getInstance().chatManager().sendMessage(message);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_attention :
                
                break;
        }
    }
}
