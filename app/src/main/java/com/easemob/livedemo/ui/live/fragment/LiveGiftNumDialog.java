package com.easemob.livedemo.ui.live.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.ui.base.BaseLiveDialogFragment;

public class LiveGiftNumDialog extends BaseLiveDialogFragment implements View.OnClickListener, LiveGiftInputNumDialog.OnConfirmClickListener {
    private ImageView ivGiftMinus;
    private ImageView ivGiftPlus;
    private TextView tvGiftNum;
    private Button btnSend;
    private TextView tvGiftTotalValues;
    private int giftNum = 1;
    private GiftBean giftBean;
    private OnGiftNumListener clickListener;
    private OnDismissListener dismissListener;

    public static LiveGiftNumDialog getNewInstance(GiftBean gift) {
        LiveGiftNumDialog dialog = new LiveGiftNumDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gift", gift);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    protected void setAnimation() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_dialog_live_gift_num;
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if (bundle != null) {
            giftBean = (GiftBean) bundle.getSerializable("gift");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        ivGiftMinus = findViewById(R.id.iv_gift_minus);
        tvGiftNum = findViewById(R.id.tv_gift_num);
        ivGiftPlus = findViewById(R.id.iv_gift_plus);
        tvGiftTotalValues = findViewById(R.id.gift_total_values);
        btnSend = findViewById(R.id.btn_send);

        tvGiftNum.setText(String.valueOf(giftNum));

        tvGiftTotalValues.setText(mContext.getString(R.string.gift_send_total_values, String.valueOf(giftNum * giftBean.getValue())));
    }

    @Override
    public void initListener() {
        super.initListener();
        ivGiftMinus.setOnClickListener(this);
        ivGiftPlus.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        tvGiftNum.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_gift_minus:
                giftNum = getNum();
                if (giftNum > 1) {
                    giftNum--;
                }
                updateNumAndValues(giftNum);
                break;
            case R.id.iv_gift_plus:
                giftNum = getNum();
                if (giftNum < 99) {
                    giftNum++;
                }
                updateNumAndValues(giftNum);
                break;
            case R.id.btn_send:
                dismiss();
                giftNum = getNum();
                if (this.clickListener != null) {
                    clickListener.onGiftNum(v, giftNum);
                }
                break;
            case R.id.tv_gift_num:
                showInputNumDialog();
                break;
        }
    }

    private void showInputNumDialog() {
        LiveGiftInputNumDialog dialog = (LiveGiftInputNumDialog) getChildFragmentManager().findFragmentByTag("gift_input_num");
        if (dialog == null) {
            dialog = LiveGiftInputNumDialog.getNewInstance(Integer.parseInt(tvGiftNum.getText().toString().trim()));
        }
        if (dialog.isAdded()) {
            return;
        }
        dialog.setOnConfirmClickListener(this);
        dialog.show(getChildFragmentManager(), "gift_input_num");
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDismiss(dialog);
        }
    }

    private int getNum() {
        try {
            String num = tvGiftNum.getText().toString().trim();
            return Integer.parseInt(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void setOnGiftNumListener(OnGiftNumListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onConfirmClick(View v, int num) {
        updateNumAndValues(num);
    }

    private void updateNumAndValues(int num) {
        tvGiftNum.setText(String.valueOf(num));
        tvGiftTotalValues.setText(mContext.getString(R.string.gift_send_total_values, String.valueOf(num * giftBean.getValue())));

    }

    public interface OnGiftNumListener {
        void onGiftNum(View view, int num);
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.dismissListener = listener;
    }

    public interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }
}
