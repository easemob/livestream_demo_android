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
import com.easemob.livedemo.data.restapi.model.ResponseModule;
import com.easemob.livedemo.ui.base.GridMarginDecoration;
import com.easemob.livedemo.ui.live.adapter.LiveListAdapter;
import com.easemob.livedemo.ui.base.BaseFragment;
import com.easemob.livedemo.ui.live.viewmodels.LiveListViewModel;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveListFragment extends BaseFragment implements OnItemClickListener {
    protected SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar loadmorePB;

    protected static final int pageSize = 10;
    protected String cursor;
    protected boolean hasMoreData;
    private boolean isLoading;
    protected boolean isLoadMore;
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

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
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
        recyclerView.addItemDecoration(new GridMarginDecoration(mContext,10));
        adapter = new LiveListAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setStatus(status);
    }

    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(LiveListViewModel.class);
        viewModel.getAllObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<ResponseModule<List<LiveRoom>>>() {
                @Override
                public void onSuccess(ResponseModule<List<LiveRoom>> data) {
                    cursor = data.cursor;
                    hasMoreData = true;
                    if(data.data.size() < pageSize) {
                        hasMoreData = false;
                    }
                    if(isLoadMore) {
                        adapter.addData(data.data);
                    }else {
                        adapter.setData(data.data);
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

    private void initListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                refreshList();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE
                        && hasMoreData
                        && !isLoading
                        && layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount() -1){
                    showLiveList(true);
                }
            }
        });
        adapter.setOnItemClickListener(this);

        LiveDataBus.get().with(DemoConstants.FRESH_LIVE_LIST, Boolean.class)
                .observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(aBoolean != null && aBoolean) {
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
        swipeRefreshLayout.setRefreshing(true);
        showLiveList(false);
    }

    /**
     * 加载数据
     * @param isLoadMore
     */
    protected void showLiveList(final boolean isLoadMore){
        this.isLoadMore = isLoadMore;
        if(!isLoadMore) {
            cursor = null;
        }
        loadLiveList(pageSize, cursor);
    }

    /**
     * 加载数据
     * @param limit
     * @param cursor
     */
    protected void loadLiveList(int limit, String cursor) {
        viewModel.getLiveRoomList(limit, cursor);
    }

    protected void hideLoadingView(boolean isLoadMore){
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
                // LiveAnchorActivity.actionStart(mContext, liveRoom);
            }else {
                showDialog();
            }
        }else {
            // LiveAnchorActivity.actionStart(mContext, liveRoom);
        }
    }

    private void showDialog() {
        Toast.makeText(mContext, R.string.em_live_list_warning, Toast.LENGTH_SHORT).show();
    }
}
