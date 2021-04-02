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
import com.easemob.custommessage.OnMsgCallBack;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.LiveDataBus;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnConfirmClickListener;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.data.model.LiveRoom;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EMLog;

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
    /**
     * 是否是切换owner的操作，如果是切换owner的操作，则不调用退出聊天室的逻辑。防止新页面刚加入直播间，正在销毁的页面又调用了退出直播间的操作，导致聊天室出现异常。
     */
    private boolean isSwitchOwner;//是否是切换owner的操作

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
        if(dialog.isAdded()) {
            return;
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
        if(TextUtils.equals(liveRoom.getId(), chatRoomId) && TextUtils.equals(newOwner, EMClient.getInstance().getCurrentUser())) {
            isSwitchOwner = true;
            if(liveListener != null) {
                liveListener.onRoomOwnerChangedToCurrentUser(chatRoomId, newOwner);
            }
        }
    }

    private void showGiftDialog() {
        LiveGiftDialog dialog = (LiveGiftDialog) getChildFragmentManager().findFragmentByTag("live_gift");
        if(dialog == null) {
            dialog = LiveGiftDialog.getNewInstance();
        }
        if(dialog.isAdded()) {
           return;
        }
        dialog.show(getChildFragmentManager(), "live_gift");
        dialog.setOnConfirmClickListener(new OnConfirmClickListener() {
            @Override
            public void onConfirmClick(View view, Object bean) {
                if(bean instanceof GiftBean) {
                    presenter.sendGiftMsg((GiftBean) bean, new OnMsgCallBack() {
                        @Override
                        public void onSuccess(EMMessage message) {
                            ThreadManager.getInstance().runOnMainThread(()-> {
                                barrageLayout.showGift((GiftBean) bean);
                            });
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
                    //如果当前用户是主播，则进入主播房间
                    if(DemoHelper.isOwner(data.getOwner())) {
                        isSwitchOwner = true;
                        if(liveListener != null) {
                            liveListener.onRoomOwnerChangedToCurrentUser(data.getChatroomId(), data.getOwner());
                        }
                        return;
                    }
                    LiveAudienceFragment.this.liveRoom = data;
                    if(DemoHelper.isLiving(data.getStatus())) {
                        //直播正在进行
                        if(liveListener != null) {
                            liveListener.onLiveOngoing(data);
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
        viewModel.getLiveRoomDetails(liveRoom.getId());
    }

    private void joinChatRoom() {
        //loadingLayout.setVisibility(View.INVISIBLE);
        EMClient.getInstance()
                .chatroomManager()
                .joinChatRoom(chatroomId, new EMValueCallBack<EMChatRoom>() {
                    @Override public void onSuccess(EMChatRoom emChatRoom) {
                        EMLog.d(TAG, "audience join chat room success");
                        chatroom = emChatRoom;
                        addChatRoomChangeListener();
                        onMessageListInit();
                        startCycleRefresh();
                        //postUserChangeEvent(StatisticsType.JOIN, EMClient.getInstance().getCurrentUser());
                    }

                    @Override public void onError(int i, String s) {
                        EMLog.d(TAG, "audience join chat room fail message: "+s);
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
            if(isMessageListInited && !isSwitchOwner) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(chatroomId);
                isMessageListInited = false;
                EMLog.d(TAG, "audience leave chat room");
                //postUserChangeEvent(StatisticsType.LEAVE, EMClient.getInstance().getCurrentUser());
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
        void onLiveOngoing(LiveRoom data);
        void onLiveClosed();
        void onRoomOwnerChangedToCurrentUser(String chatRoomId, String newOwner);
    }
}
