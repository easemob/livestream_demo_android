package com.easemob.livedemo.common;

import android.text.TextUtils;

import com.easemob.livedemo.DemoConstants;

public class DemoHelper {

    /**
     * 判断房间状态
     * @param status
     * @return
     */
    public static boolean isLiving(String status) {
        return !TextUtils.isEmpty(status) && TextUtils.equals(status, DemoConstants.LIVE_ONGOING);
    }

    public static void saveLivingId(String liveId) {
        PreferenceManager.getInstance().saveLivingId(liveId);
    }

    public static String getLivingId() {
        return PreferenceManager.getInstance().getLivingId();
    }

    /**
     * 是否显示登陆注册
     * @return
     */
    public static boolean isCanRegister() {
        return PreferenceManager.isCanRegister();
    }
}
