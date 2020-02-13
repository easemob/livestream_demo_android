package com.easemob.livedemo.ui.live.adapter;

import com.easemob.livedemo.ui.live.fragment.LiveGiftListFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GiftFragmentAdapter extends FragmentStateAdapter {

    public GiftFragmentAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        LiveGiftListFragment fragment = new LiveGiftListFragment();
        //添加参数
        return fragment;
    }

    /**
     * 根据礼物类型，此处仅为测试
     * @return
     */
    @Override
    public int getItemCount() {
        return 1;
    }
}
