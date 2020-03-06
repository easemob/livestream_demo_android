package com.easemob.livedemo.ui.live.fragment;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

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
        viewModel.getObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    Log.e("TAG", "getObservable = "+data.size());
                    setAdapter(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
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
            holder.switchMute.setVisibility(View.VISIBLE);
            holder.switchMute.setChecked(isAllMuted);
            holder.tvLabel.setVisibility(View.VISIBLE);
            holder.tvLabel.setText(getString(R.string.em_live_anchor_self));
            holder.tvMuteHint.setVisibility(View.VISIBLE);
        }else {
            holder.managerButton.setVisibility(View.GONE);
            holder.tvLabel.setVisibility(View.GONE);
            holder.tvLabel.setBackground(ContextCompat.getDrawable(mContext, R.drawable.em_live_member_label_mute_shape));
            holder.tvLabel.setText(getString(R.string.em_live_anchor_muted));
            holder.tvMuteHint.setVisibility(View.GONE);
            holder.switchMute.setVisibility(View.GONE);
        }

        holder.switchMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    viewModel.muteAllMembers(chatroomId);
                }else {
                    viewModel.unMuteAllMembers(chatroomId);
                }
            }
        });

    }
}
