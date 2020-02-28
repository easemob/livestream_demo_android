package com.easemob.livedemo.ui.live.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.common.LiveDataBus;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnConfirmClickListener;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.common.enums.Status;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.restapi.LiveManager;
import com.easemob.livedemo.ui.activity.AssociateLiveRoomActivity;
import com.easemob.livedemo.ui.activity.SimpleDialogFragment;
import com.easemob.livedemo.ui.live.viewmodels.LivingViewModel;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LiveAnchorFragment extends LiveBaseFragment {
    public static final int MSG_UPDATE_COUNTDOWN = 1;
    public static final int COUNTDOWN_DELAY = 1000;
    public static final int COUNTDOWN_START_INDEX = 3;
    public static final int COUNTDOWN_END_INDEX = 1;
    @BindView(R.id.countdown_txtv)
    TextView countdownView;
    @BindView(R.id.finish_frame)
    ViewStub liveEndLayout;
    @BindView(R.id.group_gift_info)
    Group groupGiftInfo;
    @BindView(R.id.tv_gift_num)
    TextView tvGiftNum;
    @BindView(R.id.tv_like_num)
    TextView tvLikeNum;
    @BindView(R.id.img_bt_close)
    ImageView imgBtClose;
    private Unbinder unbinder;
    protected boolean isShutDownCountdown = false;
    boolean isStarted;
    private OnCameraListener cameraListener;
    private Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_COUNTDOWN:
                    handleUpdateCountdown(msg.arg1);
                    break;
            }
        }
    };
    private LivingViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_live_anchor;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        usernameView.setText("");
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
        viewModel = new ViewModelProvider(this).get(LivingViewModel.class);
        startLive();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_bt_close :
                showDialog(new OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, Object bean) {

                    }
                });
                break;
        }
    }

    /**
     * 切换摄像头
     */
    @OnClick(R.id.switch_camera_image)
    void switchCamera() {
        //mEasyStreaming.switchCamera();
        if(cameraListener != null) {
            cameraListener.switchCamera();
        }
    }

    @Override
    protected void onGiftClick() {
        super.onGiftClick();
        showGiftDialog();
    }

    @Override
    protected void slideToLeft(int startX, float endX) {
        super.slideToLeft(startX, endX);
        Log.e("TAG", "view.child count = "+((ViewGroup)getView()).getChildCount());
        startAnimation(getView(), startX, endX);
    }

    @Override
    protected void slideToRight(float startX, float endX) {
        super.slideToRight(startX, endX);
        startAnimation(getView(), startX, endX);
    }

    @Override
    protected void skipToListDialog() {
        super.skipToListDialog();
        showUserList();
    }

    private void showGiftDialog() {
        LiveGiftStatisticsDialog.getNewInstance().show(getChildFragmentManager(), "git_statistics");
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
                                        ThreadManager.getInstance().runOnMainThread(LiveAnchorFragment.this::getLiveRoomDetail);
                                    }

                                    @Override public void onError(int i, String s) {
                                        mContext.showToast("加入聊天室失败");
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

    private void getLiveRoomDetail() {
        viewModel.getRoomDetailObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>() {
                @Override
                public void onSuccess(LiveRoom data) {
                    LiveAnchorFragment.this.liveRoom = liveRoom;
                    changeAnchorLive();
                }
            });
        });
        viewModel.getLiveRoomDetails(liveId);
    }

    private void changeAnchorLive() {
        viewModel.getChangeObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>() {
                @Override
                public void onSuccess(LiveRoom data) {
                    startAnchorLive(liveRoom);
                }
            });
        });
        if(liveRoom.isLiving()) {
            startAnchorLive(liveRoom);
        }else {
            viewModel.changeLiveStatus(liveId, EMClient.getInstance().getCurrentUser(), "ongoing");
        }
    }

    private void startAnchorLive(LiveRoom liveRoom) {
        DemoHelper.saveLivingId(liveRoom.getId());
        usernameView.setText(DemoHelper.getNickName(EMClient.getInstance().getCurrentUser()));
        ivIcon.setImageResource(DemoHelper.getAvatarResource(EMClient.getInstance().getCurrentUser()));
        addChatRoomChangeListener();
        onMessageListInit();
        mContext.showToast("直播开始！");
        if(cameraListener != null) {
            cameraListener.onStartCamera();
        }
    }

    private void showDialog(OnConfirmClickListener listener) {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.em_live_dialog_quit_title)
                .setConfirmButtonTxt(R.string.em_live_dialog_quit_btn_title)
                .setConfirmColor(R.color.em_color_warning)
                .setOnConfirmClickListener(new OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, Object bean) {
                        stopLiving();
                        LiveDataBus.get().with(DemoConstants.FRESH_LIVE_LIST).setValue(true);
                        if(listener != null) {
                            listener.onConfirmClick(view, bean);
                        }
                    }
                })
                .build()
                .show(getChildFragmentManager(), "dialog");
    }

    /**
     * 停止直播
     */
    private void stopLiving() {
        if(cameraListener != null) {
            cameraListener.onStopCamera();
        }
        leaveRoom();
    }

    private void leaveRoom() {
        viewModel.getCloseObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>() {
                @Override
                public void onSuccess(LiveRoom data) {
                    DemoHelper.saveLivingId("");
                    mContext.finish();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    mContext.finish();
                }
            });
        });
        viewModel.closeLive(liveId, EMClient.getInstance().getCurrentUser());
    }

    public void setOnCameraListener(OnCameraListener listener) {
        this.cameraListener = listener;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMessageListInited) messageView.refresh();
        EaseUI.getInstance().pushActivity(mContext);
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(presenter);
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(presenter);

        // 把此activity 从foreground activity 列表里移除
        EaseUI.getInstance().popActivity(mContext);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (presenter != null) {
            EMClient.getInstance()
                    .chatroomManager()
                    .removeChatRoomListener(presenter);
        }
        EMClient.getInstance().chatroomManager().leaveChatRoom(chatroomId);
    }

    @Override
    public void onBackPressed() {
        showDialog(new OnConfirmClickListener() {
            @Override
            public void onConfirmClick(View view, Object bean) {
                mContext.onBackPressed();
            }
        });
    }

    public interface OnCameraListener {
        void onStartCamera();
        void switchCamera();
        void onStopCamera();
    }
}
