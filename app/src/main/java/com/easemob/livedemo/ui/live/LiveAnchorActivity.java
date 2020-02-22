package com.easemob.livedemo.ui.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;

import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.common.LiveHelper;
import com.easemob.livedemo.common.OnConfirmClickListener;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.restapi.LiveManager;
import com.easemob.livedemo.ucloud.AVOption;
import com.easemob.livedemo.ucloud.LiveCameraView;
import com.easemob.livedemo.ui.activity.SimpleDialogFragment;
import com.easemob.livedemo.ui.live.fragment.LiveGiftStatisticsDialog;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.exceptions.HyphenateException;
import com.ucloud.ulive.UFilterProfile;
import com.ucloud.ulive.UNetworkListener;
import com.ucloud.ulive.UStreamStateListener;
import com.ucloud.ulive.UVideoProfile;

public class LiveAnchorActivity extends LiveBaseActivity implements View.OnClickListener {
    private static final String TAG = LiveAnchorActivity.class.getSimpleName();
    @BindView(R.id.container)
    LiveCameraView cameraView;
    @BindView(R.id.countdown_txtv)
    TextView countdownView;
    @BindView(R.id.finish_frame)
    ViewStub liveEndLayout;
    @BindView(R.id.cover_image)
    ImageView coverImage;
    @BindView(R.id.live_container)
    ConstraintLayout liveContainer;
    @BindView(R.id.group_gift_info)
    Group groupGiftInfo;
    @BindView(R.id.tv_gift_num)
    TextView tvGiftNum;
    @BindView(R.id.tv_like_num)
    TextView tvLikeNum;
    @BindView(R.id.img_bt_close)
    ImageView imgBtClose;
    //@BindView(R.id.img_bt_switch_light) ImageButton lightSwitch;
    //@BindView(R.id.img_bt_switch_voice) ImageButton voiceSwitch;

    //protected UEasyStreaming mEasyStreaming;
    protected String rtmpPushStreamDomain = "publish3.cdn.ucloud.com.cn";
    public static final int MSG_UPDATE_COUNTDOWN = 1;

    public static final int COUNTDOWN_DELAY = 1000;

    public static final int COUNTDOWN_START_INDEX = 3;
    public static final int COUNTDOWN_END_INDEX = 1;
    protected boolean isShutDownCountdown = false;
    //private LiveSettings mSettings;
    //private UStreamingProfile mStreamingProfile;
    //UEasyStreaming.UEncodingType encodingType;

    boolean isStarted;

    private AVOption mAVOption;

    private Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_COUNTDOWN:
                    handleUpdateCountdown(msg.arg1);
                    break;
            }
        }
    };

    public static void actionStart(Context context, LiveRoom liveRoom) {
        Intent starter = new Intent(context, LiveAnchorActivity.class);
        starter.putExtra("liveroom", liveRoom);
        context.startActivity(starter);
    }

    //203138620012364216img_bt_close
    @Override
    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.em_activity_live_anchor);
        ButterKnife.bind(this);
        setFitSystemForTheme(true, R.color.black);
    }

    @Override
    protected void initView() {
        super.initView();
        groupGiftInfo.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initListener() {
        super.initListener();
        imgBtClose.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        initLiveEnv();
        startLive();
    }

    public void initLiveEnv() {
        mAVOption = new AVOption();
        mAVOption.streamUrl = liveRoom.getLivePushUrl();
        mAVOption.videoFilterMode = UFilterProfile.FilterMode.GPU;
        mAVOption.videoCodecType = UVideoProfile.CODEC_MODE_HARD;
        mAVOption.videoCaptureOrientation = UVideoProfile.ORIENTATION_PORTRAIT;
        mAVOption.videoFramerate = 20;
        mAVOption.videoBitrate = UVideoProfile.VIDEO_BITRATE_NORMAL;
        mAVOption.videoResolution = UVideoProfile.Resolution.RATIO_AUTO.ordinal();
    }

    private void startPreview() {
        cameraView.init(mAVOption);
    }

    private void stopPreview() {
        cameraView.stopRecordingAndDismissPreview();
    }

    @Override
    public void onBackPressed() {
        //mEasyStreaming.();
        stopPreview();
        super.onBackPressed();
    }

    /**
     * 切换摄像头
     */
    @OnClick(R.id.switch_camera_image)
    void switchCamera() {
        //mEasyStreaming.switchCamera();
        cameraView.switchCamera();
    }

    /**
     * 关闭直播显示直播成果
     */
    @OnClick(R.id.img_bt_close)
    void closeLive() {
        //mEasyStreaming.stopRecording();
        cameraView.onPause();
        stopPreview();

        if (!isStarted) {
            finish();
            return;
        }
        showConfirmCloseLayout();
    }

    @Override
    protected void onGiftClick() {
        super.onGiftClick();
        showGiftDialog();
    }

    @Override
    protected void slideToLeft(int startX, float endX) {
        super.slideToLeft(startX, endX);
        startAnimation(liveContainer, startX, endX);
    }

    @Override
    protected void slideToRight(float startX, float endX) {
        super.slideToRight(startX, endX);
        startAnimation(liveContainer, startX, endX);
    }

    @Override
    protected void skipToListDialog() {
        super.skipToListDialog();
        showUserList();
    }

    private void showGiftDialog() {
        LiveGiftStatisticsDialog.getNewInstance().show(getSupportFragmentManager(), "git_statistics");
    }

    /**
     * 开始直播
     */
    private void startLive() {
        //Utils.hideKeyboard(titleEdit);
        new Thread() {
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
                } while (i >= COUNTDOWN_END_INDEX);
            }
        }.start();
    }

    private View liveEndView;

    private void showConfirmCloseLayout() {
        if (liveEndView == null) {
            liveEndView = liveEndLayout.inflate();
        }
        liveContainer.setVisibility(View.INVISIBLE);
        liveEndView.setVisibility(View.VISIBLE);
        Button liveContinueBtn = (Button) liveEndView.findViewById(R.id.live_close_confirm);
        TextView usernameView = (TextView) liveEndView.findViewById(R.id.tv_username);
        ImageView closeConfirmView =
                (ImageView) liveEndView.findViewById(R.id.img_finish_confirmed);
        TextView watchedCountView = (TextView) liveEndView.findViewById(R.id.txt_watched_count);
        usernameView.setText(EMClient.getInstance().getCurrentUser());
        watchedCountView.setText(watchedCount + "人看过");

        liveContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                liveEndView.setVisibility(View.GONE);
                liveContainer.setVisibility(View.VISIBLE);
                startPreview();
                if (isStarted) {
                    cameraView.startRecording();
                }
            }
        });
        closeConfirmView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });
    }

    void handleUpdateCountdown(final int count) {
        if (countdownView != null) {
            countdownView.setVisibility(View.VISIBLE);
            countdownView.setText(String.format("%d", count));
            ScaleAnimation scaleAnimation =
                    new ScaleAnimation(1.0f, 0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(COUNTDOWN_DELAY);
            scaleAnimation.setFillAfter(false);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {
                }

                @Override public void onAnimationEnd(Animation animation) {
                    countdownView.setVisibility(View.GONE);

                    if (count == COUNTDOWN_END_INDEX
                            //&& mEasyStreaming != null
                            && !isShutDownCountdown) {
                        EMClient.getInstance()
                                .chatroomManager()
                                .joinChatRoom(chatroomId, new EMValueCallBack<EMChatRoom>() {
                                    @Override public void onSuccess(EMChatRoom emChatRoom) {
                                        chatroom = emChatRoom;
                                        changeAnchorLive();
                                    }

                                    @Override public void onError(int i, String s) {
                                        showToast("加入聊天室失败");
                                    }
                                });
                    }
                }

                @Override public void onAnimationRepeat(Animation animation) {

                }
            });
            if (!isShutDownCountdown) {
                countdownView.startAnimation(scaleAnimation);
            } else {
                countdownView.setVisibility(View.GONE);
            }
        }
    }

    private void changeAnchorLive() {
        if(liveRoom.isLiving()) {
            startAnchorLive(liveRoom);
        }else {
            executeTask(new ThreadPoolManager.Task<LiveRoom>() {
                @Override
                public LiveRoom onRequest() throws HyphenateException {
                    return LiveManager.getInstance().startLive(liveId, EMClient.getInstance().getCurrentUser());
                }

                @Override
                public void onSuccess(LiveRoom liveRoom) {
                    startAnchorLive(liveRoom);
                }

                @Override
                public void onError(HyphenateException exception) {
                    showToast(exception.getMessage());
                }
            });
        }
    }

    private void startAnchorLive(LiveRoom liveRoom) {
        LiveHelper.saveLivingId(liveRoom.getId());
        addChatRoomChangeListener();
        onMessageListInit();
        showToast("直播开始！");
        //mEasyStreaming.startRecording();
        cameraView.startRecording();
        isStarted = true;
        cameraView.addStreamStateListener(mStreamStateListener);
        cameraView.addNetworkListener(mNetworkListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mEasyStreaming.onPause();
        cameraView.onPause();
        stopPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mEasyStreaming.onResume();
        startPreview();
        if (isStarted) {
            cameraView.startRecording();
        }
        if (isMessageListInited) messageView.refresh();
        EaseUI.getInstance().pushActivity(this);
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //stopPreview();

        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);

        // 把此activity 从foreground activity 列表里移除
        EaseUI.getInstance().popActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mEasyStreaming.onDestroy();
        try {
            cameraView.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (chatRoomChangeListener != null) {
            EMClient.getInstance()
                    .chatroomManager()
                    .removeChatRoomChangeListener(chatRoomChangeListener);
        }
        EMClient.getInstance().chatroomManager().leaveChatRoom(chatroomId);

//        executeRunnable(new Runnable() {
//            @Override public void run() {
//                try {
//                    LiveManager.getInstance().terminateLiveRoom(liveId);
//                } catch (LiveException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    UStreamStateListener mStreamStateListener = new UStreamStateListener() {
        //stream state
        @Override public void onStateChanged(UStreamStateListener.State state, Object o) {
        }

        @Override public void onStreamError(UStreamStateListener.Error error, Object extra) {
            switch (error) {
                case IOERROR:
                    if (isStarted && cameraView.isPreviewed()) {
                        LiveCameraView.getInstance().restart();
                    }
                    break;
            }
        }
    };

    UNetworkListener mNetworkListener = new UNetworkListener() {
        @Override public void onNetworkStateChanged(State state, Object o) {
            switch (state) {
                case NETWORK_SPEED:
                    break;
                case PUBLISH_STREAMING_TIME:
                    break;
                case DISCONNECT:
                    break;
                case RECONNECT:
                    //网络重新连接
                    if (isStarted && cameraView.isPreviewed()) {
                        LiveCameraView.getInstance().restart();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_bt_close :
                showDialog();
                break;
        }
    }

    private void showDialog() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.em_live_dialog_quit_title)
                .setConfirmButtonTxt(R.string.em_live_dialog_quit_btn_title)
                .setConfirmColor(R.color.em_color_warning)
                .setOnConfirmClickListener(new OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, Object bean) {
                        leaveRoom();
                    }
                })
                .build()
                .show(getSupportFragmentManager(), "dialog");
    }

    private void leaveRoom() {
        executeTask(new ThreadPoolManager.Task<Void>() {
            @Override
            public Void onRequest() throws HyphenateException {
                LiveManager.getInstance().closeLiveRoom(liveId, EMClient.getInstance().getCurrentUser());
                return null;
            }

            @Override
            public void onSuccess(Void aVoid) {
                LiveHelper.saveLivingId("");
                finish();
            }

            @Override
            public void onError(HyphenateException exception) {
                finish();
            }
        });
    }
}
