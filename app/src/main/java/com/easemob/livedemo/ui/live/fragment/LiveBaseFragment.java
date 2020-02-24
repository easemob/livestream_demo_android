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

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.common.OnItemClickListener;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.activity.BaseLiveFragment;
import com.easemob.livedemo.ui.activity.RoomUserDetailsDialog;
import com.easemob.livedemo.ui.activity.RoomUserManagementDialog;
import com.easemob.livedemo.ui.live.LiveBaseActivity;
import com.easemob.livedemo.ui.live.adapter.MemberAvatarAdapter;
import com.easemob.livedemo.ui.widget.PeriscopeLayout;
import com.easemob.livedemo.ui.widget.RoomMessagesView;
import com.easemob.livedemo.ui.widget.ShowGiveGiftView;
import com.easemob.livedemo.ui.widget.SingleBarrageView;
import com.easemob.livedemo.utils.Utils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class LiveBaseFragment extends BaseLiveFragment implements View.OnClickListener, View.OnTouchListener {
    private static final int MAX_SIZE = 10;
    protected static final String TAG = "LiveActivity";
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
    protected EMChatRoomChangeListener chatRoomChangeListener;
    protected EMMessageListener msgListener;
    LinkedList<String> memberList = new LinkedList<>();
    protected int membersCount;
    protected Handler handler = new Handler();
    private LinearLayoutManager layoutManager;
    private MemberAvatarAdapter avatarAdapter;
    protected boolean isMessageListInited;

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

        usernameView.setText(anchorId);
        liveIdView.setText(getString(R.string.em_live_room_id, liveId));
        audienceNumView.setText(String.valueOf(liveRoom.getAudienceNum()));
        watchedCount = liveRoom.getAudienceNum();
        tvMemberNum.setText(String.valueOf(watchedCount));
    }

    @Override
    protected void initListener() {
        super.initListener();
        tvMemberNum.setOnClickListener(this);
        toolbar.setOnClickListener(this);
        liveReceiveGift.setOnClickListener(this);
        getView().setOnTouchListener(this);
        initMessageListener();
    }

    @Override
    protected void initData() {
        super.initData();

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
        RoomUserManagementDialog managementDialog = new RoomUserManagementDialog(chatroomId);
        managementDialog.show(getChildFragmentManager(), "RoomUserManagementDialog");
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
        chatRoomChangeListener = new EMChatRoomChangeListener() {

            @Override public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(chatroomId)) {
                    mContext.finish();
                }
            }

            @Override public void onMemberJoined(String roomId, String participant) {
                onRoomMemberAdded(participant);
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {
                //                showChatroomToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
                onRoomMemberExited(participant);
            }

            @Override
            public void onRemovedFromChatRoom(String roomId, String roomName, String participant) {
                if (roomId.equals(chatroomId)) {
                    String curUser = EMClient.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMClient.getInstance().chatroomManager().leaveChatRoom(roomId);
                        //postUserChangeEvent(StatisticsType.LEAVE, curUser);
                        mContext.showToast("你已被移除出此房间");
                        mContext.finish();
                    } else {
                        //                        showChatroomToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                        onRoomMemberExited(participant);
                    }
                }
            }

            @Override
            public void onMuteListAdded(String chatRoomId, List<String> mutes, long expireTime) {
                for(String name : mutes){
                    showMemberChangeEvent(name, "被禁言");
                }
            }

            @Override public void onMuteListRemoved(String chatRoomId, List<String> mutes) {
                for(String name : mutes){
                    showMemberChangeEvent(name, "被解除禁言");
                }
            }

            @Override public void onAdminAdded(String chatRoomId, String admin) {
                if(admin.equals(EMClient.getInstance().getCurrentUser())) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override public void run() {
                            userManagerView.setVisibility(View.VISIBLE);
                        }
                    });
                }
                showMemberChangeEvent(admin, "被提升为房管");
            }

            @Override public void onAdminRemoved(String chatRoomId, String admin) {
                if(admin.equals(EMClient.getInstance().getCurrentUser())) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override public void run() {
                            userManagerView.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                showMemberChangeEvent(admin, "被解除房管");
            }

            @Override
            public void onOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {

            }
        };

        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomChangeListener);
    }

    private synchronized void onRoomMemberAdded(String name) {
        watchedCount++;
        if (!memberList.contains(name)) {
            membersCount++;
            if(memberList.size() >= MAX_SIZE)
                memberList.removeLast();
            memberList.addFirst(name);
            showMemberChangeEvent(name, "来了");
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

    private void showMemberChangeEvent(String username, String event){
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        message.setTo(chatroomId);
        message.setFrom(username);
        EMTextMessageBody textMessageBody = new EMTextMessageBody(event);
        message.addBody(textMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().saveMessage(message);
        messageView.refreshSelectLast();
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
                }
            }
        });
    }

    private void initMessageListener() {
        msgListener = new EMMessageListener() {

            @Override public void onMessageReceived(List<EMMessage> messages) {

                for (EMMessage message : messages) {
                    String username = null;
                    // 群组消息
                    if (message.getChatType() == EMMessage.ChatType.GroupChat
                            || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                        username = message.getTo();
                    } else {
                        // 单聊消息
                        username = message.getFrom();
                    }
                    // 如果是当前会话的消息，刷新聊天页面
                    if (username.equals(chatroomId)) {
                        //if (message.getBooleanAttribute(DemoConstants.EXTRA_IS_BARRAGE_MSG, false)) {
                        //    barrageLayout.addBarrage(
                        //            ((EMTextMessageBody) message.getBody()).getMessage(),
                        //            message.getFrom());
                        //}
                        messageView.refreshSelectLast();
                        barrageView.refresh();
                    } else {
                        //if(message.getChatType() == EMMessage.ChatType.Chat && message.getTo().equals(EMClient.getInstance().getCurrentUser())){
                        //  runOnUiThread(new Runnable() {
                        //    @Override public void run() {
                        //      newMsgNotifyImage.setVisibility(View.VISIBLE);
                        //    }
                        //  });
                        //}
                        //// 如果消息不是和当前聊天ID的消息
                        //EaseUI.getInstance().getNotifier().onNewMsg(message);
                    }
                }
            }

            @Override public void onCmdMessageReceived(List<EMMessage> messages) {
                EMMessage message = messages.get(messages.size() - 1);
                if (DemoConstants.CMD_GIFT.equals(((EMCmdMessageBody) message.getBody()).action())) {
                    //showLeftGiftView(message.getFrom());
                } else if(DemoConstants.CMD_PRAISE.equals(((EMCmdMessageBody) message.getBody()).action())) {
                    showPraise(message.getIntAttribute(DemoConstants.EXTRA_PRAISE_COUNT, 1));
                }
            }

            @Override public void onMessageRead(List<EMMessage> messages) {

            }

            @Override public void onMessageDelivered(List<EMMessage> messages) {

            }

            @Override public void onMessageChanged(EMMessage message, Object change) {
                if (isMessageListInited) {
                    messageView.refresh();
                }
            }
        };
    }

    protected void onMessageListInit() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                messageView.init(chatroomId);
                barrageView.setData(chatroomId);
                messageView.setMessageViewListener(new RoomMessagesView.MessageViewListener() {
                    @Override public void onMessageSend(String content) {
                        EMMessage message = EMMessage.createTxtSendMessage(content, chatroomId);
                        //if (messageView.isBarrageShow) {
                        //    message.setAttribute(DemoConstants.EXTRA_IS_BARRAGE_MSG, true);
                        //    barrageLayout.addBarrage(content,
                        //            EMClient.getInstance().getCurrentUser());
                        //}
                        message.setChatType(EMMessage.ChatType.ChatRoom);
                        EMClient.getInstance().chatManager().sendMessage(message);
                        message.setMessageStatusCallback(new EMCallBack() {
                            @Override public void onSuccess() {
                                //刷新消息列表
                                messageView.refreshSelectLast();
                                barrageView.refresh();
                            }

                            @Override public void onError(int i, String s) {
                                mContext.showToast("消息发送失败！");
                            }

                            @Override public void onProgress(int i, String s) {

                            }
                        });
                    }

                    @Override public void onItemClickListener(final EMMessage message) {
                        //if(message.getFrom().equals(EMClient.getInstance().getCurrentUser())){
                        //    return;
                        //}
                        String clickUsername = message.getFrom();
                        showUserDetailsDialog(clickUsername);
                    }

                    @Override public void onHiderBottomBar() {
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
        RoomUserDetailsDialog dialog = RoomUserDetailsDialog.newInstance(username, liveRoom);
        dialog.setManageEventListener(new RoomUserDetailsDialog.RoomManageEventListener() {
            @Override public void onKickMember(String username) {
                onRoomMemberExited(username);
            }

            @Override public void onAddBlacklist(String username) {
                onRoomMemberExited(username);
            }
        });
        dialog.show(getChildFragmentManager(), "RoomUserDetailsDialog");
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
                showUserDetailsDialog(item);
            }
        });
        ThreadPoolManager.getInstance().executeTask(new ThreadPoolManager.Task<Void>() {
            @Override public Void onRequest() throws HyphenateException {
                try {
                    chatroom = EMClient.getInstance()
                            .chatroomManager()
                            .fetchChatRoomFromServer(chatroomId, true);
                    memberList.clear();
                    List<String> tempList = new ArrayList<>();
                    tempList.addAll(chatroom.getAdminList());
                    tempList.addAll(chatroom.getMemberList());
                    if (tempList.contains(chatroom.getOwner())) {
                        tempList.remove(chatroom.getOwner());
                    }
                    if(tempList.size() > MAX_SIZE) {
                        for (int i = 0; i < MAX_SIZE; i++){
                            memberList.add(i, tempList.get(i));
                        }
                    }else {
                        memberList.addAll(tempList);
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override public void onSuccess(Void aVoid) {
                int size = chatroom.getMemberCount();
                audienceNumView.setText(String.valueOf(size));
                membersCount = size;
                //观看人数不包含主播
                watchedCount = membersCount;
                tvMemberNum.setText(String.valueOf(watchedCount));
                notifyDataSetChanged();
            }

            @Override public void onError(HyphenateException exception) {

            }
        });
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
                Log.e("TAG", "x = "+x + " y = "+y);
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
}
