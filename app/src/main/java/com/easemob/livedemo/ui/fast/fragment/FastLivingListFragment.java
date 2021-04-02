package com.easemob.livedemo.ui.fast.fragment;

import android.view.View;

import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.restapi.model.ResponseModule;
import com.easemob.livedemo.ui.fast.FastLiveAudienceActivity;
import com.easemob.livedemo.ui.live.LiveAudienceActivity;
import com.easemob.livedemo.ui.live.fragment.LiveListFragment;

import java.util.List;

public class FastLivingListFragment extends LiveListFragment {
    private List<LiveRoom> vodList;
    private static final int MAX_VOD_COUNT = 2;

    @Override
    public void onItemClick(View view, int position) {
        LiveRoom liveRoom = adapter.getItem(position);
        if(DemoHelper.isFastLiveType(liveRoom.getVideo_type())) {
            FastLiveAudienceActivity.actionStart(mContext, liveRoom);
        }else {
            LiveAudienceActivity.actionStart(mContext, liveRoom);
        }
    }


    @Override
    protected void initViewModel() {
        super.initViewModel();
        viewModel.getFastVodRoomsObservable().observe(getViewLifecycleOwner(), response -> {
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
        viewModel.getFastRoomsObservable().observe(getViewLifecycleOwner(), response -> {
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
        viewModel.getFastVodRoomList(MAX_VOD_COUNT, null);
    }

    @Override
    protected void refreshList() {
        viewModel.getFastVodRoomList(MAX_VOD_COUNT, null);
    }

    @Override
    protected void loadLiveList(int limit, String cursor) {
        viewModel.getFastRoomList(limit, cursor);
    }
}

