package com.easemob.livedemo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DialogSet;
import com.easemob.livedemo.ui.activity.BaseDialogFragment;

import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.Group;

public class DemoDialogFragment extends BaseDialogFragment implements View.OnClickListener, DialogSet {
    public TextView mTvDialogTitle;
    public Button mBtnDialogCancel;
    public Button mBtnDialogConfirm;
    public Group mGroupMiddle;
    public int title;
    public int confirmTitle;
    public boolean canceledOnTouchOutside;
    public int confirmColor;

    @Override
    public int getLayoutId() {
        return R.layout.em_fragment_dialog_base_view;
    }

    @Override
    public void setChildView(View view) {
        super.setChildView(view);
        int layoutId = getMiddleLayoutId();
        if(layoutId > 0) {
            RelativeLayout middleParent = view.findViewById(R.id.rl_dialog_middle);
            if(middleParent != null) {
                View child = LayoutInflater.from(mContext).inflate(layoutId, middleParent, false);
                //同时使middleParent可见
                view.findViewById(R.id.group_middle).setVisibility(View.VISIBLE);
                initChildView(middleParent, child);
            }
        }
    }

    protected void initChildView(RelativeLayout middleParent, View child) {

    }

    /**
     * 获取中间布局的id
     * @return
     */
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

    public void initData() {}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_cancel :
                onCancelClick(v);
                break;
            case R.id.btn_dialog_confirm:
                onConfirmClick(v);
                break;
        }
    }

    /**
     * 点击了取消按钮
     * @param v
     */
    public void onCancelClick(View v) {
        dismiss();
    }

    /**
     * 点击了确认按钮
     * @param v
     */
    public void onConfirmClick(View v) {

    }

    @Override
    public void setTitle(@StringRes int title) {
        this.title = title;
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
