package com.easemob.livedemo.common;

import com.easemob.custommessage.EmCustomMsgHelper;
import com.easemob.custommessage.EmCustomMsgType;
import com.easemob.custommessage.OnCustomMsgReceiveListener;
import com.easemob.custommessage.OnMsgCallBack;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

public class DemoMsgHelper {
    private static DemoMsgHelper instance;
    private DemoMsgHelper(){}
    private String chatroomId;

    public static DemoMsgHelper getInstance() {
        if(instance == null) {
            synchronized (DemoMsgHelper.class) {
                if(instance == null) {
                    instance = new DemoMsgHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 需要在直播页面开始的时候初始化，防止chatroomId为空或不正确
     * @param chatroomId
     */
    public void init(String chatroomId) {
        this.chatroomId = chatroomId;
        setCustomMsgListener();
        //设置相关的直播间信息
        EmCustomMsgHelper.getInstance().setChatRoomInfo(chatroomId);
    }

    public String getCurrentRoomId() {
        return this.chatroomId;
    }

    /**
     * 设置自定义消息监听
     */
    public void setCustomMsgListener() {
        EmCustomMsgHelper.getInstance().init();
    }

    /**
     * 移除自定义消息监听
     */
    public void removeCustomMsgLisenter() {
        EmCustomMsgHelper.getInstance().removeListener();
    }

    /**
     * 发送文本消息
     * @param content
     * @param isBarrageMsg
     * @param callBack
     */
    public void sendMsg(String content, boolean isBarrageMsg, OnMsgCallBack callBack) {
        if(isBarrageMsg) {
            sendBarrageMsg(content, callBack);
        }else {
            sendTxtMsg(content, callBack);
        }
    }

    /**
     * 发送文本消息
     * @param content
     * @param callBack
     */
    public void sendTxtMsg(String content, OnMsgCallBack callBack) {
        EMMessage message = EMMessage.createTxtSendMessage(content, chatroomId);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                callBack.onSuccess();
                callBack.onSuccess(message);
            }

            @Override
            public void onError(int i, String s) {
                callBack.onError(i, s);
                callBack.onError(message.getMsgId(), i, s);
            }

            @Override
            public void onProgress(int i, String s) {
                callBack.onProgress(i, s);
            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * 发送礼物消息
     * @param giftId
     * @param num
     * @param callBack
     */
    public void sendGiftMsg(String giftId, int num, OnMsgCallBack callBack) {
        EmCustomMsgHelper.getInstance().sendGiftMsg(giftId, num, new OnMsgCallBack() {
            @Override
            public void onSuccess(EMMessage message) {
                DemoHelper.saveGiftInfo(message);
                if(callBack != null) {
                    callBack.onSuccess();
                    callBack.onSuccess(message);
                }
            }

            @Override
            public void onProgress(int i, String s) {
                super.onProgress(i, s);
                if(callBack != null) {
                    callBack.onProgress(i, s);
                }
            }

            @Override
            public void onError(String messageId, int code, String error) {
                if(callBack != null) {
                    callBack.onError(code, error);
                    callBack.onError(messageId, code, error);
                }
            }
        });
    }

    /**
     * 发送点赞消息
     * @param num
     * @param callBack
     */
    public void sendLikeMsg(int num, OnMsgCallBack callBack) {
        EmCustomMsgHelper.getInstance().sendPraiseMsg(num, new OnMsgCallBack() {
            @Override
            public void onSuccess(EMMessage message) {
                DemoHelper.saveLikeInfo(message);
                if(callBack != null) {
                    callBack.onSuccess(message);
                }
            }

            @Override
            public void onProgress(int i, String s) {
                super.onProgress(i, s);
                if(callBack != null) {
                    callBack.onProgress(i, s);
                }
            }

            @Override
            public void onError(String messageId, int code, String error) {
                if(callBack != null) {
                    callBack.onError(code, error);
                    callBack.onError(messageId, code, error);
                }
            }
        });
    }

    /**
     * 发送弹幕消息
     * @param content
     * @param callBack
     */
    public void sendBarrageMsg(String content, OnMsgCallBack callBack) {
        EmCustomMsgHelper.getInstance().sendBarrageMsg(content, callBack);
    }

    /**
     * 获取礼物消息中礼物的id
     * @param msg
     * @return
     */
    public String getMsgGiftId(EMMessage msg) {
        return EmCustomMsgHelper.getInstance().getMsgGiftId(msg);
    }

    /**
     * 获取礼物消息中礼物的数量
     * @param msg
     * @return
     */
    public int getMsgGiftNum(EMMessage msg) {
        return EmCustomMsgHelper.getInstance().getMsgGiftNum(msg);
    }

    /**
     * 获取点赞消息中点赞的数目
     * @param msg
     * @return
     */
    public int getMsgPraiseNum(EMMessage msg) {
        return EmCustomMsgHelper.getInstance().getMsgPraiseNum(msg);
    }

    /**
     * 获取弹幕消息中的文本
     * @param msg
     * @return
     */
    public String getMsgBarrageTxt(EMMessage msg) {
        return EmCustomMsgHelper.getInstance().getMsgBarrageTxt(msg);
    }


    /**
     * 判断是否是礼物消息
     * @param msg
     * @return
     */
    public boolean isGiftMsg(EMMessage msg) {
        return EmCustomMsgHelper.getInstance().isGiftMsg(msg);
    }

    /**
     * 判断是否是点赞消息
     * @param msg
     * @return
     */
    public boolean isPraiseMsg(EMMessage msg) {
        return EmCustomMsgHelper.getInstance().isPraiseMsg(msg);
    }

    /**
     * 判断是否是弹幕消息
     * @param msg
     * @return
     */
    public boolean isBarrageMsg(EMMessage msg) {
        return EmCustomMsgHelper.getInstance().isBarrageMsg(msg);
    }

    public void setOnCustomMsgReceiveListener(OnCustomMsgReceiveListener listener) {
        EmCustomMsgHelper.getInstance().setOnCustomMsgReceiveListener(listener);
    }
}
