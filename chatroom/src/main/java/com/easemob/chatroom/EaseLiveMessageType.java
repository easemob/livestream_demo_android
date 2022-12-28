package com.easemob.chatroom;

import android.text.TextUtils;

public enum EaseLiveMessageType {

    CHATROOM_GIFT("chatroom_gift"),

    CHATROOM_PRAISE("chatroom_praise"),

    CHATROOM_BARRAGE("chatroom_barrage");

    private String name;

    EaseLiveMessageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EaseLiveMessageType fromName(String name) {
        for (EaseLiveMessageType type : EaseLiveMessageType.values()) {
            if (TextUtils.equals(type.getName(), name)) {
                return type;
            }
        }
        return null;
    }

}
