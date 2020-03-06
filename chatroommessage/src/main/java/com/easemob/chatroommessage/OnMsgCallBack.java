package com.easemob.chatroommessage;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMMessage;

public abstract class OnMsgCallBack implements EMCallBack {

    /**
     * 为了回调发送的message，不建议使用此回调
     */
    @Deprecated
    @Override
    public void onSuccess() {

    }

    /**
     * 用于发送弹幕消息的成功回调
     * @param message
     */
    public abstract void onSuccess(EMMessage message);

    @Override
    public void onProgress(int i, String s) {

    }
}
