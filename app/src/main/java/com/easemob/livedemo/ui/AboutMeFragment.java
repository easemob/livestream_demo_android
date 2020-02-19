package com.easemob.livedemo.ui;

import android.os.Bundle;
import android.view.View;

import com.easemob.livedemo.BuildConfig;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.activity.AboutHxActivity;
import com.easemob.livedemo.ui.activity.BaseLiveFragment;
import com.easemob.livedemo.ui.widget.ArrowItemView;

public class AboutMeFragment extends BaseLiveFragment implements View.OnClickListener {
    private ArrowItemView itemVersion;
    private ArrowItemView itemView;
    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_about_me;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        itemVersion = findViewById(R.id.item_version);
        itemView = findViewById(R.id.item_about);

        itemVersion.getTvContent().setText(BuildConfig.VERSION_NAME);
    }

    @Override
    protected void initListener() {
        super.initListener();
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_about :
                AboutHxActivity.actionStart(mContext);
                break;
        }
    }
}
