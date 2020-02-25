package com.easemob.livedemo.common;

import android.text.TextUtils;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.data.UserRepository;
import com.easemob.livedemo.data.model.User;

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

    /**
     * 获取用户的昵称
     * @param username
     * @return
     */
    public static String getNickName(String username) {
        User user = UserRepository.getInstance().getUserById(username);
        if(user == null) {
            return username;
        }
        return user.getNickname();
    }

    /**
     * 获取用户头像信息
     * @param username
     * @return
     */
    public static int getAvatarResource(String username) {
        return getAvatarResource(username, 0);
    }

    /**
     * 获取用户头像信息
     * @param username
     * @return
     */
    public static int getAvatarResource(String username, int defaultDrawable) {
        User user = UserRepository.getInstance().getUserById(username);
        if(user == null) {
            return defaultDrawable == 0 ? R.drawable.em_live_logo : defaultDrawable;
        }
        return user.getAvatarResource();
    }
}
