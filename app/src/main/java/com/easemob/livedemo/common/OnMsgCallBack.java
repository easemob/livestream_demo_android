package com.easemob.livedemo.common;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMMessage;

public abstract class OnMsgCallBack implements EMCallBack {

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
