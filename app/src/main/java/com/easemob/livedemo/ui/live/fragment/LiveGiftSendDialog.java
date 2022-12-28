package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.inf.OnConfirmClickListener;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.ui.base.DemoDialogFragment;

public class LiveGiftSendDialog extends DemoDialogFragment {
    private ImageView ivGiftIcon;
    private TextView tvGiftInfo;
    private GiftBean gift;
    private OnConfirmClickListener listener;

    public static LiveGiftSendDialog getNewInstance(GiftBean bean) {
        LiveGiftSendDialog dialog = new LiveGiftSendDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gift", bean);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public int getMiddleLayoutId() {
        return R.layout.layout_give_gift_info;
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if (bundle != null) {
            gift = (GiftBean) bundle.getSerializable("gift");
        }
    }

    @Override
    public void setChildView(View view) {
        int layoutId = getMiddleLayoutId();
        if (layoutId > 0) {
            RelativeLayout middleParent = view.findViewById(R.id.rl_dialog_middle);
            if (middleParent != null) {
                View child = LayoutInflater.from(mContext).inflate(layoutId, middleParent, false);
                middleParent.removeAllViews();
                middleParent.addView(child);
                view.findViewById(R.id.group_middle).setVisibility(View.VISIBLE);
                initChildView(middleParent, child);
            }
        }
    }

    @Override
    protected void initChildView(RelativeLayout middleParent, View child) {
        super.initChildView(middleParent, child);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        ivGiftIcon = child.findViewById(R.id.iv_gift_icon);
        tvGiftInfo = child.findViewById(R.id.tv_gift_info);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTvDialogTitle.setText(getString(R.string.gift_send_confirm));
        mBtnDialogConfirm.setText(getString(R.string.gift_send));
        ivGiftIcon.setImageResource(gift.getResource());
        tvGiftInfo.setText(getString(R.string.gift_send_info, gift.getNum(), gift.getName()));
    }

    @Override
    public void onConfirmClick(View v) {
        super.onConfirmClick(v);
        dismiss();
        if (this.listener != null) {
            listener.onConfirmClick(v, gift);
        }
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.listener = listener;
    }
}
