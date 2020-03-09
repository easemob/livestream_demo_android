package com.easemob.livedemo.ui.live.fragment;

import android.animation.ObjectAnimator;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easemob.chatroommessage.EmCustomMsgHelper;
import com.easemob.chatroommessage.MsgConstant;
import com.easemob.chatroommessage.OnCustomMsgReceiveListener;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.DemoMsgHelper;
import com.easemob.livedemo.common.LiveDataBus;
import com.easemob.livedemo.common.OnItemClickListener;
import com.easemob.chatroommessage.OnMsgCallBack;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.model.User;
import com.easemob.livedemo.ui.activity.BaseLiveFragment;
import com.easemob.livedemo.ui.activity.RoomUserDetailsDialog;
import com.easemob.livedemo.ui.activity.RoomUserManagementDialog;
import com.easemob.livedemo.ui.live.ChatRoomPresenter;
import com.easemob.livedemo.ui.live.adapter.MemberAvatarAdapter;
import com.easemob.livedemo.ui.live.viewmodels.LivingViewModel;
import com.easemob.livedemo.ui.viewmodels.UserManageViewModel;
import com.easemob.livedemo.ui.widget.PeriscopeLayout;
import com.easemob.livedemo.ui.widget.RoomMessagesView;
import com.easemob.livedemo.ui.widget.ShowGiveGiftView;
import com.easemob.livedemo.ui.widget.SingleBarrageView;
import com.easemob.livedemo.utils.Utils;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class LiveBaseFragment extends BaseLiveFragment implements View.OnClickListener, View.OnTouchListener, ChatRoomPresenter.OnChatRoomListener, OnCustomMsgReceiveListener {
    private static final int MAX_SIZE = 10;
    protected static final String TAG = "LiveActivity";
    @BindView(R.id.iv_icon)
    EaseImageView ivIcon;
    @BindView(R.id.message_view)
    RoomMessagesView messageView;
    @BindView(R.id.periscope_layout)
    PeriscopeLayout periscopeLayout;
    @BindView(R.id.bottom_bar)
    View bottomBar;
    @BindView(R.id.show_gift_view)
    ShowGiveGiftView barrageLayout;
    @BindView(R.id.horizontal_recycle_view)
    RecyclerView horizontalRecyclerView;
    @BindView(R.id.audience_num)
    TextView audienceNumView;
    //@BindView(R.id.new_messages_warn) ImageView newMsgNotifyImage;

    @BindView(R.id.user_manager_image)
    ImageView userManagerView;
    @BindView(R.id.switch_camera_image)
    ImageView switchCameraView;
    @BindView(R.id.like_image)
    ImageView likeImageView;
    @BindView(R.id.txt_live_id)
    TextView liveIdView;
    @BindView(R.id.tv_username)
    TextView usernameView;
    @BindView(R.id.tv_member_num)
    TextView tvMemberNum;
    @BindView(R.id.tv_attention)
    TextView tvAttention;
    @BindView(R.id.toolbar)
    ViewGroup toolbar;
    @BindView(R.id.live_receive_gift)
    ImageView liveReceiveGift;
    @BindView(R.id.barrageView)
    SingleBarrageView barrageView;

    protected LiveRoom liveRoom;
    protected EMChatRoom chatroom;
    /**
     * 环信聊天室id
     */
    protected String chatroomId = "";
    /**
     * ucloud直播id
     */
    protected String liveId = "";
    protected String anchorId;
    protected int watchedCount;
    LinkedList<String> memberList = new LinkedList<>();
    protected int membersCount;
    protected Handler handler = new Handler();
    private LinearLayoutManager layoutManager;
    private MemberAvatarAdapter avatarAdapter;
    protected boolean isMessageListInited;
    protected ChatRoomPresenter presenter;
    protected LivingViewModel viewModel;
    private UserManageViewModel userManageViewModel;
    private long joinTime;

    @Override
    protected void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            liveRoom = (LiveRoom) bundle.getSerializable("liveroom");
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        liveId = liveRoom.getId();
        chatroomId = liveRoom.getId();
        anchorId = liveRoom.getOwner();

        //设置相关的直播间信息
        EmCustomMsgHelper.getInstance().setChatRoomInfo(chatroomId, EMClient.getInstance().getCurrentUser());

        usernameView.setText(anchorId);
        liveIdView.setText(getString(R.string.em_live_room_id, liveId));
        audienceNumView.setText(String.valueOf(liveRoom.getAudienceNum()));
        watchedCount = liveRoom.getAudienceNum();
        tvMemberNum.setText(String.valueOf(watchedCount));

        presenter = new ChatRoomPresenter(mContext, chatroomId);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        viewModel = new ViewModelProvider(this).get(LivingViewModel.class);
        userManageViewModel = new ViewModelProvider(this).get(UserManageViewModel.class);

    }

    @Override
    protected void initListener() {
        super.initListener();
        tvMemberNum.setOnClickListener(this);
        toolbar.setOnClickListener(this);
        liveReceiveGift.setOnClickListener(this);
        getView().setOnTouchListener(this);
        presenter.setOnChatRoomListener(this);
        EmCustomMsgHelper.getInstance().setOnCustomMsgReceiveListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        joinTime = System.currentTimeMillis();
        DemoMsgHelper.getInstance().init(chatroomId);
        barrageView.initBarrage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_member_num :
                skipToListDialog();
                break;
            case R.id.toolbar :
                AnchorClick();
                break;
            case R.id.live_receive_gift :
                onGiftClick();
                break;
        }
    }

    @OnClick(R.id.comment_image) void onCommentImageClick() {
        showInputView();
    }

    private void showInputView() {
        bottomBar.setVisibility(View.INVISIBLE);
        messageView.setShowInputView(true);
        messageView.getInputView().requestFocus();
        messageView.getInputView().requestFocusFromTouch();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                Utils.showKeyboard(messageView.getInputView());
            }
        }, 200);
    }

    /**
     * 点击礼物的事件
     */
    protected void onGiftClick() {}

    /**
     * 点击头像
     */
    protected void AnchorClick(){ }

    /**
     * 跳转到member列表
     */
    protected void skipToListDialog() {}

    /**
     * 展示观众列表（主播）
     */
    protected void showUserList() {
        RoomUserManagementDialog dialog = (RoomUserManagementDialog) getChildFragmentManager().findFragmentByTag("RoomUserManagementDialog");
        if(dialog == null) {
            dialog = new RoomUserManagementDialog(chatroomId);
        }
        dialog.show(getChildFragmentManager(), "RoomUserManagementDialog");
    }

    protected void showPraise(final int count){
        requireActivity().runOnUiThread(() -> {
            for(int i = 0; i < count; i++){
                if(!mContext.isFinishing())
                    periscopeLayout.addHeart();
            }
        });
    }

    /**
     * add chat room change listener
     */
    protected void addChatRoomChangeListener() {
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(presenter);
    }

    private synchronized void onRoomMemberAdded(String name) {
        watchedCount++;
        if (!memberList.contains(name)) {
            membersCount++;
            if(memberList.size() >= MAX_SIZE)
                memberList.removeLast();
            memberList.addFirst(name);
            presenter.showMemberChangeEvent(name, getString(R.string.em_live_msg_member_add));
            EMLog.d(TAG, name + "added");
            requireActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    audienceNumView.setText(String.valueOf(membersCount));
                    tvMemberNum.setText(String.valueOf(watchedCount));
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void notifyDataSetChanged(){
        if(memberList.size() > 4){
            layoutManager.setStackFromEnd(false);
        }else{
            layoutManager.setStackFromEnd(true);
        }
        avatarAdapter.setData(memberList);
    }


    private synchronized void onRoomMemberExited(final String name) {
        memberList.remove(name);
        membersCount--;
        EMLog.e(TAG, name + "exited");
        requireActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                audienceNumView.setText(String.valueOf(membersCount));
                tvMemberNum.setText(String.valueOf(watchedCount));
                horizontalRecyclerView.getAdapter().notifyDataSetChanged();
                if(name.equals(anchorId)){
                    mContext.showLongToast("主播已结束直播");
                    LiveDataBus.get().with(DemoConstants.FRESH_LIVE_LIST).setValue(true);
                }
            }
        });
    }

    protected void onMessageListInit() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                messageView.init(chatroomId);
                messageView.setMessageViewListener(new RoomMessagesView.MessageViewListener() {
                    @Override
                    public void onMessageSend(String content, boolean isBarrageMsg) {
                        DemoMsgHelper.getInstance().sendMsg(content, isBarrageMsg, new OnMsgCallBack() {
                            @Override
                            public void onSuccess(EMMessage message) {
                                //刷新消息列表
                                messageView.refreshSelectLast();

                                if(isBarrageMsg) {
                                    barrageView.addData(message);
                                }
                            }

                            @Override
                            public void onError(int code, String error) {
                                mContext.showToast("消息发送失败！errorCode = "+code+"; errorMsg = "+error);
                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }
                        });
                    }

                    @Override
                    public void onItemClickListener(final EMMessage message) {
                        //if(message.getFrom().equals(EMClient.getInstance().getCurrentUser())){
                        //    return;
                        //}
                        String clickUsername = message.getFrom();
                        showUserDetailsDialog(clickUsername);
                    }

                    @Override
                    public void onHiderBottomBar() {
                        bottomBar.setVisibility(View.VISIBLE);
                    }
                });
                messageView.setVisibility(View.VISIBLE);
                bottomBar.setVisibility(View.VISIBLE);
                if(!chatroom.getAdminList().contains(EMClient.getInstance().getCurrentUser())
                        && !chatroom.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
                    userManagerView.setVisibility(View.INVISIBLE);
                }
                isMessageListInited = true;
                updateUnreadMsgView();
                showMemberList();
            }
        });
    }

    protected void showUserDetailsDialog(String username) {
        RoomUserDetailsDialog dialog = (RoomUserDetailsDialog) getChildFragmentManager().findFragmentByTag("RoomUserDetailsDialog");
        if(dialog == null) {
            dialog = RoomUserDetailsDialog.newInstance(username, liveRoom);
        }
        dialog.show(getChildFragmentManager(), "RoomUserDetailsDialog");
        dialog.setManageEventListener(new RoomUserDetailsDialog.RoomManageEventListener() {
            @Override public void onKickMember(String username) {
                onRoomMemberExited(username);
            }

            @Override public void onAddBlacklist(String username) {
                onRoomMemberExited(username);
            }
        });
    }

    public void updateUnreadMsgView() {
        //if(isMessageListInited) {
        //  for (EMConversation conversation : EMClient.getInstance()
        //      .chatManager()
        //      .getAllConversations()
        //      .values()) {
        //    if (conversation.getType() == EMConversation.EMConversationType.Chat
        //        && conversation.getUnreadMsgCount() > 0) {
        //      newMsgNotifyImage.setVisibility(View.VISIBLE);
        //      return;
        //    }
        //  }
        //  newMsgNotifyImage.setVisibility(View.INVISIBLE);
        //}
    }

    private void showMemberList() {
        horizontalRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(layoutManager);
        avatarAdapter = new MemberAvatarAdapter();
        horizontalRecyclerView.setAdapter(avatarAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setSize((int) EaseCommonUtils.dip2px(mContext, 5), 0);
        decoration.setDrawable(drawable);
        horizontalRecyclerView.addItemDecoration(decoration);
        avatarAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String item = avatarAdapter.getItem(position);
                skipToListDialog();
            }
        });

        userManageViewModel.getObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    memberList.clear();
                    if (data.contains(chatroom.getOwner())) {
                        data.remove(chatroom.getOwner());
                    }
                    if(data.size() > MAX_SIZE) {
                        for (int i = 0; i < MAX_SIZE; i++){
                            memberList.add(i, data.get(i));
                        }
                    }else {
                        memberList.addAll(data);
                    }
                    int size = chatroom.getMemberCount();
                    audienceNumView.setText(String.valueOf(size));
                    membersCount = size;
                    //观看人数不包含主播
                    watchedCount = membersCount;
                    tvMemberNum.setText(String.valueOf(watchedCount));
                    notifyDataSetChanged();
                }
            });
        });
        userManageViewModel.getMembers(chatroomId);
    }

    private float preX, preY;

    /**
     * 向左滑动屏幕
     * @param startX
     * @param endY
     */
    protected void slideToLeft(int startX, float endY) {

    }

    /**
     * 向右滑动屏幕
     * @param startX
     * @param endX
     */
    protected void slideToRight(float startX, float endX) {

    }

    protected void startAnimation(View target, float startX, float endX) {
        if(target == null) {
            return;
        }
        if(target instanceof ViewGroup) {
            float x = ((ViewGroup) target).getChildAt(0).getX();
            if(x != startX) {
                return;
            }
            View child = ((ViewGroup) target).getChildAt(0);
            ObjectAnimator animator = ObjectAnimator.ofFloat(child, "translationX", startX, endX);
            animator.setDuration(500);
            animator.start();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN :
                preX = ev.getX();
                preY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float curX = ev.getX();
                float curY = ev.getY();
                float x = curX - preX;
                float y = curY - preY;
                if(Math.abs(x) > Math.abs(y) && Math.abs(x) > 20) {
                    float[] screenInfo = EaseCommonUtils.getScreenInfo(mContext);
                    if(x > 0) {// 向右滑动
                        slideToLeft(0, screenInfo[0]);
                    }else {// 向左滑动
                        slideToRight(screenInfo[0], 0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    @Override
    public void onChatRoomMemberAdded(String participant) {
        onRoomMemberAdded(participant);
    }

    @Override
    public void onChatRoomMemberExited(String participant) {
        onRoomMemberExited(participant);
    }

    @Override
    public void onMessageReceived() {
        messageView.refreshSelectLast();
    }

    @Override
    public void onMessageSelectLast() {
        messageView.refreshSelectLast();
    }

    @Override
    public void onMessageChanged() {
        if (isMessageListInited) {
            messageView.refresh();
        }
    }

    @Override
    public void onReceiveGiftMsg(EMMessage message) {
        DemoHelper.saveGiftInfo(message);
        //加入直播间之前的礼物消息不再展示
        if(message.getMsgTime() < joinTime - 2000) {
            return;
        }
        Map<String, String> params = EmCustomMsgHelper.getInstance().getCustomMsgParams(message);
        Set<String> keySet = params.keySet();
        if(keySet.contains(MsgConstant.CUSTOM_GIFT_KEY_ID)) {
            GiftBean bean = DemoHelper.getGiftById(params.get(MsgConstant.CUSTOM_GIFT_KEY_ID));
            User user = new User();
            user.setUsername(message.getFrom());
            bean.setUser(user);
            if(keySet.contains(MsgConstant.CUSTOM_GIFT_KEY_NUM)) {
                bean.setNum(Integer.valueOf(params.get(MsgConstant.CUSTOM_GIFT_KEY_NUM)));
                ThreadManager.getInstance().runOnMainThread(()-> {
                    barrageLayout.showGift(bean);
                });

            }
        }
    }

    @Override
    public void onReceivePraiseMsg(EMMessage message) {
        DemoHelper.saveLikeInfo(message);
        Map<String, String> params = EmCustomMsgHelper.getInstance().getCustomMsgParams(message);
        Set<String> keySet = params.keySet();
        if(keySet.contains(MsgConstant.CUSTOM_LIKE_KEY_NUM)) {
            showPraise(Integer.valueOf(params.get(MsgConstant.CUSTOM_LIKE_KEY_NUM)));
        }

    }

    /**
     * 收到弹幕消息
     * @param message
     */
    @Override
    public void onReceiveBarrageMsg(EMMessage message) {
        ThreadManager.getInstance().runOnMainThread(()-> {
            barrageView.addData(message);
        });
    }
}
