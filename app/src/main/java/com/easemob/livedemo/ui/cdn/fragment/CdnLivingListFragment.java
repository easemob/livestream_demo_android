package com.easemob.livedemo.ui.cdn.fragment;

import android.view.View;

import com.easemob.livedemo.common.callback.OnResourceParseCallback;
import com.easemob.livedemo.common.utils.DemoHelper;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.restapi.model.ResponseModule;
import com.easemob.livedemo.ui.live.fragment.LiveListFragment;

import java.util.ArrayList;
import java.util.List;

import com.easemob.livedemo.ui.cdn.CdnLiveAudienceActivity;

public class CdnLivingListFragment extends LiveListFragment {
    private List<LiveRoom> cdnLivingList;

    @Override
    public void onItemClick(View view, int position) {
        LiveRoom liveRoom = adapter.getItem(position);
        if (DemoHelper.isCdnLiveType(liveRoom.getVideo_type())) {
            CdnLiveAudienceActivity.actionStart(mContext, liveRoom);
        } else {
            // LiveAudienceActivity.actionStart(mContext, liveRoom);
        }
    }


    @Override
    protected void initViewModel() {
        super.initViewModel();
        viewModel.getFastVodRoomsObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<ResponseModule<List<LiveRoom>>>() {
                @Override
                public void onSuccess(ResponseModule<List<LiveRoom>> data) {
                    if (!isLoadMore) {
                        cdnLivingList.clear();
                    } else {
                        cursor = data.cursor;
                    }
                    hasMoreData = data.data.size() >= pageSize;
                    cdnLivingList.addAll(data.data);
                    setAdapterData(cdnLivingList);
                    refreshLayout.finishRefresh();
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
        super.initData();
        cdnLivingList = new ArrayList<>(0);
    }

    @Override
    protected void loadLiveList(int limit, String cursor) {
        viewModel.getFastCdnRoomList(limit, cursor);
    }

    public List<LiveRoom> getLiveRooms() {
        if (null != adapter) {
            return adapter.getData();
        }
        return new ArrayList<>(0);
    }
}

