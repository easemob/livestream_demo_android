package com.easemob.livedemo.ui.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;


import com.easemob.livedemo.R;
import com.easemob.livedemo.common.inf.OnCancelClickListener;
import com.easemob.livedemo.common.inf.OnConfirmClickListener;

import java.lang.reflect.Field;

public class DemoDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    public TextView mTvDialogTitle;
    public Button mBtnDialogCancel;
    public Button mBtnDialogConfirm;
    public Group mGroupMiddle;
    public int title;
    private OnConfirmClickListener mOnConfirmClickListener;
    private OnCancelClickListener mOnCancelClickListener;

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
        Bundle bundle = getArguments();
        if(bundle != null) {
            String title = bundle.getString("title");
            String confirm_text = bundle.getString("confirm_text");
            int confirm_color = bundle.getInt("confirm_color", -1);
            boolean cancel_dismiss = bundle.getBoolean("cancel_dismiss", false);
            boolean canceledOnTouchOutside = bundle.getBoolean("canceledOnTouchOutside", false);

            if(!TextUtils.isEmpty(title)) {
                mTvDialogTitle.setText(title);
            }

            if(!TextUtils.isEmpty(confirm_text)) {
                mBtnDialogConfirm.setText(confirm_text);
            }
            if(confirm_color != -1) {
                mBtnDialogConfirm.setTextColor(confirm_color);
            }

            if(cancel_dismiss) {
                mGroupMiddle.setVisibility(View.GONE);
            }

            if(getDialog() != null) {
                getDialog().setCanceledOnTouchOutside(canceledOnTouchOutside);
            }
        }
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
        if (mOnCancelClickListener != null) {
            mOnCancelClickListener.onCancelClick(v, null);
        }
    }

    public void onConfirmClick(View v) {
        dismiss();
        if (mOnConfirmClickListener != null) {
            mOnConfirmClickListener.onConfirmClick(v, null);
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

    public int showAllowingStateLoss(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        try {
            Field dismissed = DemoDialogFragment.class.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field shown = DemoDialogFragment.class.getDeclaredField("mShownByMe");
            shown.setAccessible(true);
            shown.set(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        transaction.add(this, tag);
        try {
            Field viewDestroyed = DemoDialogFragment.class.getDeclaredField("mViewDestroyed");
            viewDestroyed.setAccessible(true);
            viewDestroyed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        int mBackStackId = transaction.commitAllowingStateLoss();
        try {
            Field backStackId = DemoDialogFragment.class.getDeclaredField("mBackStackId");
            backStackId.setAccessible(true);
            backStackId.set(this, mBackStackId);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mBackStackId;
    }

    public static class Builder {
        private final AppCompatActivity context;
        private OnConfirmClickListener mOnConfirmClickListener;
        private OnCancelClickListener mOnCancelClickListener;
        private final Bundle bundle;
        private DemoDialogFragment customFragment;

        public Builder(AppCompatActivity context) {
            this.context = context;
            bundle = new Bundle();
        }

        public Builder setTitle(@StringRes int title) {
            bundle.putString("title", context.getString(title));
            return this;
        }

        public Builder setTitle(String title) {
            bundle.putString("title", title);
            return this;
        }

        public Builder setConfirmButtonTxt(@StringRes int confirm) {
            bundle.putString("confirm_text", context.getString(confirm));
            return this;
        }

        public Builder setConfirmButtonTxt(String confirm) {
            bundle.putString("confirm_text", confirm);
            return this;
        }

        public Builder setConfirmColor(@ColorRes int color) {
            bundle.putInt("confirm_color", ContextCompat.getColor(context, color));
            return this;
        }

        public Builder setConfirmColorInt(@ColorInt int color) {
            bundle.putInt("confirm_color", color);
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

        public Builder dismissCancel(boolean dismiss) {
            bundle.putBoolean("cancel_dismiss", dismiss);
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            bundle.putBoolean("canceledOnTouchOutside", canceledOnTouchOutside);
            return this;
        }

        public <T extends DemoDialogFragment> Builder setCustomFragment(T fragment) {
            this.customFragment = fragment;
            return this;
        }

        public DemoDialogFragment build() {
            DemoDialogFragment dialog = this.customFragment != null ? this.customFragment : new DemoDialogFragment();
            dialog.setArguments(bundle);
            if (mOnConfirmClickListener != null) {
                dialog.setOnConfirmClickListener(mOnConfirmClickListener);
            }
            if (mOnCancelClickListener != null) {
                dialog.setOnCancelClickListener(mOnCancelClickListener);
            }
            return dialog;
        }

        public void show() {
            DemoDialogFragment fragment = build();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragment.showAllowingStateLoss(transaction, null);
        }
    }

}
