package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.DemoDialogFragment;

public class LiveGiftInputNumDialog extends DemoDialogFragment {
    private EditText etInputNum;
    private OnConfirmClickListener listener;
    private int giftNum;

    public static LiveGiftInputNumDialog getNewInstance(int num) {
        LiveGiftInputNumDialog dialog = new LiveGiftInputNumDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            giftNum = bundle.getInt("num");
        }
    }

    @Override
    public int getMiddleLayoutId() {
        return R.layout.em_layout_give_gift_num;
    }

    @Override
    protected void initChildView(RelativeLayout middleParent, View child) {
        super.initChildView(middleParent, child);
        middleParent.removeAllViews();
        middleParent.addView(child);

        etInputNum = child.findViewById(R.id.et_input_num);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTvDialogTitle.setText(getString(R.string.em_gift_input_num_title));
        etInputNum.setText(String.valueOf(giftNum));
    }

    @Override
    public void onConfirmClick(View v) {
        super.onConfirmClick(v);
        String num = etInputNum.getText().toString().trim();
        if(TextUtils.isEmpty(num)) {
            Toast.makeText(mContext, "请输入送礼物的数目", Toast.LENGTH_SHORT).show();
            return;
        }
        if(this.listener != null) {
            dismiss();
            listener.onConfirmClick(v, Integer.valueOf(num));
        }
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.listener = listener;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(View v, int num);
    }
}
