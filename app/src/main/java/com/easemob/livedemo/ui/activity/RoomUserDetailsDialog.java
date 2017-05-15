package com.easemob.livedemo.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.utils.Utils;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 2016/7/25.
 */
public class RoomUserDetailsDialog extends DialogFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_username) TextView usernameView;
    @BindView(R.id.btn_set_admin) Button setAdminButton;
    @BindView(R.id.layout_management) RelativeLayout managementLayout;

    private String username;
    private String chatroomId;
    private String liveId;

    public static RoomUserDetailsDialog newInstance(String username, LiveRoom liveRoom) {
        RoomUserDetailsDialog dialog = new RoomUserDetailsDialog();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putSerializable("liveRoom", liveRoom);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_user_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        customDialog();
        return view;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString("username");
            LiveRoom liveRoom = (LiveRoom) getArguments().getSerializable("liveRoom");
            chatroomId = liveRoom.getChatroomId();
            liveId = liveRoom.getId();

            EMChatRoom chatRoom = EMClient.getInstance().chatroomManager().getChatRoom(chatroomId);
            List<String> adminList = chatRoom.getAdminList();
            if(!EMClient.getInstance().getCurrentUser().equals(chatRoom.getOwner())) {
                setAdminButton.setVisibility(View.INVISIBLE);
                if (!adminList.contains(EMClient.getInstance().getCurrentUser()) ||
                        username.equals(EMClient.getInstance().getCurrentUser()) ||
                        username.equals(chatRoom.getOwner())) {
                    managementLayout.setVisibility(View.INVISIBLE);
                }
            }else{
                if(username.equals(EMClient.getInstance().getCurrentUser())){
                    setAdminButton.setVisibility(View.INVISIBLE);
                    managementLayout.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (username != null) {
            usernameView.setText(username);
        }
        //mentionBtn.setText("@TA");
    }

    private void customDialog() {
        getDialog().setCanceledOnTouchOutside(true);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    @OnClick(R.id.layout_live_no_talk) void mute() {
        if (chatroomId != null) {
            EMClient.getInstance()
                    .chatroomManager()
                    .asyncMuteChatRoomMembers(chatroomId, getUserList(), -1,
                            new EMValueCallBack<EMChatRoom>() {
                                @Override public void onSuccess(EMChatRoom value) {
                                    showToast("禁言成功");
                                }

                                @Override public void onError(int error, String errorMsg) {
                                    showToast("禁言失败");
                                }
                            });
        }
    }

    @OnClick(R.id.layout_live_add_blacklist) void addToBlacklist() {
        if (chatroomId != null) {
            EMClient.getInstance()
                    .chatroomManager()
                    .asyncBlockChatroomMembers(chatroomId, getUserList(),
                            new EMValueCallBack<EMChatRoom>() {
                                @Override public void onSuccess(EMChatRoom value) {
                                    if(eventListener != null){
                                        eventListener.onAddBlacklist(username);
                                    }
                                    showToast("加入黑名单成功");
                                }

                                @Override public void onError(int error, String errorMsg) {
                                    showToast("加入黑名单失败");
                                }
                            });
        }
    }

    @OnClick(R.id.layout_live_kick) void kickMember() {
        EMClient.getInstance()
                .chatroomManager()
                .asyncRemoveChatRoomMembers(chatroomId, getUserList(),
                        new EMValueCallBack<EMChatRoom>() {
                            @Override public void onSuccess(EMChatRoom value) {
                                if(eventListener != null){
                                    eventListener.onKickMember(username);
                                }
                                showToast("踢出成功");
                            }

                            @Override public void onError(int error, String errorMsg) {
                                showToast("踢出失败");
                            }
                        });
    }

    @OnClick(R.id.btn_set_admin) void setToAdmin() {
        EMClient.getInstance()
                .chatroomManager()
                .asyncAddChatRoomAdmin(chatroomId, username, new EMValueCallBack<EMChatRoom>() {
                    @Override public void onSuccess(EMChatRoom value) {
                        showToast("设置房管成功");
                    }

                    @Override public void onError(int error, String errorMsg) {
                        showToast("设置房管失败");
                    }
                });
        //ThreadPoolManager.getInstance().executeTask(new ThreadPoolManager.Task<Void>() {
        //  @Override public Void onRequest() throws HyphenateException {
        //    ApiManager.getInstance().grantLiveRoomAdmin(liveId, username);
        //    return null;
        //  }
        //
        //  @Override public void onSuccess(Void aVoid) {
        //    showToast("设置管理员成功");
        //  }
        //
        //  @Override public void onError(HyphenateException exception) {
        //    showToast("设置管理员失败");
        //  }
        //});
        //EMClient.getInstance().chatroomManager().asyncAddChatRoomAdmin(chatroomId, a);
    }

    private List<String> getUserList() {
        List<String> users = new ArrayList<>();
        users.add(username);
        return users;
    }

    private void showToast(final String toast) {
        Utils.showToast(getActivity(), toast);
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private RoomManageEventListener eventListener;
    public void setManageEventListener(RoomManageEventListener eventListener){
        this.eventListener = eventListener;
    }

    public interface RoomManageEventListener{
        void onKickMember(String username);

        void onAddBlacklist(String username);

    }
}
