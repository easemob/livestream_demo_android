package com.easemob.custommessage;

import com.hyphenate.chat.EMMessage;

/**
 * 定义接收到的消息类型
 */
public interface OnCustomMsgReceiveListener {
    /**
     * 接收到礼物消息
     * @param message
     */
    void onReceiveGiftMsg(EMMessage message);

    /**
     * 接收到点赞消息
     * @param message
     */
    void onReceivePraiseMsg(EMMessage message);

    /**
     * 接收到弹幕消息
     * @param message
     */
    void onReceiveBarrageMsg(EMMessage message);
}
