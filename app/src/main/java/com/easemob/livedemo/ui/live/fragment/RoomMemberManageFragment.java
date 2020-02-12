package com.easemob.livedemo.ui.live.fragment;

import android.text.TextUtils;
import android.view.View;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.activity.RoomUserManagementFragment;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

public class RoomMemberManageFragment extends RoomUserManagementFragment {

    @Override
    protected List<String> getDataFromServer() throws HyphenateException {
        EMChatRoom chatRoom = chatRoomManager.fetchChatRoomFromServer(chatroomId, true);
        List<String> allMembers = new ArrayList<>();
        List<String> memberList = chatRoom.getMemberList();
        allMembers.add(chatRoom.getOwner());
        if(chatRoom.getAdminList() != null) {
            allMembers.addAll(chatRoom.getAdminList());
        }
        if(memberList != null) {
            allMembers.addAll(memberList);
        }
        return allMembers;
    }

    @Override
    protected void showOtherInfo(ManagementAdapter.ManagementViewHolder holder, List<String> userList, int position) {
        String username = userList.get(position);
        EMChatRoom chatRoom = chatRoomManager.getChatRoom(chatroomId);
        String currentUser = EMClient.getInstance().getCurrentUser();
        boolean isAnchor = TextUtils.equals(currentUser, chatRoom.getOwner()) || chatRoom.getAdminList().contains(currentUser);
        if(isAnchor){
            holder.managerButton.setVisibility(View.VISIBLE);
            holder.managerButton.setText(getString(R.string.em_live_anchor_mute_all));
            holder.tvLabel.setVisibility(View.VISIBLE);
            holder.tvLabel.setBackground(ContextCompat.getDrawable(mContext, R.drawable.em_live_member_label_shape));
            holder.tvLabel.setText(getString(R.string.em_live_anchor_self));
        }else {
            holder.tvLabel.setVisibility(View.GONE);
            holder.managerButton.setVisibility(View.VISIBLE);
            holder.managerButton.setText(getString(R.string.em_live_anchor_mute));
        }

        holder.managerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAnchor) {

                }else {

                }
            }
        });
    }
}
