package com.easemob.livedemo.ui.live;

import android.text.TextUtils;
import android.util.Log;

import com.easemob.custommessage.OnMsgCallBack;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoMsgHelper;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.ui.base.BaseActivity;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

public class ChatRoomPresenter implements EMChatRoomChangeListener, EMMessageListener {
    private BaseActivity mContext;
    private String chatroomId;
    private String currentUser;
    private OnChatRoomListener onChatRoomListener;
    private EMConversation conversation;

    public ChatRoomPresenter(BaseActivity context, String chatroomId) {
        this.mContext = context;
        this.chatroomId = chatroomId;
        currentUser = EMClient.getInstance().getCurrentUser();
    }

//===========================================  EMChatRoomChangeListener start =================================
    @Override
    public void onChatRoomDestroyed(String roomId, String roomName) {
        if (roomId.equals(chatroomId)) {
            mContext.showToast("房间已销毁！");
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
        if(mutes.contains(EMClient.getInstance().getCurrentUser())) {
            mContext.showToast(mContext.getString(R.string.em_live_in_mute_list));
        }
    }

    @Override
    public void onMuteListRemoved(String chatRoomId, List<String> mutes) {
        if(mutes.contains(EMClient.getInstance().getCurrentUser())) {
            mContext.showToast(mContext.getString(R.string.em_live_out_mute_list));
        }
    }

    @Override
    public void onWhiteListAdded(String chatRoomId, List<String> whitelist) {
        if(whitelist.contains(EMClient.getInstance().getCurrentUser())) {
            mContext.showToast(mContext.getString(R.string.em_live_anchor_add_white));
        }
    }

    @Override
    public void onWhiteListRemoved(String chatRoomId, List<String> whitelist) {
        if(whitelist.contains(EMClient.getInstance().getCurrentUser())) {
            mContext.showToast(mContext.getString(R.string.em_live_anchor_remove_from_white));
        }
    }

    @Override
    public void onAllMemberMuteStateChanged(String chatRoomId, boolean isMuted) {
        mContext.showToast(isMuted ? mContext.getString(R.string.em_live_mute_all) : mContext.getString(R.string.em_live_out_mute_all));
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
        if(TextUtils.equals(chatroomId, chatRoomId)) {
            if(onChatRoomListener != null) {
                onChatRoomListener.onChatRoomOwnerChanged(chatRoomId, newOwner, oldOwner);
            }
        }
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
                //判断是否是自定消息，然后区分礼物，点赞及弹幕消息
                if(onChatRoomListener != null) {
                    onChatRoomListener.onMessageReceived();
                }
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
//        EMMessage message = messages.get(messages.size() - 1);
//        if (DemoConstants.CMD_GIFT.equals(((EMCmdMessageBody) message.getBody()).action())) {
//            //showLeftGiftView(message.getFrom());
//        } else if(DemoConstants.CMD_PRAISE.equals(((EMCmdMessageBody) message.getBody()).action())) {
//            if(onChatRoomListener != null) {
//                onChatRoomListener.onReceivePraiseMsg(message.getIntAttribute(DemoConstants.EXTRA_PRAISE_COUNT, 1));
//            }
//        }
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
        message.setAttribute("member_add", true);
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
        DemoMsgHelper.getInstance().sendLikeMsg(praiseCount, new OnMsgCallBack() {
            @Override
            public void onSuccess(EMMessage message) {
                Log.e("TAG", "send praise message success");
                ThreadManager.getInstance().runOnMainThread(()-> {
                    if(onChatRoomListener != null) {
                        onChatRoomListener.onMessageSelectLast();
                    }
                });
            }

            @Override
            public void onError(String messageId, int code, String error) {
                deleteMuteMsg(messageId, code);
                mContext.showToast("errorCode = " + code + "; errorMsg = "+error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    public void setOnChatRoomListener(OnChatRoomListener listener) {
        this.onChatRoomListener = listener;
    }

    /**
     * 发送礼物消息
     * @param bean
     * @param callBack
     */
    public void sendGiftMsg(GiftBean bean, OnMsgCallBack callBack) {
        DemoMsgHelper.getInstance().sendGiftMsg(bean.getId(), bean.getNum(), new OnMsgCallBack() {
            @Override
            public void onSuccess(EMMessage message) {
                if(callBack != null) {
                    callBack.onSuccess();
                    callBack.onSuccess(message);
                }
                ThreadManager.getInstance().runOnMainThread(()-> {
                    if(onChatRoomListener != null) {
                        onChatRoomListener.onMessageSelectLast();
                    }
                });
            }

            @Override
            public void onError(String messageId, int code, String error) {
                if(callBack != null) {
                    callBack.onError(code, error);
                    callBack.onError(messageId, code, error);
                }
                deleteMuteMsg(messageId, code);
                mContext.showToast("errorCode = " + code + "; errorMsg = "+error);
            }

            @Override
            public void onProgress(int progress, String status) {
                if(callBack != null) {
                    callBack.onProgress(progress, status);
                }
            }
        });
    }

    /**
     * 发送文本或者弹幕消息
     * @param content
     * @param isBarrageMsg
     * @param callBack
     */
    public void sendTxtMsg(String content, boolean isBarrageMsg, OnMsgCallBack callBack) {
        DemoMsgHelper.getInstance().sendMsg(content, isBarrageMsg, new OnMsgCallBack() {
            @Override
            public void onSuccess(EMMessage message) {
                if(callBack != null) {
                    callBack.onSuccess(message);
                }
            }

            @Override
            public void onError(String messageId, int code, String error) {
                if(callBack != null) {
                    callBack.onError(messageId, code, error);
                }
                deleteMuteMsg(messageId, code);
            }

            @Override
            public void onProgress(int i, String s) {
                if(callBack != null) {
                    callBack.onProgress(i, s);
                }
            }
        });
    }

    /**
     * 删除被禁言期间的消息
     * @param messageId
     * @param code
     */
    private void deleteMuteMsg(String messageId, int code) {
        if(code == EMError.USER_MUTED || code == EMError.MESSAGE_ILLEGAL_WHITELIST) {
            if(conversation == null) {
                conversation = EMClient.getInstance().chatManager().getConversation(chatroomId, EMConversation.EMConversationType.ChatRoom, true);
            }
            conversation.removeMessage(messageId);
            mContext.showToast("您已被禁言");
        }
    }

    public interface OnChatRoomListener {
        void onChatRoomOwnerChanged(String chatRoomId, String newOwner, String oldOwner);
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

    }
}
