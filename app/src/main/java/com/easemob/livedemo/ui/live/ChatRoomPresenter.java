package com.easemob.livedemo.ui.live;

import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.easemob.chatroom.EaseLiveMessageConstant;
import com.easemob.chatroom.EaseLiveMessageHelper;
import com.easemob.chatroom.OnSendLiveMessageCallBack;

import java.util.List;

import com.easemob.livedemo.common.utils.DemoMsgHelper;
import com.easemob.livedemo.common.utils.ThreadManager;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.ui.base.BaseActivity;

public class ChatRoomPresenter implements EMChatRoomChangeListener {
    private final BaseActivity mContext;
    private final String chatroomId;
    private final String currentUser;
    private OnChatRoomListener onChatRoomListener;
    private EMConversation conversation;
    private String ownerNickname;


    public ChatRoomPresenter(BaseActivity context, String chatroomId) {
        this.mContext = context;
        this.chatroomId = chatroomId;
        currentUser = EMClient.getInstance().getCurrentUser();


    }

    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
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
        if (onChatRoomListener != null) {
            onChatRoomListener.onChatRoomMemberAdded(participant);
        }
    }

    @Override
    public void onMemberExited(String roomId, String roomName, String participant) {
        if (onChatRoomListener != null) {
            onChatRoomListener.onChatRoomMemberExited(participant);
        }
    }

    @Override
    public void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {
        if (roomId.equals(chatroomId)) {
            if (currentUser.equals(participant)) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(roomId);
                mContext.finish();
            } else {
                if (onChatRoomListener != null) {
                    onChatRoomListener.onChatRoomMemberExited(participant);
                }
            }
        }
    }

    @Override
    public void onMuteListAdded(String chatRoomId, List<String> mutes, long expireTime) {
        if (onChatRoomListener != null) {
            onChatRoomListener.onMuteListAdded(chatRoomId, mutes, expireTime);
        }
    }

    @Override
    public void onMuteListRemoved(String chatRoomId, List<String> mutes) {
        if (onChatRoomListener != null) {
            onChatRoomListener.onMuteListRemoved(chatRoomId, mutes);
        }
    }

    @Override
    public void onWhiteListAdded(String chatRoomId, List<String> whitelist) {
        if (onChatRoomListener != null) {
            onChatRoomListener.onWhiteListAdded(chatRoomId, whitelist);
        }
    }

    @Override
    public void onWhiteListRemoved(String chatRoomId, List<String> whitelist) {
        if (onChatRoomListener != null) {
            onChatRoomListener.onWhiteListRemoved(chatRoomId, whitelist);
        }
    }

    @Override
    public void onAllMemberMuteStateChanged(String chatRoomId, boolean isMuted) {
        if (onChatRoomListener != null) {
            onChatRoomListener.onAllMemberMuteStateChanged(chatRoomId, isMuted);
        }
    }

    @Override
    public void onAdminAdded(String chatRoomId, String admin) {
        if (onChatRoomListener != null) {
            onChatRoomListener.onAdminAdded(chatRoomId, admin);
        }
    }

    @Override
    public void onAdminRemoved(String chatRoomId, String admin) {
        if (onChatRoomListener != null) {
            onChatRoomListener.onAdminRemoved(chatRoomId, admin);
        }
    }

    @Override
    public void onOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {
        if (TextUtils.equals(chatroomId, chatRoomId)) {
            if (onChatRoomListener != null) {
                onChatRoomListener.onChatRoomOwnerChanged(chatRoomId, newOwner, oldOwner);
            }
        }
    }

    @Override
    public void onAnnouncementChanged(String chatRoomId, String announcement) {

    }

//===========================================  EMChatRoomChangeListener end =================================


    public void showMemberChangeEvent(String username, String event) {
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        message.setTo(chatroomId);
        message.setFrom(username);
        EMTextMessageBody textMessageBody = new EMTextMessageBody(event);
        message.addBody(textMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(EaseLiveMessageConstant.LIVE_MESSAGE_KEY_MEMBER_JOIN, true);
        EMClient.getInstance().chatManager().saveMessage(message);
        if (onChatRoomListener != null) {
            onChatRoomListener.onMessageRefresh();
        }
    }

    public void sendPraiseMessage(int praiseCount) {
        DemoMsgHelper.getInstance().sendLikeMsg(praiseCount, new OnSendLiveMessageCallBack() {
            @Override
            public void onSuccess(EMMessage message) {
                Log.e("TAG", "send praise message success");
                ThreadManager.getInstance().runOnMainThread(() -> {
                    if (onChatRoomListener != null) {
                        onChatRoomListener.onMessageRefresh();
                    }
                });
            }

            @Override
            public void onError(int code, String error) {

            }
        });
    }

    public void setOnChatRoomListener(OnChatRoomListener listener) {
        this.onChatRoomListener = listener;
    }

    public void sendGiftMsg(GiftBean bean, OnSendLiveMessageCallBack callBack) {
        EaseLiveMessageHelper.getInstance().sendGiftMsg(chatroomId, bean.getId(), bean.getNum(), new OnSendLiveMessageCallBack() {
            @Override
            public void onSuccess(EMMessage message) {
                if (callBack != null) {
                    callBack.onSuccess(message);
                }
                ThreadManager.getInstance().runOnMainThread(() -> {
                    if (onChatRoomListener != null) {
                        onChatRoomListener.onMessageRefresh();
                    }
                });
            }

            @Override
            public void onError(int code, String error) {
                if (callBack != null) {
                    callBack.onError(code, error);
                }
            }
        });
    }

    public void sendBarrageMsg(String content, OnSendLiveMessageCallBack callBack) {
        DemoMsgHelper.getInstance().sendBarrageMsg(content, new OnSendLiveMessageCallBack() {
            @Override
            public void onSuccess(EMMessage message) {
                if (callBack != null) {
                    callBack.onSuccess(message);
                }
            }

            @Override
            public void onError(int code, String error) {
                if (callBack != null) {
                    callBack.onError(code, error);
                }
            }
        });
    }

    public interface OnChatRoomListener {
        void onChatRoomOwnerChanged(String chatRoomId, String newOwner, String oldOwner);

        void onChatRoomMemberAdded(String participant);

        void onChatRoomMemberExited(String participant);

        void onMessageRefresh();

        void onAdminAdded(String chatRoomId, String admin);

        void onAdminRemoved(String chatRoomId, String admin);

        void onMuteListAdded(String chatRoomId, List<String> mutes, long expireTime);

        void onMuteListRemoved(String chatRoomId, List<String> mutes);

        void onWhiteListAdded(String chatRoomId, List<String> whitelist);

        void onWhiteListRemoved(String chatRoomId, List<String> whitelist);

        default void onAllMemberMuteStateChanged(String chatRoomId, boolean isMuted) {
        }

    }
}
