package com.easemob.livedemo.ui.live.fragment;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.base.DemoDialogFragment;
import com.easemob.livedemo.utils.Utils;

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
        if (bundle != null) {
            giftNum = bundle.getInt("num");
        }
    }

    @Override
    public int getMiddleLayoutId() {
        return R.layout.layout_give_gift_num;
    }

    @Override
    protected void initChildView(RelativeLayout middleParent, View child) {
        super.initChildView(middleParent, child);
        middleParent.removeAllViews();
        middleParent.addView(child);

        etInputNum = child.findViewById(R.id.et_input_num);
        etInputNum.setFilters(new InputFilter[]{new InputFilterMinMax("1", "99")});
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTvDialogTitle.setText(getString(R.string.gift_input_num_title));
        etInputNum.setText(String.valueOf(giftNum));
    }

    @Override
    public void onConfirmClick(View v) {
        //super.onConfirmClick(v);
        String num = etInputNum.getText().toString().trim();
        if (TextUtils.isEmpty(num)) {
            Utils.showLongToast(mContext, getString(R.string.live_gift_miss_number));
            return;
        }
        if (this.listener != null) {
            dismiss();
            listener.onConfirmClick(v, Integer.parseInt(num));
        }
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.listener = listener;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(View v, int num);
    }

    static class InputFilterMinMax implements InputFilter {
        private float min, max;

        public InputFilterMinMax(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Float.parseFloat(min);
            this.max = Float.parseFloat(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                if (source.equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index = dest.toString().indexOf(".");
                    int mlength = dest.toString().substring(index).length();
                    if (mlength == 3) {
                        return "";
                    }
                }
                float input = Float.parseFloat(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (Exception nfe) {
                nfe.printStackTrace();
            }
            return "";
        }

        private boolean isInRange(float a, float b, float c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
