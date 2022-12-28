package com.easemob.chatroom.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class EaseLiveMessageStyleHelper {
    private static EaseLiveMessageStyleHelper instance;

    private float inputEditMarginBottom;
    private float inputEditMarginEnd;
    private float messageListMarginEnd;
    private Drawable messageListBackground;
    private int messageItemTxtColor;
    private int messageItemTxtSize;
    private Drawable messageItemBubblesBackground;
    private int messageNicknameColor;
    private int messageNicknameSize;
    private boolean messageShowNickname;
    private boolean messageShowAvatar;
    private int messageAvatarShapeType;

    private EaseLiveMessageStyleHelper() {
        inputEditMarginBottom = 0;
        inputEditMarginEnd = 0;
        messageListMarginEnd = 0;
        messageListBackground = null;
        messageItemTxtColor = 0;
        messageItemTxtSize = 0;
        messageItemBubblesBackground = null;
        messageNicknameColor = 0;
        messageNicknameSize = 0;
        messageShowNickname = true;
        messageShowAvatar = true;
        messageAvatarShapeType = -1;
    }

    public static EaseLiveMessageStyleHelper getInstance() {
        if (instance == null) {
            synchronized (EaseLiveMessageStyleHelper.class) {
                if (instance == null) {
                    instance = new EaseLiveMessageStyleHelper();
                }
            }
        }
        return instance;
    }

    public float getInputEditMarginBottom() {
        return inputEditMarginBottom;
    }

    public void setInputEditMarginBottom(float inputEditMarginBottom) {
        this.inputEditMarginBottom = inputEditMarginBottom;
    }

    public float getInputEditMarginEnd() {
        return inputEditMarginEnd;
    }

    public void setInputEditMarginEnd(float inputEditMarginEnd) {
        this.inputEditMarginEnd = inputEditMarginEnd;
    }

    public float getMessageListMarginEnd() {
        return messageListMarginEnd;
    }

    public void setMessageListMarginEnd(float messageListMarginEnd) {
        this.messageListMarginEnd = messageListMarginEnd;
    }

    public Drawable getMessageListBackground() {
        return messageListBackground;
    }

    public void setMessageListBackground(Drawable messageListBackground) {
        this.messageListBackground = messageListBackground;
    }

    public int getMessageItemTxtColor() {
        return messageItemTxtColor;
    }

    public void setMessageItemTxtColor(int messageItemTxtColor) {
        this.messageItemTxtColor = messageItemTxtColor;
    }

    public int getMessageItemTxtSize() {
        return messageItemTxtSize;
    }

    public void setMessageItemTxtSize(int messageItemTxtSize) {
        this.messageItemTxtSize = messageItemTxtSize;
    }

    public Drawable getMessageItemBubblesBackground() {
        return messageItemBubblesBackground;
    }

    public void setMessageItemBubblesBackground(Drawable messageItemBubblesBackground) {
        this.messageItemBubblesBackground = messageItemBubblesBackground;
    }

    public int getMessageNicknameColor() {
        return messageNicknameColor;
    }

    public void setMessageNicknameColor(int messageNicknameColor) {
        this.messageNicknameColor = messageNicknameColor;
    }

    public int getMessageNicknameSize() {
        return messageNicknameSize;
    }

    public void setMessageNicknameSize(int messageNicknameSize) {
        this.messageNicknameSize = messageNicknameSize;
    }

    public boolean isMessageShowNickname() {
        return messageShowNickname;
    }

    public void setMessageShowNickname(boolean messageShowNickname) {
        this.messageShowNickname = messageShowNickname;
    }

    public boolean isMessageShowAvatar() {
        return messageShowAvatar;
    }

    public void setMessageShowAvatar(boolean messageShowAvatar) {
        this.messageShowAvatar = messageShowAvatar;
    }

    public int getMessageAvatarShapeType() {
        return messageAvatarShapeType;
    }

    public void setMessageAvatarShapeType(int messageAvatarShapeType) {
        this.messageAvatarShapeType = messageAvatarShapeType;
    }

    @NonNull
    @Override
    public String toString() {
        return "EaseLiveMessageStyleHelper{" +
                "inputEditMarginBottom=" + inputEditMarginBottom +
                ", inputEditMarginEnd=" + inputEditMarginEnd +
                ", messageListMarginEnd=" + messageListMarginEnd +
                ", messageListBackground=" + messageListBackground +
                ", messageItemTxtColor=" + messageItemTxtColor +
                ", messageItemTxtSize=" + messageItemTxtSize +
                ", messageItemBubblesBackground=" + messageItemBubblesBackground +
                ", messageNicknameColor=" + messageNicknameColor +
                ", messageNicknameSize=" + messageNicknameSize +
                ", messageShowNickname=" + messageShowNickname +
                ", messageShowAvatar=" + messageShowAvatar +
                ", messageAvatarShapeType=" + messageAvatarShapeType +
                '}';
    }
}
