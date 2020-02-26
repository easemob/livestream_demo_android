package com.easemob.livedemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.easemob.livedemo.BuildConfig;
import com.easemob.livedemo.DemoApplication;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnConfirmClickListener;
import com.easemob.livedemo.ui.activity.AboutHxActivity;
import com.easemob.livedemo.ui.activity.BaseLiveFragment;
import com.easemob.livedemo.ui.activity.LoginActivity;
import com.easemob.livedemo.ui.activity.SimpleDialogFragment;
import com.easemob.livedemo.ui.widget.ArrowItemView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.util.List;

public class AboutMeFragment extends BaseLiveFragment implements View.OnClickListener, View.OnLongClickListener {
    private ArrowItemView itemVersion;
    private ArrowItemView itemView;
    private ImageView ivLogo;
    private Button btnOut;

    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_about_me;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        itemVersion = findViewById(R.id.item_version);
        itemView = findViewById(R.id.item_about);
        ivLogo = findViewById(R.id.iv_logo);
        btnOut = findViewById(R.id.btn_out);

        itemVersion.getTvContent().setText(BuildConfig.VERSION_NAME);

        boolean canRegister = DemoHelper.isCanRegister();
        btnOut.setVisibility(canRegister ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initListener() {
        super.initListener();
        itemView.setOnClickListener(this);
        btnOut.setOnClickListener(this);
        ivLogo.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_about :
                AboutHxActivity.actionStart(mContext);
                break;
            case R.id.btn_out:
                showOutDialog();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.iv_logo :
                showSelectDialog();
                return true;
        }
        return false;
    }

    private void showSelectDialog() {
        boolean canRegister = DemoHelper.isCanRegister();
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(canRegister ? R.string.em_set_select_auto : R.string.em_set_select_login)
                .setConfirmButtonTxt(R.string.em_set_select_switch)
                .setOnConfirmClickListener(new OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, Object bean) {
                        DemoHelper.setCanRegister(!canRegister);
                        btnOut.setVisibility(!canRegister ? View.VISIBLE : View.GONE);
                    }
                })
                .build()
                .show(getChildFragmentManager(), "dialog");
    }

    private void showOutDialog() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.em_set_logout_confirm)
                .setOnConfirmClickListener(new OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, Object bean) {
                        logoutHx();
                    }
                })
                .build()
                .show(getChildFragmentManager(), "dialog");
    }

    private void logoutHx() {
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                skipToLogin();
            }

            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    private void skipToLogin() {
        DemoHelper.clearUserId();
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);

        List<Activity> activities = DemoApplication.getInstance().getActivityLifecycle().getActivityList();
        if(activities != null && !activities.isEmpty()) {
            for (Activity activity : activities) {
                if(!(activity instanceof LoginActivity)) {
                    activity.finish();
                }
            }
        }
    }
}
