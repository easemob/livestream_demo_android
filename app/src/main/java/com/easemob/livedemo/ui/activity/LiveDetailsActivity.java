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
import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.LiveRoom;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.ucloud.common.logger.L;
import com.ucloud.player.widget.v2.UVideoView;
import java.util.Random;

public class LiveDetailsActivity extends LiveBaseActivity implements UVideoView.Callback {

    String rtmpPlayStreamUrl = "rtmp://vlive3.rtmp.cdn.ucloud.com.cn/ucloud/";
    private UVideoView mVideoView;

    @BindView(R.id.loading_layout) RelativeLayout loadingLayout;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.loading_text) TextView loadingText;
    @BindView(R.id.cover_image) ImageView coverView;
    @BindView(R.id.tv_username) TextView usernameView;

    @Override
    protected void onActivityCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_live_details);
        ButterKnife.bind(this);


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        LiveRoom liveRoom = getIntent().getParcelableExtra("liveroom");
        liveId = liveRoom.getId();
        chatroomId = liveRoom.getChatroomId();
        int coverRes = liveRoom.getCover();
        coverView.setImageResource(coverRes);

        anchorId = liveRoom.getAnchorId();
        usernameView.setText(anchorId);

        mVideoView = (UVideoView) findViewById(R.id.videoview);

        mVideoView.setPlayType(UVideoView.PlayType.LIVE);
        mVideoView.setPlayMode(UVideoView.PlayMode.NORMAL);
        mVideoView.setRatio(UVideoView.VIDEO_RATIO_FILL_PARENT);
        mVideoView.setDecoder(UVideoView.DECODER_VOD_SW);

        mVideoView.registerCallback(this);
        mVideoView.setVideoPath(rtmpPlayStreamUrl + liveId);
//      mVideoView.setVideoPath(rtmpPlayStreamUrl);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isMessageListInited)
            messageView.refresh();
        EaseUI.getInstance().pushActivity(this);
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);

        // 把此activity 从foreground activity 列表里移除
        EaseUI.getInstance().popActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatroomManager().leaveChatRoom(chatroomId);

        if (chatRoomChangeListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomChangeListener(chatRoomChangeListener);
        }
        if (mVideoView != null) {
            mVideoView.setVolume(0, 0);
            mVideoView.stopPlayback();
            mVideoView.release(true);
        }
    }

    @Override
    public void onEvent(int what, String message) {
        L.d(TAG, "what:" + what + ", message:" + message);
        switch (what) {
            case UVideoView.Callback.EVENT_PLAY_START:
                loadingLayout.setVisibility(View.INVISIBLE);
                EMClient.getInstance().chatroomManager().joinChatRoom(chatroomId, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom emChatRoom) {
                        chatroom = emChatRoom;
                        addChatRoomChangeListenr();
                        onMessageListInit();
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!isFinishing()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    periscopeLayout.addHeart();
                                }
                            });
                            try {
                                Thread.sleep(new Random().nextInt(400) + 200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                break;
            case UVideoView.Callback.EVENT_PLAY_PAUSE:
                break;
            case UVideoView.Callback.EVENT_PLAY_STOP:
                break;
            case UVideoView.Callback.EVENT_PLAY_COMPLETION:
                Toast.makeText(this, "直播已结束",Toast.LENGTH_LONG).show();
                finish();
                break;
            case UVideoView.Callback.EVENT_PLAY_DESTORY:
                Toast.makeText(this, "DESTORY", Toast.LENGTH_SHORT).show();
                break;
            case UVideoView.Callback.EVENT_PLAY_ERROR:
                loadingText.setText("主播尚未开播");
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "主播尚未开播", Toast.LENGTH_LONG).show();
                break;
            case UVideoView.Callback.EVENT_PLAY_RESUME:
                break;
            case UVideoView.Callback.EVENT_PLAY_INFO_BUFFERING_START:
//                Toast.makeText(VideoActivity.this, "unstable network", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @OnClick(R.id.img_bt_close) void close(){
        finish();
    }

}
