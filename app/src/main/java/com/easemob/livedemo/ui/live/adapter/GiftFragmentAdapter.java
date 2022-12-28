package com.easemob.livedemo.ui.live.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.easemob.livedemo.common.inf.OnConfirmClickListener;
import com.easemob.livedemo.ui.live.fragment.LiveGiftListFragment;

public class GiftFragmentAdapter extends FragmentStateAdapter {
    private OnVpFragmentItemListener listener;

    public GiftFragmentAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        LiveGiftListFragment fragment = new LiveGiftListFragment();
        fragment.setOnConfirmClickListener(new OnConfirmClickListener() {
            @Override
            public void onConfirmClick(View view, Object bean) {
                if (listener != null) {
                    listener.onVpFragmentItem(position, bean);
                }
            }
        });
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void setOnVpFragmentItemListener(OnVpFragmentItemListener listener) {
        this.listener = listener;
    }

    public interface OnVpFragmentItemListener {
        void onVpFragmentItem(int position, Object bean);
    }
}
