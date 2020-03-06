package com.easemob.chatroommessage;

import android.content.Context;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomMsgHelper implements EMMessageListener {
    private static ChatRoomMsgHelper instance;
    private ChatRoomMsgHelper(){}

    private Context context;
    private String chatRoomId;
    private String currentUser;
    private OnCustomMsgReceiveListener listener;

    public static ChatRoomMsgHelper getInstance() {
        if(instance == null) {
            synchronized (ChatRoomMsgHelper.class) {
                if(instance == null) {
                    instance = new ChatRoomMsgHelper();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    public void setChatRoomInfo(String chatRoomId, String currentUser) {
        this.chatRoomId = chatRoomId;
        this.currentUser = currentUser;
    }

    /**
     * 设置接收消息的监听
     * @param listener
     */
    public void setOnCustomMsgReceiveListener(OnCustomMsgReceiveListener listener) {
        this.listener = listener;
    }

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
            if (username.equals(chatRoomId)) {
                //判断是否是自定消息，然后区分礼物，点赞及弹幕消息
                if(message.getType() == EMMessage.Type.CUSTOM) {
                    EMCustomMessageBody body = (EMCustomMessageBody) message.getBody();
                    String event = body.event();
                    if(TextUtils.isEmpty(event)) {
                        return;
                    }
                    switch (event) {
                        case MsgConstant.CUSTOM_GIFT :
                            if(listener != null) {
                                listener.onReceiveGiftMsg(message);
                            }
                            break;
                        case MsgConstant.CUSTOM_LIKE :
                            if(listener != null) {
                                listener.onReceivePraiseMsg(message);
                            }
                            break;
                        case MsgConstant.CUSTOM_BARRAGE :
                            if(listener != null) {
                                listener.onReceiveBarrageMsg(message);
                            }
                            break;
                    }

                }
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }

    /**
     * 发送礼物消息
     * @param giftId
     * @param num
     * @param callBack
     */
    public void sendGiftMsg(String giftId, int num, OnMsgCallBack callBack) {
        Map<String, String> params = new HashMap<>();
        params.put(MsgConstant.CUSTOM_GIFT_KEY_ID, giftId);
        params.put(MsgConstant.CUSTOM_GIFT_KEY_NUM, String.valueOf(num));
        sendGiftMsg(params, callBack);
    }

    /**
     * 发送礼物消息(多参数)
     * @param params
     * @param callBack
     */
    public void sendGiftMsg(Map<String, String> params, final OnMsgCallBack callBack) {
        final EMMessage sendMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(MsgConstant.CUSTOM_GIFT);
        body.setParams(params);
        sendMessage.addBody(body);
        sendMessage.setTo(chatRoomId);
        sendMessage.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().sendMessage(sendMessage);
        sendMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                if(callBack != null) {
                    callBack.onSuccess();
                    callBack.onSuccess(sendMessage);
                }
            }

            @Override
            public void onError(int i, String s) {
                if(callBack != null) {
                    callBack.onError(i, s);
                }
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
     * 发送点赞消息
     * @param num
     * @param callBack
     */
    public void sendLikeMsg(int num, OnMsgCallBack callBack) {
        if(num <= 0) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(MsgConstant.CUSTOM_LIKE_KEY_NUM, String.valueOf(num));
        sendLikeMsg(params, callBack);
    }

    /**
     * 发送点赞消息(多参数)
     * @param params
     * @param callBack
     */
    public void sendLikeMsg(Map<String, String> params, final OnMsgCallBack callBack) {
        if(params.size() <= 0) {
            return;
        }
        final EMMessage sendMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(MsgConstant.CUSTOM_LIKE);
        body.setParams(params);
        sendMessage.addBody(body);
        sendMessage.setTo(chatRoomId);
        sendMessage.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().sendMessage(sendMessage);
        sendMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                if(callBack != null) {
                    callBack.onSuccess();
                    callBack.onSuccess(sendMessage);
                }
            }

            @Override
            public void onError(int i, String s) {
                if(callBack != null) {
                    callBack.onError(i, s);
                }
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
     * 发送弹幕消息
     * @param content
     * @param callBack
     */
    public void sendBarrageMsg(String content, final OnMsgCallBack callBack) {
        if(TextUtils.isEmpty(content)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(MsgConstant.CUSTOM_BARRAGE_KEY_TXT, content);
        sendBarrageMsg(params, callBack);
    }

    /**
     * 发送弹幕消息(多参数)
     * @param params
     * @param callBack
     */
    public void sendBarrageMsg(Map<String, String> params, final OnMsgCallBack callBack) {
        if(params.size() <= 0) {
            return;
        }
        final EMMessage sendMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(MsgConstant.CUSTOM_BARRAGE);
        body.setParams(params);
        sendMessage.addBody(body);
        sendMessage.setTo(chatRoomId);
        sendMessage.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().sendMessage(sendMessage);
        sendMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                if(callBack != null) {
                    callBack.onSuccess();
                    callBack.onSuccess(sendMessage);
                }
            }

            @Override
            public void onError(int i, String s) {
                if(callBack != null) {
                    callBack.onError(i, s);
                }
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
     * 获取自定义消息中的参数
     * @param message
     * @return
     */
    public Map<String, String> getCustomMsgParams(EMMessage message) {
        if(message == null) {
            return null;
        }
        EMMessageBody body = message.getBody();
        if(!(body instanceof EMCustomMessageBody)) {
            return null;
        }
        return ((EMCustomMessageBody) body).getParams();
    }
}
