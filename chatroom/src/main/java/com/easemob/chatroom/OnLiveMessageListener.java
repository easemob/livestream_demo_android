package com.easemob.chatroom;


import com.hyphenate.chat.EMMessage;

import java.util.List;


public interface OnLiveMessageListener {
    default void onMessageReceived(List<EMMessage> messages) {
    }

    void onGiftMessageReceived(EMMessage message);

    default void onPraiseMessageReceived(EMMessage message) {
    }

    default void onBarrageMessageReceived(EMMessage message) {
    }
}
