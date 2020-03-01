package com.easemob.livedemo.ui.live.fragment;

import android.view.View;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.ui.activity.RoomUserManagementFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

public class RoomMemberManageFragment extends RoomUserManagementFragment {

    @Override
    protected void initViewModel() {
        super.initViewModel();
        viewModel.getMuteObservable().observe(getViewLifecycleOwner(), response-> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    muteList = data;
                }
            });
        });
    }

    @Override
    protected void executeFetchTask() {
        viewModel.getMembers(chatroomId);
    }

    @Override
    protected void showOtherInfo(ManagementAdapter.ManagementViewHolder holder, List<String> userList, int position) {
        String username = userList.get(position);
        boolean isAnchor = DemoHelper.isOwner(username);
        boolean isMemberMuted = false;
        if(isAnchor){
            holder.managerButton.setVisibility(View.VISIBLE);
            holder.managerButton.setText(isAllMuted ? getString(R.string.em_live_anchor_unmute_all) : getString(R.string.em_live_anchor_mute_all));
            holder.tvLabel.setVisibility(View.VISIBLE);
            holder.tvLabel.setBackground(ContextCompat.getDrawable(mContext, R.drawable.em_live_member_label_shape));
            holder.tvLabel.setText(getString(R.string.em_live_anchor_self));
        }else {
            isMemberMuted = muteList.contains(username);
            if(isMemberMuted) {
                holder.managerButton.setVisibility(View.GONE);
                holder.tvLabel.setVisibility(View.VISIBLE);
                holder.tvLabel.setBackground(ContextCompat.getDrawable(mContext, R.drawable.em_live_member_label_mute_shape));
                holder.tvLabel.setText(getString(R.string.em_live_anchor_muted));
            }else {
                holder.tvLabel.setVisibility(View.GONE);
                holder.managerButton.setVisibility(View.VISIBLE);
                holder.managerButton.setText(getString(R.string.em_live_anchor_mute));
            }

        }

        holder.managerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAnchor) {
                    if(isAllMuted) {
                        viewModel.unMuteAllMembers(chatroomId);
                    }else {
                        viewModel.muteAllMembers(chatroomId);
                    }
                }else {
                    List<String> list = new ArrayList<>();
                    list.add(username);
                    viewModel.muteChatRoomMembers(chatroomId, list, -1);
                }
            }
        });
    }
}
