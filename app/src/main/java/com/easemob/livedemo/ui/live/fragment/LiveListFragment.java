package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.LiveDataBus;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnItemClickListener;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.base.GridMarginDecoration;
import com.easemob.livedemo.ui.live.adapter.LiveListAdapter;
import com.easemob.livedemo.ui.base.BaseFragment;
import com.easemob.livedemo.ui.live.LiveAnchorActivity;
import com.easemob.livedemo.ui.live.viewmodels.LiveListViewModel;
import com.easemob.qiniu_sdk.OnCallBack;
import com.easemob.qiniu_sdk.PushStreamHelper;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveListFragment extends BaseFragment implements OnItemClickListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar loadmorePB;

    protected static final int pageSize = 100;
    private String cursor;
    private boolean hasMoreData;
    private boolean isLoading;
    private final List<LiveRoom> liveRoomList = new ArrayList<>();
    public LiveListAdapter adapter;
    private GridLayoutManager layoutManager;
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();
        initData();
    }

    private void initArgument() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            status = bundle.getString("status");
        }
    }

    private void initView() {
        loadmorePB = (ProgressBar) getView().findViewById(R.id.pb_load_more);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycleview);
        layoutManager = new GridLayoutManager(mContext, 2, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridMarginDecoration(mContext,3));
        adapter = new LiveListAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setStatus(status);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(LiveListViewModel.class);
        viewModel.getAllObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<LiveRoom>>() {
                @Override
                public void onSuccess(List<LiveRoom> data) {
                    adapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    hideLoadingView(false);
                }
            });
        });
        viewModel.getLivingRoomsObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<LiveRoom>>() {
                @Override
                public void onSuccess(List<LiveRoom> data) {
                    adapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    hideLoadingView(false);
                }
            });
        });
    }

    private void initListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                showLiveList(false);
            }
        });
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == RecyclerView.SCROLL_STATE_IDLE
//                        && hasMoreData
//                        && !isLoading
//                        && layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount() -1){
//                    showLiveList(true);
//                }
//            }
//        });
        adapter.setOnItemClickListener(this);

        LiveDataBus.get().with(DemoConstants.FRESH_LIVE_LIST, Boolean.class)
                .observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(aBoolean != null && aBoolean) {
                            showLiveList(false);
                        }
                    }
                });
    }

    private void initData() {
        swipeRefreshLayout.setRefreshing(true);
        showLiveList(false);
    }

    /**
     * 加载数据
     * @param isLoadMore
     */
    protected void showLiveList(final boolean isLoadMore){
        viewModel.getLiveRoomList(pageSize);
    }

    private void hideLoadingView(boolean isLoadMore){
        isLoading = false;
        if(!isLoadMore)
            swipeRefreshLayout.setRefreshing(false);
        else
            loadmorePB.setVisibility(View.INVISIBLE);
    }

    private boolean isOngoingLive() {
        return !TextUtils.isEmpty(status) && TextUtils.equals(status, DemoConstants.LIVE_ONGOING);
    }

    @Override
    public void onItemClick(View view, int position) {
        LiveRoom liveRoom = adapter.getItem(position);
        String status = liveRoom.getStatus();
        boolean living = DemoHelper.isLiving(status);
        if(living) {
            if(TextUtils.equals(liveRoom.getOwner(), EMClient.getInstance().getCurrentUser())) {
                LiveAnchorActivity.actionStart(mContext, liveRoom);
            }else {
                showDialog();
            }

        }else {
            LiveAnchorActivity.actionStart(mContext, liveRoom);
        }
    }

    private void showDialog() {
        Toast.makeText(mContext, R.string.em_live_list_warning, Toast.LENGTH_SHORT).show();
    }
}
