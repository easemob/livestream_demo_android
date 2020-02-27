package com.easemob.livedemo.ui.activity;

import android.content.Intent;
import android.view.View;

import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.live.LiveAudienceActivity;

public class LivingListFragment extends LiveListFragment {

    @Override
    public void onItemClick(View view, int position) {
        LiveRoom liveRoom = adapter.getItem(position);
        startActivity(new Intent(mContext, LiveAudienceActivity.class)
                .putExtra("liveroom", liveRoom));
    }

    @Override
    protected void showLiveList(boolean isLoadMore) {
        viewModel.getLivingRoomLists(pageSize);
    }
}
