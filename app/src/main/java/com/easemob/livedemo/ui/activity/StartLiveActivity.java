package com.easemob.livedemo.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.easemob.livedemo.R;
import com.easemob.livedemo.data.TestDataRepository;
import com.easemob.livedemo.data.model.LiveSettings;
import com.easemob.livedemo.utils.Log2FileUtil;
import com.easemob.livedemo.utils.Utils;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.ucloud.common.util.DeviceUtils;
import com.ucloud.live.UEasyStreaming;
import com.ucloud.live.UStreamingProfile;
import com.ucloud.live.widget.UAspectFrameLayout;
import java.util.Random;

public class StartLiveActivity extends LiveBaseActivity implements UEasyStreaming.UStreamingStateListener{
    private static final String TAG = StartLiveActivity.class.getSimpleName();
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.container) UAspectFrameLayout mPreviewContainer;
    @BindView(R.id.start_container) RelativeLayout startContainer;
    @BindView(R.id.et_title) EditText titleEdit;
    @BindView(R.id.countdown_txtv) TextView countdownView;

    protected UEasyStreaming mEasyStreaming;
    protected String rtmpPushStreamDomain = "publish3.cdn.ucloud.com.cn";
    public static final int MSG_UPDATE_COUNTDOWN = 1;

    public static final int COUNTDOWN_DELAY = 1000;

    public static final  int COUNTDOWN_START_INDEX = 3;
    public static final  int COUNTDOWN_END_INDEX = 1;
    protected boolean isShutDownCountdown = false;
    private LiveSettings mSettings;
    private UStreamingProfile mStreamingProfile;
    UEasyStreaming.UEncodingType encodingType;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_COUNTDOWN:
                    handleUpdateCountdown(msg.arg1);
                    break;
            }
        }
    };

    //203138620012364216
    @Override
    protected void onActivityCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_start_live);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        liveId = TestDataRepository.getLiveRoomId(EMClient.getInstance().getCurrentUser());
        chatroomId = TestDataRepository.getChatRoomId(EMClient.getInstance().getCurrentUser());

        initEnv();
    }

    public void initEnv() {
        mSettings = new LiveSettings(this);
        if (mSettings.isOpenLogRecoder()) {
            Log2FileUtil.getInstance().setLogCacheDir(mSettings.getLogCacheDir());
            Log2FileUtil.getInstance().startLog(); //
        }

//        UStreamingProfile.Stream stream = new UStreamingProfile.Stream(rtmpPushStreamDomain, "ucloud/" + mSettings.getPusblishStreamId());
        //hardcode
        UStreamingProfile.Stream stream = new UStreamingProfile.Stream(rtmpPushStreamDomain, "ucloud/" + liveId);

        mStreamingProfile = new UStreamingProfile.Builder()
                .setVideoCaptureWidth(mSettings.getVideoCaptureWidth())
                .setVideoCaptureHeight(mSettings.getVideoCaptureHeight())
                .setVideoEncodingBitrate(mSettings.getVideoEncodingBitRate()) //UStreamingProfile.VIDEO_BITRATE_NORMAL
                .setVideoEncodingFrameRate(mSettings.getVideoFrameRate())
                .setStream(stream).build();

        encodingType = UEasyStreaming.UEncodingType.MEDIA_X264;
        if(DeviceUtils.hasJellyBeanMr2()){
            encodingType = UEasyStreaming.UEncodingType.MEDIA_CODEC;
        }
        mEasyStreaming = new UEasyStreaming(this, encodingType);
        mEasyStreaming.setStreamingStateListener(this);
        mEasyStreaming.setAspectWithStreamingProfile(mPreviewContainer, mStreamingProfile);
    }


    @Override
    public void onStateChanged(int type, Object event) {
        switch (type) {
            case UEasyStreaming.State.MEDIA_INFO_SIGNATRUE_FAILED:
                Toast.makeText(this, event.toString(), Toast.LENGTH_LONG).show();
                break;
            case UEasyStreaming.State.START_RECORDING:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(!isFinishing()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    periscopeLayout.addHeart();
                                }
                            });
                            try {
                                Thread.sleep(new Random().nextInt(400) + 100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mEasyStreaming.stopRecording();
        super.onBackPressed();
    }

    @OnClick(R.id.img_bt_switch_camera) void switchCamera(){
        mEasyStreaming.switchCamera();
    }

    @OnClick(R.id.btn_start) void startLive(){
        if(TextUtils.isEmpty(titleEdit.getText())){
            Toast.makeText(this, "直播标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //demo为了测试方便，只有指定的账号才能开启直播
        if(liveId == null){
            String[] anchorIds = TestDataRepository.anchorIds;
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < anchorIds.length; i++){
                sb.append(anchorIds[i]);
                if(i != (anchorIds.length-1))
                    sb.append(",");
            }
            new EaseAlertDialog(this,"demo中只有"+sb.toString()+"这几个账户才能开启直播").show();
            return;
        }

        startContainer.setVisibility(View.INVISIBLE);
        Utils.hideKeyboard(titleEdit);
        new Thread(){
            public void run() {
                int i = COUNTDOWN_START_INDEX;
                do {
                    Message msg = Message.obtain();
                    msg.what = MSG_UPDATE_COUNTDOWN;
                    msg.arg1 = i;
                    handler.sendMessage(msg);
                    i--;
                    try {
                        Thread.sleep(COUNTDOWN_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while(i >= COUNTDOWN_END_INDEX);
            }
        }.start();
    }





    protected void setListItemClickListener() {}

    public void handleUpdateCountdown(final int count) {
        if (countdownView != null) {
            countdownView.setVisibility(View.VISIBLE);
            countdownView.setText(String.format("%d", count));
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(COUNTDOWN_DELAY);
            scaleAnimation.setFillAfter(false);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    countdownView.setVisibility(View.GONE);
                    EMClient.getInstance().chatroomManager().joinChatRoom(chatroomId, new EMValueCallBack<EMChatRoom>() {
                        @Override
                        public void onSuccess(EMChatRoom emChatRoom) {
                            chatroom = emChatRoom;
                            addChatRoomChangeListenr();
                            onMessageListInit();
                        }

                        @Override
                        public void onError(int i, String s) {
                            showToast("加入聊天室失败");
                        }
                    });

                    if (count == COUNTDOWN_END_INDEX && mEasyStreaming != null && !isShutDownCountdown) {
                        showToast("直播开始！");
                        mEasyStreaming.startRecording();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (!isShutDownCountdown) {
                countdownView.startAnimation(scaleAnimation);
            } else {
                countdownView.setVisibility(View.GONE);
            }
        }
    }


    @Override
    void onChatImageClck() {
        if(encodingType == UEasyStreaming.UEncodingType.MEDIA_CODEC)
          mEasyStreaming.toggleFilter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEasyStreaming.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEasyStreaming.onResume();
        if(isMessageListInited)
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
        if(mSettings.isOpenLogRecoder()) {
            Log2FileUtil.getInstance().stopLog();
        }
        mEasyStreaming.onDestroy();

        EMClient.getInstance().chatroomManager().leaveChatRoom(chatroomId);

        if(chatRoomChangeListener != null){
            EMClient.getInstance().chatroomManager().removeChatRoomChangeListener(chatRoomChangeListener);
        }
    }
}
