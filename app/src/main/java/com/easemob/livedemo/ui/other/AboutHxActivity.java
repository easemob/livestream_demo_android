package com.easemob.livedemo.ui.other;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.base.BaseLiveActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class AboutHxActivity extends BaseLiveActivity implements EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private WebView webView;
    private ProgressBar pbLoad;

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
        pbLoad = findViewById(R.id.pb_load);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        webView.loadUrl("http://www.easemob.com/about");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100) {
                    pbLoad.setVisibility(View.GONE);
                }else {
                    pbLoad.setVisibility(View.VISIBLE);
                    pbLoad.setProgress(newProgress);
                }
            }
        });
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        settings.setLoadsImagesAutomatically(true);
    }

    @Override
    public void onBackPress(View view) {
        if(webView.canGoBack()) {
            webView.goBack();
        }else {
            onBackPressed();
        }
    }
}
