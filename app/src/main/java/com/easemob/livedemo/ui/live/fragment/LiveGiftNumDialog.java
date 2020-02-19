package com.easemob.livedemo.ui.live.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.OnItemClickListener;
import com.easemob.livedemo.data.model.GiftBean;

import androidx.annotation.NonNull;

public class LiveGiftNumDialog extends BaseLiveDialogFragment implements View.OnClickListener, LiveGiftInputNumDialog.OnConfirmClickListener {
    private TextView tvGiftName;
    private ImageView ivGiftMinus;
    private ImageView ivGiftPlus;
    private TextView tvGiftNum;
    private Button btnSend;
    private int giftNum = 1;
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
        return R.layout.em_fragment_dialog_live_gift_num;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tvGiftName = findViewById(R.id.tv_gift_name);
        ivGiftMinus = findViewById(R.id.iv_gift_minus);
        tvGiftNum = findViewById(R.id.tv_gift_num);
        ivGiftPlus = findViewById(R.id.iv_gift_plus);
        btnSend = findViewById(R.id.btn_send);

        tvGiftNum.setText(giftNum + "");
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
            case R.id.iv_gift_minus :
                giftNum = getNum();
                if(giftNum > 1) {
                    giftNum--;
                }
                tvGiftNum.setText(String.valueOf(giftNum));
                break;
            case R.id.iv_gift_plus :
                giftNum = getNum();
                giftNum ++;
                tvGiftNum.setText(String.valueOf(giftNum));
                break;
            case R.id.btn_send :
                dismiss();
                giftNum = getNum();
                if(this.clickListener != null) {
                    clickListener.onGiftNum(v, giftNum);
                }
                break;
            case R.id.tv_gift_num:
                showInputNumDialog();
                break;
        }
    }

    private void showInputNumDialog() {
        LiveGiftInputNumDialog dialog = LiveGiftInputNumDialog.getNewInstance(Integer.valueOf(tvGiftNum.getText().toString().trim()));
        dialog.setOnConfirmClickListener(this);
        dialog.show(getChildFragmentManager(), "gift_input_num");
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(dismissListener != null) {
            dismissListener.onDismiss(dialog);
        }
    }

    private int getNum() {
        String num = tvGiftNum.getText().toString().trim();
        return Integer.valueOf(num);
    }

    public void setOnGiftNumListener(OnGiftNumListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onConfirmClick(View v, int num) {
        tvGiftNum.setText(String.valueOf(num));
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
