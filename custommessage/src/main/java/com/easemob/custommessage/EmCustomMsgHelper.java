package com.easemob.custommessage;

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

public class EmCustomMsgHelper implements EMMessageListener {
    private static EmCustomMsgHelper instance;
    private EmCustomMsgHelper(){}

    private Context context;
    private String chatRoomId;
    private String currentUser;
    private OnCustomMsgReceiveListener listener;

    public static EmCustomMsgHelper getInstance() {
        if(instance == null) {
            synchronized (EmCustomMsgHelper.class) {
                if(instance == null) {
                    instance = new EmCustomMsgHelper();
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
            if (username.equals(chatRoomId)) {
                //判断是否是自定消息，然后区分礼物，点赞及弹幕消息
                if(message.getType() == EMMessage.Type.CUSTOM) {
                    EMCustomMessageBody body = (EMCustomMessageBody) message.getBody();
                    String event = body.event();
                    if(TextUtils.isEmpty(event)) {
                        return;
                    }
                    EmCustomMsgType msgType = getCustomMsgType(event);
                    if(msgType != null) {
                        switch (msgType) {
                            case CHATROOM_GIFT:
                                if(listener != null) {
                                    listener.onReceiveGiftMsg(message);
                                }
                                break;
                            case CHATROOM_LIKE:
                                if(listener != null) {
                                    listener.onReceivePraiseMsg(message);
                                }
                                break;
                            case CHATROOM_BARRAGE:
                                if(listener != null) {
                                    listener.onReceiveBarrageMsg(message);
                                }
                                break;
                        }
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
        EMCustomMessageBody body = new EMCustomMessageBody(EmCustomMsgType.CHATROOM_GIFT.getName());
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
        EMCustomMessageBody body = new EMCustomMessageBody(EmCustomMsgType.CHATROOM_LIKE.getName());
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
        EMCustomMessageBody body = new EMCustomMessageBody(EmCustomMsgType.CHATROOM_BARRAGE.getName());
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
     * 获取礼物消息中礼物的id
     * @param msg
     * @return
     */
    public String getMsgGiftId(EMMessage msg) {
        if(!isGiftMsg(msg)) {
            return null;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if(params.containsKey(MsgConstant.CUSTOM_GIFT_KEY_ID)) {
            return params.get(MsgConstant.CUSTOM_GIFT_KEY_ID);
        }
        return null;
    }

    /**
     * 获取礼物消息中礼物的数量
     * @param msg
     * @return
     */
    public int getMsgGiftNum(EMMessage msg) {
        if(!isGiftMsg(msg)) {
            return 0;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if(params.containsKey(MsgConstant.CUSTOM_GIFT_KEY_NUM)) {
            String num = params.get(MsgConstant.CUSTOM_GIFT_KEY_NUM);
            if(TextUtils.isEmpty(num)) {
                return 0;
            }
            try {
                return Integer.valueOf(num);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 获取点赞消息中点赞的数目
     * @param msg
     * @return
     */
    public int getMsgLikeNum(EMMessage msg) {
        if(!isLikeMsg(msg)) {
            return 0;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if(params.containsKey(MsgConstant.CUSTOM_LIKE_KEY_NUM)) {
            String num = params.get(MsgConstant.CUSTOM_LIKE_KEY_NUM);
            if(TextUtils.isEmpty(num)) {
                return 0;
            }
            try {
                return Integer.valueOf(num);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 获取弹幕消息中的文本
     * @param msg
     * @return
     */
    public String getMsgBarrageTxt(EMMessage msg) {
        if(!isBarrageMsg(msg)) {
            return null;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if(params.containsKey(MsgConstant.CUSTOM_BARRAGE_KEY_TXT)) {
            return params.get(MsgConstant.CUSTOM_BARRAGE_KEY_TXT);
        }
        return null;
    }

    /**
     * 判断是否是礼物消息
     * @param msg
     * @return
     */
    public boolean isGiftMsg(EMMessage msg) {
        return getCustomMsgType(getCustomEvent(msg)) == EmCustomMsgType.CHATROOM_GIFT;
    }

    /**
     * 判断是否是点赞消息
     * @param msg
     * @return
     */
    public boolean isLikeMsg(EMMessage msg) {
        return getCustomMsgType(getCustomEvent(msg)) == EmCustomMsgType.CHATROOM_LIKE;
    }

    /**
     * 判断是否是弹幕消息
     * @param msg
     * @return
     */
    public boolean isBarrageMsg(EMMessage msg) {
        return getCustomMsgType(getCustomEvent(msg)) == EmCustomMsgType.CHATROOM_BARRAGE;
    }

    /**
     * 获取自定义消息中的event字段
     * @param message
     * @return
     */
    public String getCustomEvent(EMMessage message) {
        if(message == null) {
            return null;
        }
        if(!(message.getBody() instanceof  EMCustomMessageBody)) {
            return null;
        }
        return ((EMCustomMessageBody) message.getBody()).event();
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

    /**
     * 获取自定义消息类型
     * 注意：需要event转为大写，才能获取到正确的type值
     * @param event
     * @return
     */
    public EmCustomMsgType getCustomMsgType(String event) {
        if(TextUtils.isEmpty(event)) {
            return null;
        }
        try {
            return EmCustomMsgType.valueOf(event.toUpperCase());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }
}
