package com.easemob.livedemo.common.utils;

import com.hyphenate.chat.EMMessage;
import com.easemob.chatroom.EaseLiveMessageHelper;
import com.easemob.chatroom.OnSendLiveMessageCallBack;


public class DemoMsgHelper {
    private static DemoMsgHelper instance;

    private DemoMsgHelper() {
    }

    private String chatroomId;

    public static DemoMsgHelper getInstance() {
        if (instance == null) {
            synchronized (DemoMsgHelper.class) {
                if (instance == null) {
                    instance = new DemoMsgHelper();
                }
            }
        }
        return instance;
    }

    /**
     * It needs to be initialized at the beginning of the live page to prevent the chatroomId from being empty or incorrect
     *
     * @param chatroomId
     */
    public void init(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getCurrentRoomId() {
        return this.chatroomId;
    }

    /**
     * Send a like message
     *
     * @param num
     * @param callBack
     */
    public void sendLikeMsg(int num, OnSendLiveMessageCallBack callBack) {
        EaseLiveMessageHelper.getInstance().sendPraiseMsg(chatroomId, num, new OnSendLiveMessageCallBack() {
            @Override
            public void onSuccess(EMMessage message) {
                DemoHelper.saveLikeInfo(message);
                if (callBack != null) {
                    callBack.onSuccess(message);
                }
            }

            @Override
            public void onError(int code, String error) {
                if (callBack != null) {
                    callBack.onError(code, error);
                }
            }
        });
    }

    /**
     * Send a barrage message
     *
     * @param content
     * @param callBack
     */
    public void sendBarrageMsg(String content, OnSendLiveMessageCallBack callBack) {
        EaseLiveMessageHelper.getInstance().sendBarrageMsg(chatroomId, content, callBack);
    }

    /**
     * Gets the id of the gift in the gift message
     *
     * @param msg
     * @return
     */
    public String getMsgGiftId(EMMessage msg) {
        return EaseLiveMessageHelper.getInstance().getMsgGiftId(msg);
    }

    /**
     * Gets the number of gifts in the gift message
     *
     * @param msg
     * @return
     */
    public int getMsgGiftNum(EMMessage msg) {
        return EaseLiveMessageHelper.getInstance().getMsgGiftNum(msg);
    }

    /**
     * Gets the number of likes in the like message
     *
     * @param msg
     * @return
     */
    public int getMsgPraiseNum(EMMessage msg) {
        return EaseLiveMessageHelper.getInstance().getMsgPraiseNum(msg);
    }

    /**
     * Gets the text in the bullet screen message
     *
     * @param msg
     * @return
     */
    public String getMsgBarrageTxt(EMMessage msg) {
        return EaseLiveMessageHelper.getInstance().getMsgBarrageTxt(msg);
    }


    /**
     * Determine if it is a gift message
     *
     * @param msg
     * @return
     */
    public boolean isGiftMsg(EMMessage msg) {
        return EaseLiveMessageHelper.getInstance().isGiftMsg(msg);
    }

    /**
     * Determine if it is a like message
     *
     * @param msg
     * @return
     */
    public boolean isPraiseMsg(EMMessage msg) {
        return EaseLiveMessageHelper.getInstance().isPraiseMsg(msg);
    }

    /**
     * Determine if it is a bullet screen message
     *
     * @param msg
     * @return
     */
    public boolean isBarrageMsg(EMMessage msg) {
        return EaseLiveMessageHelper.getInstance().isBarrageMsg(msg);
    }
}
