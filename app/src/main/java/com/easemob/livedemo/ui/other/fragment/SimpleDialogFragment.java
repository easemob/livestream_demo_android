package com.easemob.livedemo.ui.other.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.easemob.livedemo.common.inf.OnCancelClickListener;
import com.easemob.livedemo.common.inf.OnConfirmClickListener;
import com.easemob.livedemo.ui.base.BaseActivity;
import com.easemob.livedemo.ui.base.DemoDialogFragment;

public class SimpleDialogFragment extends DemoDialogFragment {
    public static final String MESSAGE_KEY = "message";
    private String message;
    private OnConfirmClickListener mOnConfirmClickListener;
    private OnCancelClickListener mOnCancelClickListener;

    public static void showDialog(BaseActivity context, String message, OnConfirmClickListener listener) {
        SimpleDialogFragment fragment = new SimpleDialogFragment();
        fragment.setOnConfirmClickListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_KEY, message);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragment.show(transaction, null);
    }

    public static void showDialog(BaseActivity context, @StringRes int message, OnConfirmClickListener listener) {
        SimpleDialogFragment fragment = new SimpleDialogFragment();
        fragment.setOnConfirmClickListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_KEY, context.getResources().getString(message));
        fragment.setArguments(bundle);
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragment.show(transaction, null);
    }

    @Override
    public void initArgument() {
        if (getArguments() != null) {
            message = getArguments().getString(MESSAGE_KEY);
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if (!TextUtils.isEmpty(message)) {
            mTvDialogTitle.setText(message);
        }
        if (title != 0) {
            mTvDialogTitle.setText(title);
        } else if (!TextUtils.isEmpty(titleStr)) {
            mTvDialogTitle.setText(titleStr);
        }
        if (confirmTitle != 0) {
            mBtnDialogConfirm.setText(confirmTitle);
        }
        if (confirmColor != 0) {
            mBtnDialogConfirm.setTextColor(ContextCompat.getColor(mContext, confirmColor));
        }
    }

    @Override
    public void initData() {
        super.initData();
        try {
            getDialog().setCanceledOnTouchOutside(canceledOnTouchOutside);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfirmClick(View v) {
        super.onConfirmClick(v);
        dismiss();
        if (mOnConfirmClickListener != null) {
            mOnConfirmClickListener.onConfirmClick(v, null);
        }
    }

    @Override
    public void onCancelClick(View v) {
        super.onCancelClick(v);
        if (mOnCancelClickListener != null) {
            mOnCancelClickListener.onCancelClick(v, null);
        }
    }

    /**
     * 设置确定按钮的点击事件
     *
     * @param listener
     */
    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.mOnConfirmClickListener = listener;
    }

    /**
     * 设置取消按钮的点击事件
     *
     * @param listener
     */
    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.mOnCancelClickListener = listener;
    }

    public static class Builder {
        private Context context;
        private int title;
        private String titleStr;
        private int confirm;
        private int confirmColor;
        private OnConfirmClickListener mOnConfirmClickListener;
        private OnCancelClickListener mOnCancelClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(@StringRes int title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(String title) {
            this.titleStr = title;
            return this;
        }

        public Builder setConfirmButtonTxt(@StringRes int confirm) {
            this.confirm = confirm;
            return this;
        }

        public Builder setConfirmColor(@ColorRes int color) {
            this.confirmColor = color;
            return this;
        }

        public Builder setOnConfirmClickListener(OnConfirmClickListener listener) {
            this.mOnConfirmClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(OnCancelClickListener listener) {
            this.mOnCancelClickListener = listener;
            return this;
        }

        public SimpleDialogFragment build() {
            SimpleDialogFragment dialog = new SimpleDialogFragment();
            if (title != 0) {
                dialog.setTitle(title);
            } else if (!TextUtils.isEmpty(titleStr)) {
                dialog.setTitle(titleStr);
            }

            if (confirm != 0) {
                dialog.setConfirmTitle(confirm);
            }
            if (confirmColor != 0) {
                dialog.setConfirmColor(confirmColor);
            }
            if (mOnConfirmClickListener != null) {
                dialog.setOnConfirmClickListener(mOnConfirmClickListener);
            }
            if (mOnCancelClickListener != null) {
                dialog.setOnCancelClickListener(mOnCancelClickListener);
            }
            return dialog;
        }
    }

}
