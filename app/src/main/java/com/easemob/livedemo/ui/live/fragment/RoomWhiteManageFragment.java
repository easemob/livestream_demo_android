package com.easemob.livedemo.ui.live.fragment;

import android.view.View;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.activity.RoomUserManagementFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

public class RoomWhiteManageFragment extends RoomUserManagementFragment {

    @Override
    protected void executeFetchTask() {
        viewModel.getWhiteList(chatroomId);
    }

    @Override
    protected void showOtherInfo(ManagementAdapter.ManagementViewHolder holder, List<String> userList, int position) {
        holder.tvLabel.setVisibility(View.GONE);
        holder.managerButton.setVisibility(View.VISIBLE);
        holder.managerButton.setText(getString(R.string.em_live_anchor_remove_white));

        holder.managerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = new ArrayList<>();
                list.add(userList.get(position));
                viewModel.removeFromChatRoomWhiteList(chatroomId, list);
            }
        });
    }
}
