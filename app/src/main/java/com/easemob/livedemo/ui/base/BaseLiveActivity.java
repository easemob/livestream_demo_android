package com.easemob.livedemo.ui.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.easemob.livedemo.R;


public abstract class BaseLiveActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
        int layoutId = getLayoutId();
        if (layoutId != 0) {
            setContentView(layoutId);
        } else {
            setContentView(getContentView());
        }
        initSystemFit();
        initIntent(getIntent());
        initView();
        initListener();
        initData();
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View getContentView() {
        return null;
    }

    protected void initSystemFit() {
        setFitSystemForTheme(true);
    }

    protected void initIntent(Intent intent) {
    }

    protected void initView() {
    }

    protected void initListener() {
    }

    protected void initData() {
    }
}
