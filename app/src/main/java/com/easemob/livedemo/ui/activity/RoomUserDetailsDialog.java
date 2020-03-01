package com.easemob.livedemo.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.common.reponsitories.Resource;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.viewmodels.UserDetailManageViewModel;
import com.easemob.livedemo.ui.viewmodels.UserManageViewModel;
import com.easemob.livedemo.utils.Utils;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by wei on 2016/7/25.
 */
public class RoomUserDetailsDialog extends DialogFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_username)
    TextView usernameView;
    @BindView(R.id.btn_set_admin)
    Button setAdminButton;
    @BindView(R.id.layout_management)
    Group managementLayout;
    @BindView(R.id.iv_avatar)
    EaseImageView ivAvatar;
    @BindView(R.id.tv_fan_num)
    TextView tvFanNum;
    @BindView(R.id.tv_attention_num)
    TextView tvAttentionNum;
    @BindView(R.id.tv_gift_num)
    TextView tvGiftNum;
    @BindView(R.id.tv_mute_status)
    TextView tvMuteStatus;
    @BindView(R.id.tv_white_status)
    TextView tvWhiteStatus;

    private BaseActivity mContext;
    private String username;
    private String chatroomId;
    private String liveId;
    private String type;
    private UserManageViewModel viewModel;
    private UserDetailManageViewModel detailViewModel;
    private List<String> whiteList;
    private List<String> muteList;

    public static RoomUserDetailsDialog newInstance(String username, LiveRoom liveRoom) {
        return RoomUserDetailsDialog.newInstance(username,liveRoom, DemoConstants.TYPE_AUDIENCE);
    }

    public static RoomUserDetailsDialog newInstance(String username, LiveRoom liveRoom, String type) {
        RoomUserDetailsDialog dialog = new RoomUserDetailsDialog();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putSerializable("liveRoom", liveRoom);
        args.putString("type", type);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_user_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        customDialog();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString("username");
            LiveRoom liveRoom = (LiveRoom) getArguments().getSerializable("liveRoom");
            type = getArguments().getString("type");
            chatroomId = liveRoom.getChatroomId();
            liveId = liveRoom.getId();
        }
        if (username.equals(EMClient.getInstance().getCurrentUser())) {
            managementLayout.setVisibility(View.GONE);
        }
        if(TextUtils.equals(type, DemoConstants.TYPE_ANCHOR) && !DemoHelper.isOwner(username)) {
            managementLayout.setVisibility(View.VISIBLE);
        }
        if (username != null) {
            usernameView.setText(DemoHelper.getNickName(username));
            ivAvatar.setImageResource(DemoHelper.getAvatarResource(username, R.drawable.ease_default_avatar));
        }
        //mentionBtn.setText("@TA");
    }

    private void customDialog() {
        getDialog().setCanceledOnTouchOutside(true);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(UserManageViewModel.class);
        detailViewModel = new ViewModelProvider(this).get(UserDetailManageViewModel.class);
        viewModel.getMuteObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    muteList = data;
                    if(data.contains(username)) {
                        tvMuteStatus.setText(getString(R.string.em_live_anchor_muted));
                    }else {
                        tvMuteStatus.setText(getString(R.string.em_live_anchor_mute));
                    }
                }
            });
        });
        viewModel.getObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    whiteList = data;
                    if(data.contains(username)) {
                        tvWhiteStatus.setText(getString(R.string.em_live_anchor_remove_from_white));
                    }else {
                        tvWhiteStatus.setText(getString(R.string.em_live_anchor_add_white));
                    }
                }
            });
        });
        detailViewModel.getWhiteObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(EMChatRoom data) {
                    viewModel.getWhiteList(chatroomId);
                }
            });
        });

        detailViewModel.getMuteObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(EMChatRoom data) {
                    viewModel.getMuteList(chatroomId);
                }
            });
        });
        viewModel.getWhiteList(chatroomId);
        viewModel.getMuteList(chatroomId);
    }

    @OnClick(R.id.layout_live_no_talk)
    void mute() {
        if (chatroomId != null) {
            if(muteList != null && muteList.contains(username)) {
                detailViewModel.unMuteChatRoomMembers(chatroomId, getUserList());
            }else {
                detailViewModel.muteChatRoomMembers(chatroomId, getUserList(), -1);
            }

        }
    }

    @OnClick(R.id.layout_live_add_whitelist)
    void addToBlacklist() {
        if (chatroomId != null) {
            if(whiteList != null && whiteList.contains(username)) {
                detailViewModel.removeFromChatRoomWhiteList(chatroomId, getUserList());
            }else {
                detailViewModel.addToChatRoomWhiteList(chatroomId, getUserList());
            }
        }
    }

    @OnClick(R.id.layout_live_kick)
    void kickMember() {
        EMClient.getInstance()
                .chatroomManager()
                .asyncRemoveChatRoomMembers(chatroomId, getUserList(),
                        new EMValueCallBack<EMChatRoom>() {
                            @Override
                            public void onSuccess(EMChatRoom value) {
                                if (eventListener != null) {
                                    eventListener.onKickMember(username);
                                }
                                showToast("踢出成功");
                            }

                            @Override
                            public void onError(int error, String errorMsg) {
                                showToast("踢出失败");
                            }
                        });
    }

    @OnClick(R.id.btn_set_admin)
    void setToAdmin() {
        EMClient.getInstance()
                .chatroomManager()
                .asyncAddChatRoomAdmin(chatroomId, username, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        showToast("设置房管成功");
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private RoomManageEventListener eventListener;

    public void setManageEventListener(RoomManageEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public interface RoomManageEventListener {
        void onKickMember(String username);

        void onAddBlacklist(String username);

    }


    /**
     * 解析Resource<T>
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if(mContext != null) {
            mContext.parseResource(response, callback);
        }
    }
}
