package com.easemob.livedemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.widget.BarrageLayout;
import com.easemob.livedemo.ui.widget.LiveLeftGiftView;
import com.easemob.livedemo.ui.widget.PeriscopeLayout;
import com.easemob.livedemo.ui.widget.RoomMessagesView;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.util.EMLog;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wei on 2016/6/12.
 */
public class LiveBaseActivity extends BaseActivity{
    protected static final String TAG = "LiveActivity";

    @BindView(R.id.left_gift_view) LiveLeftGiftView leftGiftView;
    @BindView(R.id.message_view) RoomMessagesView messageView;
    @BindView(R.id.periscope_layout) PeriscopeLayout periscopeLayout;
    @BindView(R.id.bottom_bar) View bottomBar;

    @BindView(R.id.barrage_layout)
    BarrageLayout barrageLayout;

    protected String roomChatId = "";
    protected boolean isMessageListInited;
    protected EMChatRoomChangeListener chatRoomChangeListener;


    volatile boolean isGiftShowing = false;
    List<String> toShowList = Collections.synchronizedList(new LinkedList<String>());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected synchronized void showLeftGiftVeiw(String name) {
        if(!isGiftShowing) {
            showGiftDerect(name);
        }else {
            toShowList.add(name);
        }
    }

    private void showGiftDerect(final String name){
        isGiftShowing = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leftGiftView.setVisibility(View.VISIBLE);
                leftGiftView.setName(name);
                leftGiftView.setTranslationY(0);
                ViewAnimator.animate(leftGiftView)
                        .alpha(0,1)
                        .translationX(-leftGiftView.getWidth(),0)
                        .duration(600)
                        .thenAnimate(leftGiftView)
                        .alpha(1,0)
                        .translationY(-1.5f*leftGiftView.getHeight())
                        .duration(800)
                        .onStop(new AnimationListener.Stop() {
                            @Override
                            public void onStop() {
                                String pollName = null;
                                try {
                                    pollName = toShowList.remove(0);
                                }catch (Exception e){

                                }
                                if(pollName != null) {
                                    showGiftDerect(pollName);
                                }else{
                                    isGiftShowing = false;
                                }
                            }
                        })
                        .startDelay(2000)
                        .start();
                ViewAnimator.animate(leftGiftView.getGiftImageView())
                        .translationX(-leftGiftView.getGiftImageView().getX(),0)
                        .duration(1100)
                        .start();
            }
        });
    }

    protected void addChatRoomChangeListenr() {
        chatRoomChangeListener = new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(roomChatId)) {
                    EMLog.e(TAG, " room : " + roomId + " with room name : " + roomName + " was destroyed");
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                message.setReceipt(roomChatId);
                message.setFrom(participant);
                EMTextMessageBody textMessageBody = new EMTextMessageBody("来了");
                message.addBody(textMessageBody);
                message.setChatType(EMMessage.ChatType.ChatRoom);
                EMClient.getInstance().chatManager().saveMessage(message);
                messageView.refreshSelectLast();
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {
//                showChatroomToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
            }

            @Override
            public void onMemberKicked(String roomId, String roomName, String participant) {
                if (roomId.equals(roomChatId)) {
                    String curUser = EMClient.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMClient.getInstance().chatroomManager().leaveChatRoom(roomId);
                        showToast("你已被移除出此房间");
                        finish();
                    } else {
//                        showChatroomToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                    }
                }
            }

        };

        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomChangeListener);
    }


    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(roomChatId)) {
                    if(message.getBooleanAttribute(DemoConstants.EXTRA_IS_BARRAGE_MSG, false)){
                        barrageLayout.addBarrage(((EMTextMessageBody)message.getBody()).getMessage(), message.getFrom());
                    }
                    messageView.refreshSelectLast();
                } else {
                    // 如果消息不是和当前聊天ID的消息
                    EaseUI.getInstance().getNotifier().onNewMsg(message);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            EMMessage message = messages.get(messages.size()-1);
            if(DemoConstants.CMD_GIFT.equals(((EMCmdMessageBody)message.getBody()).action())){
                showLeftGiftVeiw(message.getFrom());

            }
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            if (isMessageListInited) {
//                messageList.refresh();
            }
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            if (isMessageListInited) {
//                messageList.refresh();
            }
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            if (isMessageListInited) {
                messageView.refresh();
            }
        }
    };

    protected void onMessageListInit(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageView.init(roomChatId);
                messageView.setMessageViewListener(new RoomMessagesView.MessageViewListener() {
                    @Override
                    public void onMessageSend(String content) {
                        EMMessage message = EMMessage.createTxtSendMessage(content, roomChatId);
                        if(messageView.isBarrageShow){
                            message.setAttribute(DemoConstants.EXTRA_IS_BARRAGE_MSG, true);
                            barrageLayout.addBarrage(content, EMClient.getInstance().getCurrentUser());
                        }
                        message.setChatType(EMMessage.ChatType.ChatRoom);
                        EMClient.getInstance().chatManager().sendMessage(message);
                        message.setMessageStatusCallback(new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                //刷新消息列表
                                messageView.refreshSelectLast();
                            }

                            @Override
                            public void onError(int i, String s) {
                                showToast("消息发送失败！");
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
                    }

                    @Override
                    public void onHiderBottomBar() {
                        bottomBar.setVisibility(View.VISIBLE);
                        messageView.setShowInputView(false);
                    }
                });
                messageView.setVisibility(View.VISIBLE);
                bottomBar.setVisibility(View.VISIBLE);
                isMessageListInited = true;
            }
        });
    }

    @OnClick(R.id.root_layout)
    void onRootLayoutClick() {
        periscopeLayout.addHeart();
    }

    @OnClick(R.id.comment_image) void onCommentImageClick(){
        messageView.setShowInputView(true);
        bottomBar.setVisibility(View.INVISIBLE);
    }
    @OnClick(R.id.present_image) void onPresentImageClick(){
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setReceipt(roomChatId);
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(DemoConstants.CMD_GIFT);
        message.addBody(cmdMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().sendMessage(message);
        showLeftGiftVeiw(EMClient.getInstance().getCurrentUser());
    }

    @OnClick(R.id.chat_image) void onChatImageClck(){

    }
}
