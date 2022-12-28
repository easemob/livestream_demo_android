package com.easemob.chatroom;

import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EaseLiveMessageHelper {
    private final static String TAG = EaseLiveMessageHelper.class.getSimpleName();
    private static EaseLiveMessageHelper instance;
    private final LiveMessageListener messageListener;
    private static String chatroomId;


    private EaseLiveMessageHelper() {
        messageListener = new LiveMessageListener();
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    public static EaseLiveMessageHelper getInstance() {
        if (instance == null) {
            synchronized (EaseLiveMessageHelper.class) {
                if (instance == null) {
                    instance = new EaseLiveMessageHelper();
                }
            }
        }
        return instance;
    }

    public void init(String chatRoomId) {
        chatroomId = chatRoomId;
        messageListener.setChatRoomId(chatRoomId);
    }

    public void addLiveMessageListener(OnLiveMessageListener liveMessageListener) {
        messageListener.addLiveMessageListener(liveMessageListener);
    }

    public void removeLiveMessageListener(OnLiveMessageListener liveMessageListener) {
        messageListener.removeLiveMessageListener(liveMessageListener);
    }


    /**
     * Send a text message
     *
     * @param content
     * @param callBack
     */
    public void sendTxtMsg(String content, OnSendLiveMessageCallBack callBack) {
        EMMessage message = EMMessage.createTxtSendMessage(content, chatroomId);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                if (null != callBack) {
                    callBack.onSuccess(message);
                }
            }

            @Override
            public void onError(int i, String s) {
                if (null != callBack) {
                    callBack.onError(i, s);
                }
                deleteMuteMsg(message.getMsgId(), i);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * send gift message
     *
     * @param giftId
     * @param num
     * @param callBack
     */
    public void sendGiftMsg(String chatRoomId, String giftId, int num, OnSendLiveMessageCallBack callBack) {
        Map<String, String> params = new HashMap<>();
        params.put(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_ID, giftId);
        params.put(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_NUM, String.valueOf(num));
        sendGiftMsg(chatRoomId, params, callBack);
    }

    /**
     * send gift message
     *
     * @param params
     * @param callBack
     */
    public void sendGiftMsg(String chatRoomId, Map<String, String> params, final OnSendLiveMessageCallBack callBack) {
        if (params.size() <= 0) {
            return;
        }
        sendCustomMsg(chatRoomId, EaseLiveMessageType.CHATROOM_GIFT.getName(), params, callBack);
    }

    /**
     * send praise message
     *
     * @param num
     * @param callBack
     */
    public void sendPraiseMsg(String chatRoomId, int num, OnSendLiveMessageCallBack callBack) {
        if (num <= 0) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(EaseLiveMessageConstant.LIVE_MESSAGE_PRAISE_KEY_NUM, String.valueOf(num));
        sendPraiseMsg(chatRoomId, params, callBack);
    }

    /**
     * send praise message
     *
     * @param params
     * @param callBack
     */
    public void sendPraiseMsg(String chatRoomId, Map<String, String> params, final OnSendLiveMessageCallBack callBack) {
        if (params.size() <= 0) {
            return;
        }
        sendCustomMsg(chatRoomId, EaseLiveMessageType.CHATROOM_PRAISE.getName(), params, callBack);
    }

    /**
     * send barrage message
     *
     * @param content
     * @param callBack
     */
    public void sendBarrageMsg(String chatRoomId, String content, final OnSendLiveMessageCallBack callBack) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(EaseLiveMessageConstant.LIVE_MESSAGE_BARRAGE_KEY_TXT, content);
        sendBarrageMsg(chatRoomId, params, callBack);
    }

    /**
     * send barrage message
     *
     * @param params
     * @param callBack
     */
    public void sendBarrageMsg(String chatRoomId, Map<String, String> params, final OnSendLiveMessageCallBack callBack) {
        if (params.size() <= 0) {
            return;
        }
        sendCustomMsg(chatRoomId, EaseLiveMessageType.CHATROOM_BARRAGE.getName(), params, callBack);
    }

    /**
     * send custom message
     *
     * @param event
     * @param params
     * @param callBack
     */
    public void sendCustomMsg(String chatRoomId, String event, Map<String, String> params, final OnSendLiveMessageCallBack callBack) {
        sendCustomMsg(chatRoomId, EMMessage.ChatType.ChatRoom, event, params, callBack);
    }

    /**
     * send custom message
     *
     * @param to
     * @param chatType
     * @param event
     * @param params
     * @param callBack
     */
    public void sendCustomMsg(String to, EMMessage.ChatType chatType, String event, Map<String, String> params, final OnSendLiveMessageCallBack callBack) {
        final EMMessage sendMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(event);
        body.setParams(params);
        sendMessage.addBody(body);
        sendMessage.setTo(to);
        sendMessage.setChatType(chatType);
        sendMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                if (callBack != null) {
                    callBack.onSuccess(sendMessage);
                }
            }

            @Override
            public void onError(int i, String s) {
                if (callBack != null) {
                    callBack.onError(i, s);
                }
                deleteMuteMsg(sendMessage.getMsgId(), i);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
        EMClient.getInstance().chatManager().sendMessage(sendMessage);
    }

    /**
     * get gift id from message
     *
     * @param msg
     * @return
     */
    public String getMsgGiftId(EMMessage msg) {
        if (!isGiftMsg(msg)) {
            return null;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if (params.containsKey(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_ID)) {
            return params.get(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_ID);
        }
        return null;
    }

    /**
     * get the number of gift in message
     *
     * @param msg
     * @return
     */
    public int getMsgGiftNum(EMMessage msg) {
        if (!isGiftMsg(msg)) {
            return 0;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if (params.containsKey(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_NUM)) {
            String num = params.get(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_NUM);
            if (TextUtils.isEmpty(num)) {
                return 0;
            }
            try {
                return Integer.parseInt(num);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * get the number of praise in message
     *
     * @param msg
     * @return
     */
    public int getMsgPraiseNum(EMMessage msg) {
        if (!isPraiseMsg(msg)) {
            return 0;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if (params.containsKey(EaseLiveMessageConstant.LIVE_MESSAGE_PRAISE_KEY_NUM)) {
            String num = params.get(EaseLiveMessageConstant.LIVE_MESSAGE_PRAISE_KEY_NUM);
            if (TextUtils.isEmpty(num)) {
                return 0;
            }
            try {
                return Integer.parseInt(num);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * get the content of barrage in message
     *
     * @param msg
     * @return
     */
    public String getMsgBarrageTxt(EMMessage msg) {
        if (!isBarrageMsg(msg)) {
            return null;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if (params.containsKey(EaseLiveMessageConstant.LIVE_MESSAGE_BARRAGE_KEY_TXT)) {
            return params.get(EaseLiveMessageConstant.LIVE_MESSAGE_BARRAGE_KEY_TXT);
        }
        return null;
    }

    /**
     * whether is gift message
     *
     * @param msg
     * @return
     */
    public boolean isGiftMsg(EMMessage msg) {
        return getCustomMsgType(getCustomEvent(msg)) == EaseLiveMessageType.CHATROOM_GIFT;
    }

    /**
     * whether is praise message
     *
     * @param msg
     * @return
     */
    public boolean isPraiseMsg(EMMessage msg) {
        return getCustomMsgType(getCustomEvent(msg)) == EaseLiveMessageType.CHATROOM_PRAISE;
    }

    /**
     * whether is barrage message
     *
     * @param msg
     * @return
     */
    public boolean isBarrageMsg(EMMessage msg) {
        return getCustomMsgType(getCustomEvent(msg)) == EaseLiveMessageType.CHATROOM_BARRAGE;
    }

    /**
     * get the event of custom message
     *
     * @param message
     * @return
     */
    public String getCustomEvent(EMMessage message) {
        if (message == null) {
            return null;
        }
        if (!(message.getBody() instanceof EMCustomMessageBody)) {
            return null;
        }
        return ((EMCustomMessageBody) message.getBody()).event();
    }

    /**
     * get the params of message
     *
     * @param message
     * @return
     */
    public Map<String, String> getCustomMsgParams(EMMessage message) {
        if (message == null) {
            return null;
        }
        EMMessageBody body = message.getBody();
        if (!(body instanceof EMCustomMessageBody)) {
            return null;
        }
        return ((EMCustomMessageBody) body).getParams();
    }

    /**
     * get the type of custom message
     *
     * @param event
     * @return
     */
    public EaseLiveMessageType getCustomMsgType(String event) {
        if (TextUtils.isEmpty(event)) {
            return null;
        }
        return EaseLiveMessageType.fromName(event);
    }

    private void deleteMuteMsg(String messageId, int code) {
        if (code == EMError.USER_MUTED || code == EMError.MESSAGE_ILLEGAL_WHITELIST) {
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(chatroomId, EMConversation.EMConversationType.ChatRoom, true);
            conversation.removeMessage(messageId);
        }
    }

    static class LiveMessageListener implements EMMessageListener {
        private String chatRoomId;
        private final List<OnLiveMessageListener> liveMessageListeners;

        public LiveMessageListener() {
            liveMessageListeners = new ArrayList<>();
        }

        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;

        }

        public void addLiveMessageListener(OnLiveMessageListener liveMessageListener) {
            if (liveMessageListener == null) {
                return;
            }
            if (!liveMessageListeners.contains(liveMessageListener)) {
                liveMessageListeners.add(liveMessageListener);
            }
        }

        public void removeLiveMessageListener(OnLiveMessageListener liveMessageListener) {
            if (liveMessageListener == null) {
                return;
            }
            liveMessageListeners.remove(liveMessageListener);
        }

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            EMLog.i(TAG, "onMessageReceived messages size=" + messages.size());
            for (EMMessage message : messages) {
                if (message.getType() != EMMessage.Type.CUSTOM) {
                    continue;
                }

                if (message.getChatType() != EMMessage.ChatType.GroupChat && message.getChatType() != EMMessage.ChatType.ChatRoom) {
                    continue;
                }
                String username = message.getTo();

                if (!TextUtils.equals(username, chatRoomId)) {
                    continue;
                }

                EMCustomMessageBody body = (EMCustomMessageBody) message.getBody();
                String event = body.event();

                if (TextUtils.isEmpty(event)) {
                    continue;
                }
                EaseLiveMessageType msgType = EaseLiveMessageHelper.getInstance().getCustomMsgType(event);
                if (msgType == null) {
                    continue;
                }

                switch (msgType) {
                    case CHATROOM_GIFT:
                        for (OnLiveMessageListener liveMessageListener : liveMessageListeners) {
                            liveMessageListener.onGiftMessageReceived(message);
                        }
                        break;
                    case CHATROOM_PRAISE:
                        for (OnLiveMessageListener liveMessageListener : liveMessageListeners) {
                            liveMessageListener.onPraiseMessageReceived(message);
                        }
                        break;
                    case CHATROOM_BARRAGE:
                        for (OnLiveMessageListener liveMessageListener : liveMessageListeners) {
                            liveMessageListener.onBarrageMessageReceived(message);
                        }
                        break;
                }
            }

            for (OnLiveMessageListener liveMessageListener : liveMessageListeners) {
                liveMessageListener.onMessageReceived(messages);
            }
        }
    }
}
