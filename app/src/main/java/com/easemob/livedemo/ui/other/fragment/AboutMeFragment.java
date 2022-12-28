package com.easemob.livedemo.ui.other.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.easemob.livedemo.ui.LoginActivity;
import com.easemob.livedemo.ui.other.AboutActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.util.EMLog;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.livedata.LiveDataBus;
import com.easemob.livedemo.common.utils.DemoHelper;
import com.easemob.livedemo.ui.base.BaseLiveFragment;
import com.easemob.livedemo.ui.widget.ArrowItemView;
import com.easemob.livedemo.utils.Utils;

public class AboutMeFragment extends BaseLiveFragment {
    private EaseImageView userIcon;
    private TextView username;
    private ArrowItemView itemAbout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about_me;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        userIcon = findViewById(R.id.user_icon);
        username = findViewById(R.id.username);
        itemAbout = findViewById(R.id.item_about);

        EaseUserUtils.setUserAvatar(mContext, DemoHelper.getAgoraId(), userIcon);

        EaseUserUtils.setUserNick(DemoHelper.getAgoraId(), username);

        itemAbout.setContent("V" + Utils.getAppVersionName(mContext));
    }

    @Override
    protected void initListener() {
        super.initListener();
        itemAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AboutActivity.class);
                startActivity(intent);
            }
        });
        itemAbout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showExitDialog();
                return true;
            }
        });
    }

    private void showExitDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.about_logout_title)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void logout() {
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent);
                mContext.finish();
            }

            @Override
            public void onError(int code, String error) {

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        LiveDataBus.get().with(DemoConstants.NICKNAME_CHANGE, String.class)
                .observe(getViewLifecycleOwner(), response -> {
                    if (!TextUtils.isEmpty(response)) {
                        EaseUserUtils.setUserNick(response, username);
                    }
                });
        LiveDataBus.get().with(DemoConstants.AVATAR_CHANGE, Boolean.class)
                .observe(getViewLifecycleOwner(), response -> {
                    EMLog.i("lives","AVATAR_CHANGE");
                    EaseUserUtils.setUserAvatar(mContext, DemoHelper.getAgoraId(), userIcon);
                });
    }
}
