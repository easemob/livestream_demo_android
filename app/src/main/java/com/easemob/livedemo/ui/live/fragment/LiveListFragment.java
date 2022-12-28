package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easemob.livedemo.ui.live.adapter.LiveListAdapter;
import com.easemob.livedemo.ui.live.viewmodels.LiveListViewModel;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.inf.OnUpdateUserInfoListener;
import com.easemob.livedemo.common.livedata.LiveDataBus;
import com.easemob.livedemo.common.utils.DemoHelper;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.repository.UserRepository;
import com.easemob.livedemo.ui.base.BaseFragment;
import com.easemob.livedemo.ui.base.GridMarginDecoration;
import com.easemob.livedemo.ui.widget.LiveListRefreshHeader;

public class LiveListFragment extends BaseFragment implements OnItemClickListener {
    protected SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    protected static int pageSize;
    protected String cursor;
    protected boolean hasMoreData;
    private boolean isLoading;
    protected boolean isLoadMore;
    public LiveListAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private String status;
    protected LiveListViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        initArgument();
        return inflater.inflate(R.layout.fragment_live_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initViewModel();
        initListener();
        initData();
        refreshList();
    }

    private void initArgument() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            status = bundle.getString("status");
        }
    }

    private void initView() {
        if (null == getView()) {
            return;
        }
        recyclerView = getView().findViewById(R.id.recycleview);
        gridLayoutManager = new GridLayoutManager(mContext, 2, RecyclerView.VERTICAL, false);
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        refreshLayout = getView().findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshHeader(new LiveListRefreshHeader(mContext));
        recyclerView.addItemDecoration(new GridMarginDecoration(mContext, 4));
        adapter = new LiveListAdapter();
        adapter.setEmptyView(R.layout.live_list_empty);
        recyclerView.setAdapter(adapter);

        adapter.setStatus(status);
    }

    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(LiveListViewModel.class);
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshList();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && hasMoreData
                        && !isLoading
                        && gridLayoutManager.findLastVisibleItemPosition() == gridLayoutManager.getItemCount() - 1) {
                    showLiveList(true);
                }
            }
        });
        adapter.setOnItemClickListener(this);

        LiveDataBus.get().with(DemoConstants.FRESH_LIVE_LIST, Boolean.class)
                .observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if (aBoolean != null && aBoolean) {
                            int limit = pageSize;
                            try {
                                limit = adapter.getData().size();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            isLoadMore = false;
                            loadLiveList(limit, null);
                        }
                    }
                });
    }

    protected void refreshList() {
        showLiveList(false);
    }

    protected void initData() {
        pageSize = 10;
        refreshLayout.setEnableRefresh(true);
    }

    protected void showLiveList(final boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
        if (!isLoadMore) {
            cursor = null;
        }
        loadLiveList(pageSize, cursor);
    }

    protected void loadLiveList(int limit, String cursor) {
        viewModel.getLiveRoomList(limit, cursor);
    }

    protected void hideLoadingView(boolean isLoadMore) {
        isLoading = false;
    }

    private boolean isOngoingLive() {
        return !TextUtils.isEmpty(status) && TextUtils.equals(status, DemoConstants.LIVE_ONGOING);
    }

    @Override
    public void onItemClick(View view, int position) {
        LiveRoom liveRoom = adapter.getItem(position);
        String status = liveRoom.getStatus();
        boolean living = DemoHelper.isLiving(status);
        if (living) {
            if (TextUtils.equals(liveRoom.getOwner(), EMClient.getInstance().getCurrentUser())) {
                // LiveAnchorActivity.actionStart(mContext, liveRoom);
            } else {
                showDialog();
            }
        } else {
            // LiveAnchorActivity.actionStart(mContext, liveRoom);
        }
    }

    private void showDialog() {
        Toast.makeText(mContext, R.string.live_list_warning, Toast.LENGTH_SHORT).show();
    }

    protected void setAdapterData(List<LiveRoom> data) {
        if (data == null) {
            return;
        }
        List<String> anchorList = new ArrayList<>(data.size());
        for (LiveRoom liveRoom : data) {
            if (!anchorList.contains(liveRoom.getOwner())) {
                anchorList.add(liveRoom.getOwner());
            }
        }

        if (anchorList.size() == 0) {
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter.setData(data);
        } else {
            UserRepository.getInstance().fetchUserInfo(new ArrayList<>(anchorList), new OnUpdateUserInfoListener() {
                @Override
                public void onSuccess(Map<String, EMUserInfo> userInfoMap) {
                    if (null != LiveListFragment.this.getActivity()) {
                        LiveListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setLayoutManager(gridLayoutManager);
                                adapter.setData(data);
                            }
                        });
                    }
                }

                @Override
                public void onError(int error, String errorMsg) {

                }
            });
        }


    }
}
