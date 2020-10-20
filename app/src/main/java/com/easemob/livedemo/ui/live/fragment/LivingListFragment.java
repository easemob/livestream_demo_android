package com.easemob.livedemo.ui.live.fragment;

import android.view.View;

import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.restapi.model.ResponseModule;
import com.easemob.livedemo.ui.live.LiveAudienceActivity;

import java.util.List;

public class LivingListFragment extends LiveListFragment {
    private static final int MAX_VOD_COUNT = 2;
    private List<LiveRoom> vodList;

    @Override
    public void onItemClick(View view, int position) {
        LiveRoom liveRoom = adapter.getItem(position);
        LiveAudienceActivity.actionStart(mContext, liveRoom);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        viewModel.getVodRoomsObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<ResponseModule<List<LiveRoom>>>() {
                @Override
                public void onSuccess(ResponseModule<List<LiveRoom>> data) {
                    vodList = data.data;
                    showLiveList(false);
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    showLiveList(false);
                }
            });
        });
        viewModel.getLivingRoomsObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<ResponseModule<List<LiveRoom>>>() {
                @Override
                public void onSuccess(ResponseModule<List<LiveRoom>> data) {
                    cursor = data.cursor;
                    hasMoreData = true;
                    List<LiveRoom> livingRooms = data.data;
                    if(livingRooms.size() < pageSize) {
                        hasMoreData = false;
                    }
                    if(isLoadMore) {
                        adapter.addData(livingRooms);
                    }else {
                        if(vodList != null && livingRooms != null) {
                            livingRooms.addAll(0, vodList);
                        }
                        adapter.setData(livingRooms);
                    }
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    hideLoadingView(isLoadMore);
                }
            });
        });
    }

    @Override
    protected void initData() {
        swipeRefreshLayout.setRefreshing(true);
        viewModel.getVodRoomList(MAX_VOD_COUNT, null);
    }

    @Override
    protected void refreshList() {
        viewModel.getVodRoomList(MAX_VOD_COUNT, null);
    }

    @Override
    protected void loadLiveList(int limit, String cursor) {
        viewModel.getLivingRoomList(limit, cursor);
    }
}
