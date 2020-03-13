package com.easemob.livedemo.ui.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

public abstract class BaseLiveActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initSystemFit();
        initIntent(getIntent());
        initView();
        initListener();
        initData();
    }

    protected abstract int getLayoutId();

    protected void initSystemFit() {
        setFitSystemForTheme(true);
    }

    protected void initIntent(Intent intent) {}

    protected void initView() {}

    protected void initListener() {}

    protected void initData() {}

}
