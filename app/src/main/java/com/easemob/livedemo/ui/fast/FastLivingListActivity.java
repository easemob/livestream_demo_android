package com.easemob.livedemo.ui.fast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.base.BaseLiveActivity;
import com.easemob.livedemo.ui.fast.fragment.FastLivingListFragment;
import com.easemob.livedemo.ui.other.CreateLiveRoomActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class FastLivingListActivity extends BaseLiveActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener {
    public static final String EXTRA_IS_FAST = "isFast";
    private EaseTitleBar titleBar;

    public static void actionStart(Context context, boolean isFast) {
        Intent intent = new Intent(context, FastLivingListActivity.class);
        intent.putExtra(EXTRA_IS_FAST, isFast);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_living;
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar = findViewById(R.id.title_bar);
        boolean isFast = getIntent().getBooleanExtra(EXTRA_IS_FAST, true);
        if (isFast) {
            titleBar.setTitle(getString(R.string.live_type_fast_title));
        } else {
            titleBar.setTitle(getString(R.string.live_type_interaction_title));
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        boolean isFast = getIntent().getBooleanExtra(EXTRA_IS_FAST, true);
        FastLivingListFragment fragment = new FastLivingListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("status", "ongoing");
        bundle.putBoolean(EXTRA_IS_FAST, isFast);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment).commit();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onRightClick(View view) {
        CreateLiveRoomActivity.actionStart(mContext);
    }
}

