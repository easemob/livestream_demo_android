package com.easemob.livedemo.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.Group;

import com.easemob.livedemo.common.inf.DialogSet;

import com.easemob.livedemo.R;

public class DemoDialogFragment extends BaseDialogFragment implements View.OnClickListener, DialogSet {
    public TextView mTvDialogTitle;
    public Button mBtnDialogCancel;
    public Button mBtnDialogConfirm;
    public Group mGroupMiddle;
    public int title;
    public String titleStr;
    public int confirmTitle;
    public boolean canceledOnTouchOutside;
    public int confirmColor;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_dialog_base_view;
    }

    @Override
    public void setChildView(View view) {
        super.setChildView(view);
        int layoutId = getMiddleLayoutId();
        if (layoutId > 0) {
            RelativeLayout middleParent = view.findViewById(R.id.rl_dialog_middle);
            if (middleParent != null) {
                View child = LayoutInflater.from(mContext).inflate(layoutId, middleParent, false);
                view.findViewById(R.id.group_middle).setVisibility(View.VISIBLE);
                initChildView(middleParent, child);
            }
        }
    }

    protected void initChildView(RelativeLayout middleParent, View child) {

    }

    public int getMiddleLayoutId() {
        return 0;
    }

    public void initView(Bundle savedInstanceState) {
        mTvDialogTitle = findViewById(R.id.tv_dialog_title);
        mBtnDialogCancel = findViewById(R.id.btn_dialog_cancel);
        mBtnDialogConfirm = findViewById(R.id.btn_dialog_confirm);
        mGroupMiddle = findViewById(R.id.group_middle);
    }

    public void initListener() {
        mBtnDialogCancel.setOnClickListener(this);
        mBtnDialogConfirm.setOnClickListener(this);
    }

    public void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_cancel:
                onCancelClick(v);
                break;
            case R.id.btn_dialog_confirm:
                onConfirmClick(v);
                break;
        }
    }

    public void onCancelClick(View v) {
        dismiss();
    }

    public void onConfirmClick(View v) {

    }

    @Override
    public void setTitle(@StringRes int title) {
        this.title = title;
    }

    @Override
    public void setTitle(String title) {
        this.titleStr = title;
    }

    @Override
    public void setConfirmTitle(@StringRes int confirmTitle) {
        this.confirmTitle = confirmTitle;
    }

    @Override
    public void setConfirmColor(int color) {
        this.confirmColor = color;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

}
