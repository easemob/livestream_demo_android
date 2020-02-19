package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.OnConfirmClickListener;
import com.easemob.livedemo.ui.live.adapter.GiftFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class LiveGiftDialog extends BaseLiveDialogFragment {
    private ViewPager2 vpList;
    private TabLayout tabLayout;
    private GiftFragmentAdapter adapter;
    private OnConfirmClickListener listener;

    public static LiveGiftDialog getNewInstance() {
        return new LiveGiftDialog();
    }

    @Override
    public int getLayoutId() {
        return R.layout.em_fragment_dialog_live_gift;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        vpList = findViewById(R.id.vp_list);
        tabLayout = findViewById(R.id.tab_layout);

        //禁止ViewPager2滑动翻页
        vpList.setUserInputEnabled(false);
        adapter = new GiftFragmentAdapter(mContext);
        vpList.setAdapter(adapter);
        //设置缓冲页数
        vpList.setOffscreenPageLimit(1);//根据礼物类型进行变化
        //关联TabLayout
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, vpList, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(getString(R.string.em_live_gift_name));
            }
        });
        mediator.attach();
    }

    @Override
    public void initListener() {
        super.initListener();
        adapter.setOnVpFragmentItemListener(new GiftFragmentAdapter.OnVpFragmentItemListener() {
            @Override
            public void onVpFragmentItem(int position, Object bean) {
                if(listener != null) {
                    listener.onConfirmClick(null, bean);
                }
            }
        });
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.listener = listener;
    }
}
