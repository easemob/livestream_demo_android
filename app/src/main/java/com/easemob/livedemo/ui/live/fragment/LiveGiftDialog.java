package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;

import com.easemob.livedemo.R;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class LiveGiftDialog extends BaseLiveDialogFragment {
    private ViewPager2 vpList;
    private TabLayout tabLayout;

    @Override
    public int getLayoutId() {
        return R.layout.em_fragment_dialog_live_gift;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        vpList = findViewById(R.id.vp_list);
        tabLayout = findViewById(R.id.tab_layout);


    }

    @Override
    public void initData() {
        super.initData();
    }

    private class GiftAdapter extends FragmentStateAdapter {

        public GiftAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return null;
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
