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
    protected List<String> getDataFromServer() throws HyphenateException {
        Map<String, Long> map = chatRoomManager.fetchChatRoomMuteList(chatroomId, 1, 50);
        List<String> list = new ArrayList<String>(map.keySet());
        return list;
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

            }
        });
    }
}
