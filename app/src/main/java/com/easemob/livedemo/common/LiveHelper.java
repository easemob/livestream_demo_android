package com.easemob.livedemo.common;

import android.text.TextUtils;

import com.easemob.livedemo.DemoConstants;

public class LiveHelper {

    /**
     * 判断房间状态
     * @param status
     * @return
     */
    public static boolean isLiving(String status) {
        return !TextUtils.isEmpty(status) && TextUtils.equals(status, DemoConstants.LIVE_ONGOING);
    }
}
