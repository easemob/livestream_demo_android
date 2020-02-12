package com.easemob.livedemo.ui.live.fragment;

import android.view.View;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.activity.RoomUserManagementFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class RoomWhiteManageFragment extends RoomUserManagementFragment {
    @Override
    protected List<String> getDataFromServer() throws HyphenateException {
        return chatRoomManager.fetchChatRoomBlackList(chatroomId, 1, 50);
    }

    @Override
    protected void showOtherInfo(ManagementAdapter.ManagementViewHolder holder, List<String> userList, int position) {
        holder.tvLabel.setVisibility(View.GONE);
        holder.managerButton.setVisibility(View.VISIBLE);
        holder.managerButton.setText(getString(R.string.em_live_anchor_remove_white));

        holder.managerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
