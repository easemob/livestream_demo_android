package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.data.model.User;
import com.easemob.livedemo.ui.live.adapter.LiveGiftStatisticsAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LiveGiftStatisticsDialog extends BaseLiveDialogFragment {
    private RecyclerView rvList;
    private TextView tvGiftNum;
    private TextView tvSenderNum;
    private LiveGiftStatisticsAdapter adapter;

    public static LiveGiftStatisticsDialog getNewInstance() {
        LiveGiftStatisticsDialog dialog = new LiveGiftStatisticsDialog();
        return dialog;
    }

    @Override
    public int getLayoutId() {
        return R.layout.em_fragment_dialog_live_gift_statistics;
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
    public void initData() {
        super.initData();
        getData();
    }

    private void getData() {
        List<GiftBean> list = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            GiftBean bean = new GiftBean();
            User user = new User();
            user.setNickname("测试"+(i+1));
            bean.setUser(user);
            bean.setGift("鲜花");
            bean.setNum((int) (Math.random() * 50));
            list.add(bean);
        }
        adapter.setData(list);
    }
}
