package com.easemob.livedemo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.easemob.livedemo.R;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class LiveAllActivity extends BaseLiveActivity implements EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LiveAllActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_live_all;
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar = findViewById(R.id.title_bar);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        LiveListFragment fragment = new LiveListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("status", "all");
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment).commit();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
