package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.ui.base.BaseLiveDialogFragment;
import com.easemob.livedemo.ui.live.viewmodels.UserManageViewModel;
import com.hyphenate.chat.EMChatRoom;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;

public class RoomManageUserDialog extends BaseLiveDialogFragment implements View.OnClickListener {
    private TextView tvMute;
    private TextView tvWhite;
    private TextView tvCancel;
    private UserManageViewModel viewModel;
    private String username;
    private String roomId;
    private boolean isMuted;
    private boolean inWhiteList;

    public static RoomManageUserDialog getNewInstance(String chatroomId, String username) {
        RoomManageUserDialog dialog = new RoomManageUserDialog();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("chatroomid", chatroomId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public int getLayoutId() {
        return R.layout.em_fragment_dialog_live_manage_user;
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            username = bundle.getString("username");
            roomId = bundle.getString("chatroomid");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tvMute = findViewById(R.id.tv_mute);
        tvWhite = findViewById(R.id.tv_white);
        tvCancel = findViewById(R.id.tv_cancel);
    }

    @Override
    public void initViewModel() {
        super.initViewModel();
        viewModel = new ViewModelProvider(this).get(UserManageViewModel.class);
        viewModel.getChatRoomObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(EMChatRoom data) {
                    viewModel.getWhiteList(roomId);
                    viewModel.getMuteList(roomId);
                }
            });
        });
        viewModel.getWhitesObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    inWhiteList = data.contains(username);
                    tvWhite.setText(inWhiteList ?
                            getString(R.string.em_live_anchor_remove_from_white) :
                            getString(R.string.em_live_anchor_add_white));
                }
            });
        });
        viewModel.getMuteObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    isMuted = data.contains(username);
                    tvMute.setText(isMuted ?
                            getString(R.string.em_live_anchor_remove_mute) :
                            getString(R.string.em_live_anchor_mute));
                }
            });
        });
    }

    @Override
    public void initListener() {
        super.initListener();
        tvMute.setOnClickListener(this);
        tvWhite.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.getWhiteList(roomId);
        viewModel.getMuteList(roomId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_mute :
                if(isMuted) {
                    viewModel.unMuteChatRoomMembers(roomId, getList(username));
                }else {
                    viewModel.muteChatRoomMembers(roomId, getList(username), -1);
                }
                break;
            case R.id.tv_white :
                if(inWhiteList) {
                    viewModel.removeFromChatRoomWhiteList(roomId, getList(username));
                }else {
                    viewModel.addToChatRoomWhiteList(roomId, getList(username));
                }
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    public List<String> getList(String username) {
        List<String> list = new ArrayList<>();
        list.add(username);
        return list;
    }
}
