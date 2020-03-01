package com.easemob.livedemo.ui.live.fragment;

import android.view.View;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.activity.RoomUserManagementFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;

public class RoomMuteManageFragment extends RoomUserManagementFragment {

    @Override
    protected void executeFetchTask() {
        viewModel.getMuteList(chatroomId);
    }

    @Override
    protected void showOtherInfo(ManagementAdapter.ManagementViewHolder holder, List<String> userList, int position) {
        holder.tvLabel.setVisibility(View.VISIBLE);
        holder.tvLabel.setText(getString(R.string.em_live_anchor_mute));
        holder.tvLabel.setBackground(ContextCompat.getDrawable(mContext, R.drawable.em_live_member_label_mute_shape));
        holder.managerButton.setVisibility(View.VISIBLE);
        holder.managerButton.setText(getString(R.string.em_live_anchor_remove_white));

        holder.managerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = new ArrayList<>();
                list.add(userList.get(position));
                viewModel.unMuteChatRoomMembers(chatroomId, list);
            }
        });
    }
}
