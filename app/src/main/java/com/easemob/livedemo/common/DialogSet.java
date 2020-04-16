package com.easemob.livedemo.common;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

public interface DialogSet {
    void setTitle(@StringRes int title);

    void setConfirmTitle(@StringRes int confirmTitle);

    void setConfirmColor(@ColorRes int color);

    void setCanceledOnTouchOutside(boolean canceledOnTouchOutside);
}
