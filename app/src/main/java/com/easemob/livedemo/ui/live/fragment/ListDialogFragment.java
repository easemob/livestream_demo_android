package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.interfaces.OnItemClickListener;

import java.util.Arrays;
import java.util.List;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.base.BaseActivity;
import com.easemob.livedemo.ui.base.BaseDialogFragment;

public class ListDialogFragment extends BaseDialogFragment {
    private View layout;
    private RecyclerView rvDialogList;
    private Button btnCancel;
    private View dividerView;
    private EaseBaseRecyclerViewAdapter adapter;

    private String title;
    private String cancel;
    private int cancelColor;
    private OnDialogItemClickListener itemClickListener;
    private List<String> data;
    private OnDialogCancelClickListener cancelClickListener;
    private int animations;
    private int gravity;
    private int dividerViewBgResId;
    private int layoutBgResId;
    private int titleColorRes;
    private int contentColorRes;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_dialog_list;
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogParams();
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if (animations != 0) {
            try {
                getDialog().getWindow().setWindowAnimations(animations);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        layout = findViewById(R.id.layout);
        TextView tvTitle = findViewById(R.id.tv_title);
        rvDialogList = findViewById(R.id.rv_dialog_list);
        btnCancel = findViewById(R.id.btn_cancel);
        dividerView = findViewById(R.id.view_divider);

        if (0 != layoutBgResId) {
            layout.setBackgroundResource(layoutBgResId);
        }

        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
            dividerView.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            dividerView.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
            if (0 != titleColorRes) {
                tvTitle.setTextColor(mContext.getResources().getColor(titleColorRes));
            }
        }

        if (dividerViewBgResId != 0) {
            dividerView.setBackgroundResource(dividerViewBgResId);
        }

        if (gravity != -1) {
            tvTitle.setGravity(gravity | Gravity.CENTER_VERTICAL);
        }

        if (TextUtils.isEmpty(cancel)) {
            btnCancel.setText(getString(R.string.cancel));
        } else {
            btnCancel.setText(cancel);
        }

        if (cancelColor != 0) {
            btnCancel.setTextColor(cancelColor);
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (cancelClickListener != null) {
                    cancelClickListener.OnCancel(v);
                }
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        rvDialogList.setLayoutManager(new LinearLayoutManager(mContext));
        if (adapter == null) {
            adapter = getDefaultAdapter();
        }
        rvDialogList.setAdapter(adapter);

        //rvDialogList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        adapter.setData(data);

        this.adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dismiss();
                if (itemClickListener != null) {
                    itemClickListener.OnItemClick(view, position);
                }
            }
        });
    }

    private void setCancelColor(int cancelColor) {
        this.cancelColor = cancelColor;
    }

    private void setWindowAnimations(int animations) {
        this.animations = animations;
    }

    private void setData(List<String> data) {
        this.data = data;
    }

    private void setOnCancelClickListener(OnDialogCancelClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
    }

    private void setCancel(String cancel) {
        this.cancel = cancel;
    }

    private void setOnItemClickListener(OnDialogItemClickListener clickListener) {
        this.itemClickListener = clickListener;
    }

    private void setAdapter(EaseBaseRecyclerViewAdapter adapter) {
        this.adapter = adapter;
    }


    private EaseBaseRecyclerViewAdapter getDefaultAdapter() {
        DefaultAdapter defaultAdapter = new DefaultAdapter();
        defaultAdapter.setGravity(gravity);
        defaultAdapter.setContentTextColorRes(contentColorRes);
        return defaultAdapter;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setGravity(int gravity) {
        this.gravity = gravity;
    }

    private void setDividerViewBg(int resId) {
        this.dividerViewBgResId = resId;
    }

    private void setLayoutBg(int resId) {
        this.layoutBgResId = resId;
    }

    public void setTitleColorRes(int titleColorRes) {
        this.titleColorRes = titleColorRes;
    }

    public void setContentColorRes(int contentColorRes) {
        this.contentColorRes = contentColorRes;
    }

    public interface OnDialogItemClickListener {
        void OnItemClick(View view, int position);
    }

    public interface OnDialogCancelClickListener {
        void OnCancel(View view);
    }


    private static class DefaultAdapter extends EaseBaseRecyclerViewAdapter<String> {
        private int gravity;
        private int contentTextColorRes;

        public DefaultAdapter() {
            gravity = -1;
            contentTextColorRes = 0;
        }

        public void setGravity(int gravity) {
            this.gravity = gravity;
        }

        public void setContentTextColorRes(int contentTextColorRes) {
            this.contentTextColorRes = contentTextColorRes;
        }

        @Override
        public MyViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_default_list_item, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            viewHolder.setGravity(gravity);
            viewHolder.setContentTextColorRes(contentTextColorRes);
            return viewHolder;
        }

        private static class MyViewHolder extends ViewHolder<String> {
            private TextView content;
            private int gravity;
            private int contentTextColorRes;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                gravity = -1;
                contentTextColorRes = 0;
            }

            public void setGravity(int gravity) {
                this.gravity = gravity;
            }

            public void setContentTextColorRes(int contentTextColorRes) {
                this.contentTextColorRes = contentTextColorRes;
            }

            @Override
            public void initView(View itemView) {
                content = findViewById(R.id.tv_title);
            }

            @Override
            public void setData(String item, int position) {
                if (-1 != gravity) {
                    content.setGravity(gravity | Gravity.CENTER_VERTICAL);
                }
                if (0 != contentTextColorRes) {
                    content.setTextColor(contentTextColorRes);
                }
                content.setText(item);
            }
        }
    }


    public static class Builder {
        private BaseActivity context;
        private String title;
        private EaseBaseRecyclerViewAdapter adapter;
        private List<String> data;
        private OnDialogItemClickListener clickListener;
        private String cancel;
        private int cancelColor;
        private OnDialogCancelClickListener cancelClickListener;
        private Bundle bundle;
        private int animations;
        private int gravity;
        private int dividerViewBgResId;
        private int layoutBgResId;
        private int titleColorRes;
        private int contentColorRes;

        public Builder(BaseActivity context) {
            this.context = context;
        }

        public Builder setTitle(@StringRes int title) {
            this.title = context.getString(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setAdapter(EaseBaseRecyclerViewAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder setData(List<String> data) {
            this.data = data;
            return this;
        }

        public Builder setDividerViewBgResId(int dividerViewBgResId) {
            this.dividerViewBgResId = dividerViewBgResId;
            return this;
        }

        public Builder setLayoutBgResId(int layoutBgResId) {
            this.layoutBgResId = layoutBgResId;
            return this;
        }

        public Builder setTitleColorRes(int titleColorRes) {
            this.titleColorRes = titleColorRes;
            return this;
        }

        public Builder setContentColorRes(int contentColorRes) {
            this.contentColorRes = contentColorRes;
            return this;
        }

        public Builder setData(String[] data) {
            this.data = Arrays.asList(data);
            return this;
        }

        public Builder setOnItemClickListener(OnDialogItemClickListener listener) {
            this.clickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(@StringRes int cancel, OnDialogCancelClickListener listener) {
            this.cancel = context.getString(cancel);
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(String cancel, OnDialogCancelClickListener listener) {
            this.cancel = cancel;
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setCancelColorRes(@ColorRes int color) {
            this.cancelColor = ContextCompat.getColor(context, color);
            return this;
        }

        public Builder setCancelColor(@ColorInt int color) {
            this.cancelColor = color;
            return this;
        }

        public Builder setArgument(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder setWindowAnimations(@StyleRes int animations) {
            this.animations = animations;
            return this;
        }

        public ListDialogFragment build() {
            ListDialogFragment fragment = new ListDialogFragment();
            fragment.setTitle(title);
            fragment.setGravity(gravity);
            fragment.setAdapter(adapter);
            fragment.setData(data);
            fragment.setOnItemClickListener(this.clickListener);
            fragment.setCancel(cancel);
            fragment.setCancelColor(cancelColor);
            fragment.setOnCancelClickListener(this.cancelClickListener);
            fragment.setArguments(this.bundle);
            fragment.setWindowAnimations(animations);
            fragment.setDividerViewBg(dividerViewBgResId);
            fragment.setLayoutBg(layoutBgResId);
            fragment.setTitleColorRes(titleColorRes);
            fragment.setContentColorRes(contentColorRes);
            return fragment;
        }

        public ListDialogFragment show() {
            ListDialogFragment fragment = build();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragment.show(transaction, null);
            return fragment;
        }

    }


}
