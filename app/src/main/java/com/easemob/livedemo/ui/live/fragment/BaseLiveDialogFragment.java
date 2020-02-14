package com.easemob.livedemo.ui.live.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.activity.BaseDialogFragment;

public abstract class BaseLiveDialogFragment extends BaseDialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        try {
            Window dialogWindow = getDialog().getWindow();
            dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
            dialogWindow.setDimAmount(0);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            dialogWindow.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setAnimation() {
        super.setAnimation();
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.windowAnimations = R.style.LiveDialogFragment_Animation;
            dialogWindow.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
