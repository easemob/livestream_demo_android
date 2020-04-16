package com.easemob.custommessage;

import android.text.TextUtils;

public enum EmCustomMsgType {
    /**
     * 礼物消息
     */
    CHATROOM_GIFT("chatroom_gift"),

    /**
     * 点赞
     */
    CHATROOM_PRAISE("chatroom_praise"),

    /**
     * 弹幕
     */
    CHATROOM_BARRAGE("chatroom_barrage");

    private String name;
    private EmCustomMsgType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EmCustomMsgType fromName(String name) {
        for (EmCustomMsgType type : EmCustomMsgType.values()) {
            if(TextUtils.equals(type.getName(), name)) {
                return type;
            }
        }
        return null;
    }

}
