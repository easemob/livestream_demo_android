package com.easemob.livedemo.ui.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.base.BaseLiveActivity;
import com.easemob.livedemo.ui.live.fragment.LivingListFragment;
import com.easemob.livedemo.ui.other.CreateLiveRoomActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class LivingListActivity extends BaseLiveActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener {
    private EaseTitleBar titleBar;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LivingListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_living;
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle(getString(R.string.em_live_type_normal_title));
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
        LivingListFragment fragment = new LivingListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("status", "ongoing");
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

