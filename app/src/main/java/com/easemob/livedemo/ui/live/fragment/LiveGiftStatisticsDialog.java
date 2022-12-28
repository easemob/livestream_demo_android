package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easemob.livedemo.ui.live.adapter.LiveGiftStatisticsAdapter;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.livedata.LiveDataBus;
import com.easemob.livedemo.ui.base.BaseLiveDialogFragment;
import com.easemob.livedemo.ui.live.viewmodels.GiftStatisticsViewModel;

public class LiveGiftStatisticsDialog extends BaseLiveDialogFragment {
    private RecyclerView rvList;
    private TextView tvGiftNum;
    private TextView tvSenderNum;
    private LiveGiftStatisticsAdapter adapter;
    private GiftStatisticsViewModel viewModel;

    public static LiveGiftStatisticsDialog getNewInstance() {
        LiveGiftStatisticsDialog dialog = new LiveGiftStatisticsDialog();
        return dialog;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_dialog_live_gift_statistics;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        rvList = findViewById(R.id.rv_list);
        tvGiftNum = findViewById(R.id.tv_gift_num);
        tvSenderNum = findViewById(R.id.tv_sender_num);

        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new LiveGiftStatisticsAdapter();
        rvList.setAdapter(adapter);
        rvList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    public void initViewModel() {
        super.initViewModel();
        viewModel = new ViewModelProvider(this).get(GiftStatisticsViewModel.class);
        viewModel.getGiftObservable().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                tvGiftNum.setText(getString(R.string.live_gift_total, response.size()));
                adapter.setData(response);
            }
        });
        viewModel.getSenderNumObservable().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                tvSenderNum.setText(getString(R.string.live_gift_send_total, response));
            }
        });
        LiveDataBus.get().with(DemoConstants.REFRESH_GIFT_LIST, Boolean.class)
                .observe(getViewLifecycleOwner(), response -> {
                    if (response != null && response) {
                        getData();
                    }
                });
    }

    @Override
    public void initData() {
        super.initData();
        getData();
    }

    private void getData() {
        viewModel.getGiftListFromDb();
        viewModel.getGiftSenderNumFromDb();
    }
}
