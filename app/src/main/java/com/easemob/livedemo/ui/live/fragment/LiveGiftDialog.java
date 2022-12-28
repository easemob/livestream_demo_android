package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.easemob.livedemo.ui.live.adapter.GiftFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.inf.OnConfirmClickListener;
import com.easemob.livedemo.ui.base.BaseLiveDialogFragment;

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
        return R.layout.fragment_dialog_live_gift;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        vpList = findViewById(R.id.vp_list);
        tabLayout = findViewById(R.id.tab_layout);

        vpList.setUserInputEnabled(false);
        adapter = new GiftFragmentAdapter(mContext);
        vpList.setAdapter(adapter);
        vpList.setOffscreenPageLimit(1);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, vpList, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(getString(R.string.live_gift_name));
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
                if (listener != null) {
                    listener.onConfirmClick(null, bean);
                }
            }
        });
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.listener = listener;
    }
}
