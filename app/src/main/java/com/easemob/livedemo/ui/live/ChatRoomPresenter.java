package com.easemob.livedemo.ui.live;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.ui.activity.BaseActivity;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

public class ChatRoomPresenter implements EMChatRoomChangeListener, EMMessageListener {
    private BaseActivity mContext;
    private String chatroomId;
    private String currentUser;
    private OnChatRoomListener onChatRoomListener;

    public ChatRoomPresenter(BaseActivity context, String chatroomId) {
        this.mContext = context;
        this.chatroomId = chatroomId;
        currentUser = EMClient.getInstance().getCurrentUser();
    }

//===========================================  EMChatRoomChangeListener start =================================
    @Override
    public void onChatRoomDestroyed(String roomId, String roomName) {
        if (roomId.equals(chatroomId)) {
            mContext.finish();
        }
    }

    @Override
    public void onMemberJoined(String roomId, String participant) {
        if(onChatRoomListener != null) {
            onChatRoomListener.onChatRoomMemberAdded(participant);
        }
    }

    @Override
    public void onMemberExited(String roomId, String roomName, String participant) {
        if(onChatRoomListener != null) {
            onChatRoomListener.onChatRoomMemberExited(participant);
        }
    }

    @Override
    public void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {
        if (roomId.equals(chatroomId)) {
            if (currentUser.equals(participant)) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(roomId);
                mContext.showToast("你已被移除出此房间");
                mContext.finish();
            } else {
                if(onChatRoomListener != null) {
                    onChatRoomListener.onChatRoomMemberExited(participant);
                }
            }
        }
    }

    @Override
    public void onMuteListAdded(String chatRoomId, List<String> mutes, long expireTime) {
        for(String name : mutes){
            showMemberChangeEvent(name, "被禁言");
        }
    }

    @Override
    public void onMuteListRemoved(String chatRoomId, List<String> mutes) {
        for(String name : mutes){
            showMemberChangeEvent(name, "被解除禁言");
        }
    }

    @Override
    public void onAdminAdded(String chatRoomId, String admin) {
        showMemberChangeEvent(admin, "被提升为房管");
    }

    @Override
    public void onAdminRemoved(String chatRoomId, String admin) {
        showMemberChangeEvent(admin, "被解除房管");
    }

    @Override
    public void onOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {

    }

    @Override
    public void onAnnouncementChanged(String chatRoomId, String announcement) {

    }

//===========================================  EMChatRoomChangeListener end =================================

//===========================================  EMMessageListener start =================================

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
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
                if(onChatRoomListener != null) {
                    onChatRoomListener.onMessageReceived();
                }
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        EMMessage message = messages.get(messages.size() - 1);
        if (DemoConstants.CMD_GIFT.equals(((EMCmdMessageBody) message.getBody()).action())) {
            //showLeftGiftView(message.getFrom());
        } else if(DemoConstants.CMD_PRAISE.equals(((EMCmdMessageBody) message.getBody()).action())) {
            if(onChatRoomListener != null) {
                onChatRoomListener.onChatRoomShowPraise(message.getIntAttribute(DemoConstants.EXTRA_PRAISE_COUNT, 1));
            }
        }
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {

    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        if(onChatRoomListener != null) {
            onChatRoomListener.onMessageChanged();
        }
    }

//===========================================  EMMessageListener end =================================

    /**
     * 成员变化
     * @param username
     * @param event
     */
    public void showMemberChangeEvent(String username, String event){
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        message.setTo(chatroomId);
        message.setFrom(username);
        EMTextMessageBody textMessageBody = new EMTextMessageBody(event);
        message.addBody(textMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().saveMessage(message);
        if(onChatRoomListener != null) {
            onChatRoomListener.onMessageSelectLast();
        }
    }

    /**
     * 发送点赞消息
     * @param praiseCount
     */
    public void sendPraiseMessage(int praiseCount) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setTo(chatroomId);
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(DemoConstants.CMD_PRAISE);
        message.addBody(cmdMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(DemoConstants.EXTRA_PRAISE_COUNT, praiseCount);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    public void setOnChatRoomListener(OnChatRoomListener listener) {
        this.onChatRoomListener = listener;
    }

    public interface OnChatRoomListener {
        /**
         * 观众进入房间
         * @param participant
         */
        void onChatRoomMemberAdded(String participant);

        /**
         * 观众退出房间
         * @param participant
         */
        void onChatRoomMemberExited(String participant);

        /**
         * 收到新消息
         */
        void onMessageReceived();

        /**
         * 需要消息列表滑动到最后
         */
        void onMessageSelectLast();

        /**
         * 消息发生改变
         */
        void onMessageChanged();

        /**
         * 显示点赞
         * @param count
         */
        void onChatRoomShowPraise(int count);
    }
}
