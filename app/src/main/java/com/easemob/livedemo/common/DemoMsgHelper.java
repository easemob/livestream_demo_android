package com.easemob.livedemo.common;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

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
     * @param callBack
     */
    public void sendTxtMsg(String content, EMCallBack callBack) {
        EMMessage message = EMMessage.createTxtSendMessage(content, chatroomId);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().sendMessage(message);
        message.setMessageStatusCallback(callBack);
    }
}
