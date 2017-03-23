package com.easemob.livedemo.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.data.restapi.ApiManager;
import com.easemob.livedemo.data.restapi.LiveException;
import com.easemob.livedemo.data.restapi.model.StatisticsType;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.exceptions.HyphenateException;
import com.ucloud.common.logger.L;
import com.ucloud.player.widget.v2.UVideoView;

public class LiveAudienceActivity extends LiveBaseActivity implements UVideoView.Callback {

    String rtmpPlayStreamUrl = "rtmp://vlive3.rtmp.cdn.ucloud.com.cn/ucloud/";
    private UVideoView mVideoView;

    @BindView(R.id.loading_layout) RelativeLayout loadingLayout;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.loading_text) TextView loadingText;
    @BindView(R.id.cover_image) ImageView coverView;

    @Override protected void onActivityCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_live_audience);
        ButterKnife.bind(this);

        userManagerView.setVisibility(View.INVISIBLE);
        switchCameraView.setVisibility(View.INVISIBLE);
        likeImageView.setVisibility(View.VISIBLE);

        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        Glide.with(this).load(liveRoom.getCover()).placeholder(R.color.placeholder).into(coverView);

        mVideoView = (UVideoView) findViewById(R.id.videoview);

        mVideoView.setPlayType(UVideoView.PlayType.LIVE);
        mVideoView.setPlayMode(UVideoView.PlayMode.NORMAL);
        mVideoView.setRatio(UVideoView.VIDEO_RATIO_FILL_PARENT);
        mVideoView.setDecoder(UVideoView.DECODER_VOD_SW);

        mVideoView.registerCallback(this);
        //mVideoView.setVideoPath(rtmpPlayStreamUrl + liveId);
        mVideoView.setVideoPath(liveRoom.getLivePullUrl());
    }

    @Override protected void onResume() {
        super.onResume();
        if (isMessageListInited) messageView.refresh();
        EaseUI.getInstance().pushActivity(this);
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
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

            postUserChangeEvent(StatisticsType.LEAVE, EMClient.getInstance().getCurrentUser());
        }

        if (chatRoomChangeListener != null) {
            EMClient.getInstance()
                    .chatroomManager()
                    .removeChatRoomChangeListener(chatRoomChangeListener);
        }
        if (mVideoView != null) {
            mVideoView.setVolume(0, 0);
            mVideoView.stopPlayback();
            mVideoView.release(true);
        }
    }

    @Override public void onEvent(int what, String message) {
        L.d(TAG, "what:" + what + ", message:" + message);
        switch (what) {
            case UVideoView.Callback.EVENT_PLAY_START:
                loadingLayout.setVisibility(View.INVISIBLE);
                EMClient.getInstance()
                        .chatroomManager()
                        .joinChatRoom(chatroomId, new EMValueCallBack<EMChatRoom>() {
                            @Override public void onSuccess(EMChatRoom emChatRoom) {
                                chatroom = emChatRoom;
                                addChatRoomChangeListener();
                                onMessageListInit();
                                postUserChangeEvent(StatisticsType.JOIN, EMClient.getInstance().getCurrentUser());
                            }

                            @Override public void onError(int i, String s) {
                                showToast("加入聊天室失败");
                            }
                        });

                //new Thread(new Runnable() {
                //    @Override
                //    public void run() {
                //        while (!isFinishing()) {
                //            runOnUiThread(new Runnable() {
                //                @Override
                //                public void run() {
                //                    periscopeLayout.addHeart();
                //                }
                //            });
                //            try {
                //                Thread.sleep(new Random().nextInt(500) + 200);
                //            } catch (InterruptedException e) {
                //                e.printStackTrace();
                //            }
                //        }
                //    }
                //}).start();
                break;
            case UVideoView.Callback.EVENT_PLAY_PAUSE:
                break;
            case UVideoView.Callback.EVENT_PLAY_STOP:
                break;
            case UVideoView.Callback.EVENT_PLAY_COMPLETION:
                Toast.makeText(this, "直播已结束", Toast.LENGTH_LONG).show();
                finish();
                break;
            case UVideoView.Callback.EVENT_PLAY_DESTORY:
                Toast.makeText(this, "DESTORY", Toast.LENGTH_SHORT).show();
                break;
            case UVideoView.Callback.EVENT_PLAY_ERROR:
                loadingText.setText("主播尚未直播");
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "主播尚未直播", Toast.LENGTH_LONG).show();
                break;
            case UVideoView.Callback.EVENT_PLAY_RESUME:
                break;
            case UVideoView.Callback.EVENT_PLAY_INFO_BUFFERING_START:
                //                Toast.makeText(VideoActivity.this, "unstable network", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @OnClick(R.id.img_bt_close) void close() {
        finish();
    }

    int praiseCount;
    int praiseSendDelay = 5 * 1000;
    long previousSendTime;

    /**
     * 点赞
     */
    @OnClick(R.id.like_image) void Praise() {
        periscopeLayout.addHeart();
        synchronized (this) {
            ++praiseCount;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - previousSendTime >= praiseSendDelay) {
            previousSendTime = currentTime;
            executeTask(new ThreadPoolManager.Task<Void>() {
                @Override public Void onRequest() throws HyphenateException {
                    int count = 0;
                    synchronized (LiveAudienceActivity.this){
                        count = praiseCount;
                        praiseCount = 0;
                    }
                    sendPraiseMessage(count);
                    ApiManager.get().postStatistics(StatisticsType.PRAISE, liveId, count);
                    return null;
                }

                @Override public void onSuccess(Void aVoid) {
                }

                @Override public void onError(HyphenateException exception) {

                }
            });
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

}
