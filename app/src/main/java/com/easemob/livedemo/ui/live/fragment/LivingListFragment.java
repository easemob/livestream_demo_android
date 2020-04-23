package com.easemob.livedemo.ui.live.fragment;

import android.content.Intent;
import android.view.View;

import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.live.LiveAudienceActivity;
import com.easemob.livedemo.ui.live.fragment.LiveListFragment;
import com.easemob.qiniu_sdk.OnCallBack;
import com.easemob.qiniu_sdk.PushStreamHelper;

public class LivingListFragment extends LiveListFragment {

    @Override
    public void onItemClick(View view, int position) {
        LiveRoom liveRoom = adapter.getItem(position);
        LiveAudienceActivity.actionStart(mContext, liveRoom);
    }

    @Override
    protected void showLiveList(boolean isLoadMore) {
        viewModel.getLivingRoomLists(pageSize);
    }
}
