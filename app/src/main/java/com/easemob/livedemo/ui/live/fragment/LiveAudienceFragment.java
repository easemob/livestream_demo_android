package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.LiveDataBus;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnConfirmClickListener;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.live.LiveAnchorActivity;
import com.easemob.qiniu_sdk.OnCallBack;
import com.easemob.qiniu_sdk.PushStreamHelper;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LiveAudienceFragment extends LiveBaseFragment {
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

    private Unbinder unbinder;
    private OnLiveListener liveListener;
    int praiseCount;
    final int praiseSendDelay = 4 * 1000;
    private Thread sendPraiseThread;

    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_live_audience;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        switchCameraView.setVisibility(View.GONE);
        likeImageView.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(liveRoom.getCover()).placeholder(R.color.placeholder).into(coverView);

    }

    @Override
    protected void initListener() {
        super.initListener();
        tvAttention.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        usernameView.setText(DemoHelper.getNickName(liveRoom.getOwner()));
        ivIcon.setImageResource(DemoHelper.getAvatarResource(liveRoom.getOwner()));
        getLiveRoomDetail();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_attention :

                break;
        }
    }

    @OnClick(R.id.img_bt_close) void close() {
        mContext.finish();
    }

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
                    while(mContext != null && !mContext.isFinishing()){
                        int count = 0;
                        synchronized (LiveAudienceFragment.this){
                            count = praiseCount;
                            praiseCount = 0;
                        }
                        if(count > 0) {
                            presenter.sendPraiseMessage(count);
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

    @Override
    protected void skipToListDialog() {
        super.skipToListDialog();
        LiveMemberListDialog dialog = (LiveMemberListDialog) getChildFragmentManager().findFragmentByTag("liveMember");
        if(dialog == null) {
            dialog = LiveMemberListDialog.getNewInstance(chatroomId);
        }
        dialog.show(getChildFragmentManager(), "liveMember");
        dialog.setOnItemClickListener(new LiveMemberListDialog.OnMemberItemClickListener() {
            @Override
            public void OnMemberItemClick(View view, int position, String member) {
//                showUserDetailsDialog(member);
            }
        });
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

    @Override
    protected void showPraise(int count) {
        //观众端不展示动画
    }

    @Override
    public void onChatRoomOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {
        super.onChatRoomOwnerChanged(chatRoomId, newOwner, oldOwner);
        // 如果直播间主播被调整为自己
        if(TextUtils.equals(chatroomId, chatRoomId) && TextUtils.equals(newOwner, EMClient.getInstance().getCurrentUser())) {
            PushStreamHelper.getInstance().getPublishUrl(EMClient.getInstance().getCurrentUser(), new OnCallBack<String>() {
                @Override
                public void onSuccess(String data) {
                    LiveAnchorActivity.actionStart(mContext, liveRoom, data);
                    mContext.finish();
                }

                @Override
                public void onFail(String message) {

                }
            });

        }
    }

    private void showGiftDialog() {
        LiveGiftDialog dialog = (LiveGiftDialog) getChildFragmentManager().findFragmentByTag("live_gift");
        if(dialog == null) {
            dialog = LiveGiftDialog.getNewInstance();
        }
        dialog.show(getChildFragmentManager(), "live_gift");
        dialog.setOnConfirmClickListener(new OnConfirmClickListener() {
            @Override
            public void onConfirmClick(View view, Object bean) {
                if(bean instanceof GiftBean) {
                    presenter.sendGiftMsg((GiftBean) bean, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            ThreadManager.getInstance().runOnMainThread(()-> {
                                barrageLayout.showGift((GiftBean) bean);
                            });
                        }

                        @Override
                        public void onError(int code, String error) {
                            mContext.showToast("errorCode = " + code + "; errorMsg = "+error);
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }
                    });

                }
            }
        });
    }

    private void getLiveRoomDetail() {
        viewModel.getRoomDetailObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<LiveRoom>() {
                @Override
                public void onSuccess(LiveRoom data) {
                    LiveAudienceFragment.this.liveRoom = liveRoom;
                    if(DemoHelper.isLiving(liveRoom.getStatus())) {
                        //直播正在进行
                        if(liveListener != null) {
                            liveListener.onLiveOngoing();
                        }
                        messageView.getInputView().requestFocus();
                        messageView.getInputView().requestFocusFromTouch();
                        joinChatRoom();

                    }else {
                        mContext.showLongToast("直播已结束");
                        if(liveListener != null) {
                            liveListener.onLiveClosed();
                        }
                    }
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    loadingLayout.setVisibility(View.INVISIBLE);
                }
            });
        });

        viewModel.getLiveRoomDetails(liveId);
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
                            mContext.showLongToast("你没有权限加入此房间");
                            mContext.finish();
                        }else if(i == EMError.CHATROOM_MEMBERS_FULL){
                            mContext.showLongToast("房间成员已满");
                            mContext.finish();
                        }else {
                            mContext.showLongToast("加入聊天室失败: " +s);
                        }
                    }
                });
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

        if(mContext.isFinishing()) {
            LiveDataBus.get().with(DemoConstants.FRESH_LIVE_LIST).setValue(true);
            if(isMessageListInited) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(chatroomId);

                //postUserChangeEvent(StatisticsType.LEAVE, EMClient.getInstance().getCurrentUser());
            }

            if (presenter != null) {
                EMClient.getInstance()
                        .chatroomManager()
                        .removeChatRoomListener(presenter);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setOnLiveListener(OnLiveListener liveListener) {
        this.liveListener = liveListener;
    }

    public interface OnLiveListener {
        void onLiveOngoing();
        void onLiveClosed();
    }
}
