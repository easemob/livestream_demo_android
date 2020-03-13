package com.easemob.custommessage;

public enum EmCustomMsgType {
    /**
     * 礼物消息
     */
    CHATROOM_GIFT("chatroom_gift"),

    /**
     * 点赞
     */
    CHATROOM_LIKE("chatroom_like"),

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

}
