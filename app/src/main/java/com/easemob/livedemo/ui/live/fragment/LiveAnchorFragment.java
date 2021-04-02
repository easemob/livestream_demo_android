package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.easemob.livedemo.common.DemoMsgHelper;
import com.easemob.livedemo.common.LiveDataBus;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnConfirmClickListener;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.common.db.dao.ReceiveGiftDao;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.live.LiveAudienceActivity;
import com.easemob.livedemo.ui.other.fragment.SimpleDialogFragment;
import com.easemob.livedemo.ui.live.viewmodels.LivingViewModel;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

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
    private LivingViewModel viewModel;
    private boolean isOnGoing;
    private boolean reChangeLiveStatus;
    //是否将owner移交给其他人，此种场景，不进行退出聊天室的操作。
    private boolean isSwitchOwnerToOther;

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
        switchCameraView.setVisibility(View.VISIBLE);
        groupGiftInfo.setVisibility(View.VISIBLE);

        ReceiveGiftDao giftDao = DemoHelper.getReceiveGiftDao();
        if(giftDao != null) {
            int totalNum = giftDao.loadGiftTotalNum(DemoMsgHelper.getInstance().getCurrentRoomId());
            tvGiftNum.setText(getString(R.string.em_live_anchor_receive_gift_info, DemoHelper.formatNum(totalNum)));
        }else {
            tvGiftNum.setText(getString(R.string.em_live_anchor_receive_gift_info, DemoHelper.formatNum(0)));
        }

        int likeNum = DemoHelper.getLikeNum(liveId);
        tvLikeNum.setText(getString(R.string.em_live_anchor_like_info, DemoHelper.formatNum(likeNum)));

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
        LiveDataBus.get().with(DemoConstants.REFRESH_GIFT_LIST, Boolean.class)
                .observe(getViewLifecycleOwner(), response -> {
                    if(response != null && response) {
                        int totalNum = DemoHelper.getReceiveGiftDao().loadGiftTotalNum(DemoMsgHelper.getInstance().getCurrentRoomId());
                        tvGiftNum.setText(getString(R.string.em_live_anchor_receive_gift_info, DemoHelper.formatNum(totalNum)));
                    }
                });

        LiveDataBus.get().with(DemoConstants.REFRESH_LIKE_NUM, Boolean.class)
                .observe(getViewLifecycleOwner(), response -> {
                    if(response != null && response) {
                        int likeNum = DemoHelper.getLikeNum(liveId);
                        tvLikeNum.setText(getString(R.string.em_live_anchor_like_info, DemoHelper.formatNum(likeNum)));
                    }
                });
        LiveDataBus.get().with(DemoConstants.FINISH_LIVE, Boolean.class)
                .observe(getViewLifecycleOwner(), response -> {
                    if(response != null && response) {
                        stopLiving();
                    }
                });
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
    protected void skipToListDialog() {
        super.skipToListDialog();
        try {
            showUserList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showGiftDialog() {
        LiveGiftStatisticsDialog dialog = (LiveGiftStatisticsDialog) getChildFragmentManager().findFragmentByTag("git_statistics");
        if(dialog == null) {
            dialog = LiveGiftStatisticsDialog.getNewInstance();
        }
        if(dialog.isAdded()) {
            return;
        }
        dialog.show(getChildFragmentManager(), "git_statistics");
    }

    @Override
    protected void showUserDetailsDialog(String username) {
        if(TextUtils.equals(username, liveRoom.getOwner())) {
            return;
        }
        RoomManageUserDialog fragment = (RoomManageUserDialog) getChildFragmentManager().findFragmentByTag("RoomManageUserDialog");
        if(fragment == null) {
            fragment = RoomManageUserDialog.getNewInstance(chatroomId, username);
        }
        if(fragment.isAdded()) {
            return;
        }
        fragment.show(getChildFragmentManager(), "RoomManageUserDialog");
    }

    @Override
    public void onChatRoomOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {
        super.onChatRoomOwnerChanged(chatRoomId, newOwner, oldOwner);
        EMLog.d(TAG, "onChatRoomOwnerChanged oldOwner: "+oldOwner + " newOwner: "+newOwner + " current user: "+EMClient.getInstance().getCurrentUser());
        if(TextUtils.equals(chatroomId, chatRoomId) && !TextUtils.equals(newOwner, EMClient.getInstance().getCurrentUser())) {
            isSwitchOwnerToOther = true;
            DemoHelper.removeTarget(chatRoomId);
            DemoHelper.removeSaveLivingId();
            if(cameraListener != null) {
                cameraListener.onRoomOwnerChangedToOtherUser(chatRoomId, newOwner);
            }
        }
    }

    @Override
    public void handleHandlerMessage(Message msg) {
        super.handleHandlerMessage(msg);
        switch (msg.what) {
            case MSG_UPDATE_COUNTDOWN:
                handleUpdateCountdown(msg.arg1);
                break;
        }
    }

    @Override
    protected void checkLiveStatus(LiveRoom data) {
        super.checkLiveStatus(data);
        //页面没有销毁，直播一直在进行，但是直播状态不是"ongoing"状态
        if(mContext != null && !mContext.isFinishing() && isOnGoing && DemoHelper.isOwner(data.getOwner()) && !data.isLiving()) {
            restartAnchorLive();
        }
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
                            && !isShutDownCountdown && mContext != null && !mContext.isFinishing()) {
                        joinChatRoom();
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

    private void joinChatRoom() {
        EMClient.getInstance()
                .chatroomManager()
                .joinChatRoom(chatroomId, new EMValueCallBack<EMChatRoom>() {
                    @Override public void onSuccess(EMChatRoom emChatRoom) {
                        EMLog.d(TAG, "joinChatRoom success room id: "+emChatRoom.getId() + " owner: "+emChatRoom.getOwner());
                        chatroom = emChatRoom;
                        ThreadManager.getInstance().runOnMainThread(LiveAnchorFragment.this::getLiveRoomDetail);
                    }

                    @Override public void onError(int i, String s) {
                        EMLog.d(TAG, "joinChatRoom fail message: "+s);
                        mContext.showToast("加入聊天室失败");
                        mContext.finish();
                    }
                });
    }

    /**
     * 这里判断是否是owner，需要使用上一步的chatroom对象进行判断，防止通过rest接口获取的数据不是最新的，导致判断出错。
     */
    private void getLiveRoomDetail() {
        viewModel.getRoomDetailObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>() {
                @Override
                public void onSuccess(LiveRoom data) {
                    //需要保证聊天室和直播间的主播均不是当前用户
                    if(data.isLiving() && (!DemoHelper.isOwner(chatroom.getOwner()) && !DemoHelper.isOwner(data.getOwner()))) {
                        EMLog.d(TAG, "getLiveRoomDetails 主播正在直播 owner: "+chatroom.getOwner());
                        //退出房间
                        mContext.showToast(getString(R.string.em_live_list_warning));
                        exitRoom();
                    }else {
                        EMLog.d(TAG, "getLiveRoomDetails 准备开始直播");
                        LiveAnchorFragment.this.liveRoom = data;
                        changeAnchorLive();
                    }

                }
            });
        });
        viewModel.getLiveRoomDetails(liveId);
    }

    private void exitRoom() {
        DemoHelper.removeTarget(liveId);
        DemoHelper.removeSaveLivingId();
        mContext.finish();
    }

    private void changeAnchorLive() {
        reChangeLiveStatus = false;
        changeAnchorLiveByServer();
    }

    private void restartAnchorLive() {
        EMLog.d(TAG, "restartAnchorLive");
        reChangeLiveStatus = true;
        changeAnchorLiveByServer();
    }

    private void changeAnchorLiveByServer() {
        viewModel.getChangeObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>() {
                @Override
                public void onSuccess(LiveRoom data) {
                    EMLog.d(TAG, "changeLiveStatus success");
                    LiveDataBus.get().with(DemoConstants.FRESH_LIVE_LIST).setValue(true);
                    if(!reChangeLiveStatus) {
                        //开始直播，则开始统计点赞及礼物统计，实际开发中，应该由服务器进行统计，此处仅为展示用
                        DemoHelper.saveLikeNum(data.getId(), 0);
                        DemoHelper.getReceiveGiftDao().clearData(DemoMsgHelper.getInstance().getCurrentRoomId());
                        startAnchorLive(liveRoom);
                    }
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    exitRoom();
                }
            });
        });
        if(liveRoom.isLiving() && !reChangeLiveStatus) {
            EMLog.d(TAG, "restartAnchorLive 直接开播");
            startAnchorLive(liveRoom);
        }else {
            EMLog.d(TAG, "restartAnchorLive 调用接口");
            viewModel.changeLiveStatus(liveId, EMClient.getInstance().getCurrentUser(), "ongoing");
        }
    }

    private void startAnchorLive(LiveRoom liveRoom) {
        isOnGoing = true;
        DemoHelper.saveLivingId(liveRoom.getId());
        usernameView.setText(DemoHelper.getNickName(EMClient.getInstance().getCurrentUser()));
        Log.e("TAG", "image resource = "+DemoHelper.getAvatarResource(EMClient.getInstance().getCurrentUser()));
//        ivIcon.setImageResource(DemoHelper.getAvatarResource(EMClient.getInstance().getCurrentUser()));
        ivIcon.setImageResource(R.drawable.em_avatar_1);
        addChatRoomChangeListener();
        onMessageListInit();
        mContext.showToast("直播开始！");
        if(cameraListener != null) {
            cameraListener.onStartCamera();
        }
        startCycleRefresh();
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
        if(isOnGoing) {
            isOnGoing = false;
            leaveRoom();
        }
    }

    private void leaveRoom() {
        viewModel.getCloseObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>() {
                @Override
                public void onSuccess(LiveRoom data) {
                    DemoHelper.removeTarget(data.getId());
                    DemoHelper.removeSaveLivingId();
                    if(DemoHelper.getReceiveGiftDao() != null) {
                        DemoHelper.getReceiveGiftDao().clearData(DemoMsgHelper.getInstance().getCurrentRoomId());
                    }
                    DemoHelper.saveLikeNum(data.getId(), 0);
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
        if(mContext.isFinishing()) {
            LiveDataBus.get().with(DemoConstants.FRESH_LIVE_LIST).setValue(true);
            if(isMessageListInited && !isSwitchOwnerToOther) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(chatroomId);
                isMessageListInited = false;
                EMLog.d(TAG, "leave chat room id: "+chatroomId);
            }
        }
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
        void onRoomOwnerChangedToOtherUser(String chatRoomId, String newOwner);
    }
}
