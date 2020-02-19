package com.easemob.livedemo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import com.easemob.livedemo.R;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class AboutHxActivity extends BaseLiveActivity implements EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private WebView webView;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, AboutHxActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_about_hx;
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar = findViewById(R.id.title_bar);
        webView = findViewById(R.id.webView);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
