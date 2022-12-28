package com.easemob.livedemo.ui.other;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.easemob.livedemo.R;
import com.easemob.livedemo.databinding.ActivityAboutBinding;
import com.easemob.livedemo.ui.base.BaseLiveActivity;
import com.easemob.livedemo.utils.Utils;
import com.hyphenate.chat.EMClient;

public class AboutActivity extends BaseLiveActivity implements View.OnClickListener {

    private ActivityAboutBinding mBinding;

    @Override
    protected View getContentView() {
        mBinding = ActivityAboutBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void initView() {
        super.initView();
        mBinding.titlebarTitle.setTypeface(Utils.getRobotoBlackTypeface(this.getApplicationContext()));
    }


    @Override
    protected void initListener() {
        super.initListener();
        mBinding.itemPolicy.setOnClickListener(this);
        mBinding.itemMore.setOnClickListener(this);

        mBinding.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBinding.titlebarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mBinding.itemSdkVersion.setContent("V" + EMClient.VERSION);
        mBinding.itemLibVersion.setContent("V" + Utils.getAppVersionName(mContext));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_policy:
                startToWeb("https://www.easemob.com/protocol");
                break;
            case R.id.item_more:
                startToWeb("https://www.easemob.com/");
                break;
        }
    }

    private void startToWeb(String url) {
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }
}