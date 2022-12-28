package com.easemob.chatroom;


import com.hyphenate.chat.EMMessage;


public interface OnSendLiveMessageCallBack {
    /**
     * A successful callback
     *
     * @param message message
     */
    void onSuccess(EMMessage message);

    /**
     * @param code  error code
     * @param error error info
     */
    void onError(int code, String error);


}
