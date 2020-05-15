package com.easemob.custommessage;

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

/**
 * 自定义消息的帮助类（目前主要用于聊天室中礼物，点赞及弹幕消息）。
 * 用法如下：
 * （1）初始化 {@link #init()}，添加消息监听，根据业务需求，选择合适的地方初始化。
 * （2）设置聊天室信息 {@link #setChatRoomInfo(String)} 设置聊天室的id，用于筛选聊天室消息
 * （3）设置自定义消息监听{@link #setOnCustomMsgReceiveListener(OnCustomMsgReceiveListener)}
 *      用于接收不同的自定义消息类型（目前仅礼物，点赞及弹幕消息）。
 * （4）发送自定义消息：
 *      a、如果自定义消息类型与library相同，且所传参数相同或者相近，可以直接调用如下方法：
 *      {@link #sendGiftMsg(String, int, OnMsgCallBack)},
 *      {@link #sendPraiseMsg(int, OnMsgCallBack)},
 *      {@link #sendBarrageMsg(String, OnMsgCallBack)} 或者
 *      {@link #sendGiftMsg(Map, OnMsgCallBack)},
 *      {@link #sendPraiseMsg(Map, OnMsgCallBack)},
 *      {@link #sendBarrageMsg(Map, OnMsgCallBack)}
 *      b、如果有其他自定义消息类型，可以调用如下方法：
 *      {@link #sendCustomMsg(String, Map, OnMsgCallBack)},
 *      {@link #sendCustomMsg(String, EMMessage.ChatType, String, Map, OnMsgCallBack)}。
 * （5）自定义消息类型枚举{@link EmCustomMsgType} 定义了礼物，点赞及弹幕消息类型（以event区分）
 */
public class EmCustomMsgHelper implements EMMessageListener {
    private static EmCustomMsgHelper instance;
    private EmCustomMsgHelper(){}

    private String chatRoomId;
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

    /**
     * 根据业务要求，放在application或者其他需要初始化的地方
     */
    public void init() {
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    /**
     * 设置聊天室id
     * @param chatRoomId
     */
    public void setChatRoomInfo(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    /**
     * 设置接收消息的监听
     * @param listener
     */
    public void setOnCustomMsgReceiveListener(OnCustomMsgReceiveListener listener) {
        this.listener = listener;
    }

    /**
     * 移除监听（在页面中初始化后，记得在onDestroy()生命周期中移除）
     */
    public void removeListener() {
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            // 先判断是否自定义消息
            if(message.getType() != EMMessage.Type.CUSTOM) {
                continue;
            }
            // 再排除单聊
            if(message.getChatType() != EMMessage.ChatType.GroupChat && message.getChatType() != EMMessage.ChatType.ChatRoom) {
                continue;
            }
            String username = message.getTo();
            // 判断是否同一个聊天室或者群组
            if(!TextUtils.equals(username, chatRoomId)) {
                continue;
            }
            // 判断是否是自定消息，然后区分礼物，点赞及弹幕消息
            EMCustomMessageBody body = (EMCustomMessageBody) message.getBody();
            String event = body.event();
            // 如果event为空，则不处理
            if(TextUtils.isEmpty(event)) {
                continue;
            }
            EmCustomMsgType msgType = getCustomMsgType(event);
            if(msgType == null) {
                continue;
            }
            // 最后返回各自的消息类型
            switch (msgType) {
                case CHATROOM_GIFT:
                    if(listener != null) {
                        listener.onReceiveGiftMsg(message);
                    }
                    break;
                case CHATROOM_PRAISE:
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
        if(params.size() <= 0) {
            return;
        }
        sendCustomMsg(EmCustomMsgType.CHATROOM_GIFT.getName(), params, callBack);
    }

    /**
     * 发送点赞消息
     * @param num
     * @param callBack
     */
    public void sendPraiseMsg(int num, OnMsgCallBack callBack) {
        if(num <= 0) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(MsgConstant.CUSTOM_PRAISE_KEY_NUM, String.valueOf(num));
        sendPraiseMsg(params, callBack);
    }

    /**
     * 发送点赞消息(多参数)
     * @param params
     * @param callBack
     */
    public void sendPraiseMsg(Map<String, String> params, final OnMsgCallBack callBack) {
        if(params.size() <= 0) {
            return;
        }
        sendCustomMsg(EmCustomMsgType.CHATROOM_PRAISE.getName(), params, callBack);
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
        sendCustomMsg(EmCustomMsgType.CHATROOM_BARRAGE.getName(), params, callBack);
    }

    /**
     * 发送自定义消息
     * @param event
     * @param params
     * @param callBack
     */
    public void sendCustomMsg(String event, Map<String, String> params, final OnMsgCallBack callBack) {
        sendCustomMsg(chatRoomId, EMMessage.ChatType.ChatRoom, event, params, callBack);
    }

    /**
     * 发送自定义消息
     * @param to
     * @param chatType
     * @param event
     * @param params
     * @param callBack
     */
    public void sendCustomMsg(String to, EMMessage.ChatType chatType, String event, Map<String, String> params, final OnMsgCallBack callBack) {
        final EMMessage sendMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(event);
        body.setParams(params);
        sendMessage.addBody(body);
        sendMessage.setTo(to);
        sendMessage.setChatType(chatType);
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
                    callBack.onError(sendMessage.getMsgId(), i, s);
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
    public int getMsgPraiseNum(EMMessage msg) {
        if(!isPraiseMsg(msg)) {
            return 0;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if(params.containsKey(MsgConstant.CUSTOM_PRAISE_KEY_NUM)) {
            String num = params.get(MsgConstant.CUSTOM_PRAISE_KEY_NUM);
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
    public boolean isPraiseMsg(EMMessage msg) {
        return getCustomMsgType(getCustomEvent(msg)) == EmCustomMsgType.CHATROOM_PRAISE;
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
     * @param event
     * @return
     */
    public EmCustomMsgType getCustomMsgType(String event) {
        if(TextUtils.isEmpty(event)) {
            return null;
        }
        return EmCustomMsgType.fromName(event);
    }
}
