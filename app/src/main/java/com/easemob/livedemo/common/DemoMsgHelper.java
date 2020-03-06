package com.easemob.livedemo.common;

import android.icu.util.Measure;
import android.text.TextUtils;
import android.widget.TextView;

import com.easemob.livedemo.DemoConstants;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.adapter.message.EMACustomMessageBody;

import java.util.HashMap;
import java.util.Map;

public class DemoMsgHelper {
    private static DemoMsgHelper instance;
    private DemoMsgHelper(){}
    private String chatroomId;

    public static DemoMsgHelper getInstance() {
        if(instance == null) {
            synchronized (DemoMsgHelper.class) {
                if(instance == null) {
                    instance = new DemoMsgHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 需要在直播页面开始的时候初始化，防止chatroomId为空或不正确
     * @param chatroomId
     */
    public void init(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    /**
     * 发送文本消息
     * @param content
     * @param isBarrageMsg
     * @param callBack
     */
    public void sendMsg(String content, boolean isBarrageMsg, OnMsgCallBack callBack) {
        if(isBarrageMsg) {
            sendBarrageMsg(content, callBack);
        }else {
            sendTxtMsg(content, callBack);
        }
    }

    /**
     * 发送文本消息
     * @param content
     * @param callBack
     */
    public void sendTxtMsg(String content, OnMsgCallBack callBack) {
        EMMessage message = EMMessage.createTxtSendMessage(content, chatroomId);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().sendMessage(message);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                callBack.onSuccess(message);
            }

            @Override
            public void onError(int i, String s) {
                callBack.onError(i, s);
            }

            @Override
            public void onProgress(int i, String s) {
                callBack.onProgress(i, s);
            }
        });
    }

    /**
     * 发送礼物消息
     * @param giftId
     * @param num
     * @param callBack
     */
    public void sendGiftMsg(String giftId, int num, EMCallBack callBack) {
        EMMessage sendMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(DemoConstants.CUSTOM_GIFT);
        Map<String, String> params = new HashMap<>();
        params.put(DemoConstants.CUSTOM_GIFT_KEY_ID, giftId);
        params.put(DemoConstants.CUSTOM_GIFT_KEY_NUM, String.valueOf(num));
        body.setParams(params);
        sendMessage.addBody(body);
        sendMessage.setTo(chatroomId);
        sendMessage.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().sendMessage(sendMessage);
        sendMessage.setMessageStatusCallback(callBack);
    }

    /**
     * 发送点赞消息
     * @param num
     * @param callBack
     */
    public void sendLikeMsg(int num, EMCallBack callBack) {
        if(num <= 0) {
            return;
        }
        EMMessage sendMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(DemoConstants.CUSTOM_LIKE);
        Map<String, String> params = new HashMap<>();
        params.put(DemoConstants.CUSTOM_LIKE_KEY_NUM, String.valueOf(num));
        body.setParams(params);
        sendMessage.addBody(body);
        sendMessage.setTo(chatroomId);
        sendMessage.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().sendMessage(sendMessage);
        sendMessage.setMessageStatusCallback(callBack);
    }

    /**
     * 发送弹幕消息
     * @param content
     * @param callBack
     */
    public void sendBarrageMsg(String content, OnMsgCallBack callBack) {
        if(TextUtils.isEmpty(content)) {
            return;
        }
        EMMessage sendMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(DemoConstants.CUSTOM_BARRAGE);
        Map<String, String> params = new HashMap<>();
        params.put(DemoConstants.CUSTOM_BARRAGE_KEY_TXT, content);
        body.setParams(params);
        sendMessage.addBody(body);
        sendMessage.setTo(chatroomId);
        sendMessage.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().sendMessage(sendMessage);
        sendMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                if(callBack != null) {
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
}
