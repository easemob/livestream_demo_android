package com.easemob.livedemo.common;

import android.text.TextUtils;

import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.data.UserRepository;
import com.easemob.livedemo.data.model.User;
import com.hyphenate.chat.EMClient;

public class DemoHelper {

    /**
     * 判断房间状态
     * @param status
     * @return
     */
    public static boolean isLiving(String status) {
        return !TextUtils.isEmpty(status) && TextUtils.equals(status, DemoConstants.LIVE_ONGOING);
    }

    /**
     * 判断是不是房主
     * @param username
     * @return
     */
    public static boolean isOwner(String username) {
        if(TextUtils.isEmpty(username)) {
            return false;
        }
        return TextUtils.equals(username, EMClient.getInstance().getCurrentUser());
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

    public static void setCanRegister(boolean canRegister) {
        PreferenceManager.setCanRegister(canRegister);
    }

    /**
     * 获取用户的昵称
     * @param username
     * @return
     */
    public static String getNickName(String username) {
        User user = UserRepository.getInstance().getUserByUsername(username);
        if(user == null) {
            return username;
        }
        return user.getNickname();
    }

    /**
     * 获取当前用户数据（模拟数据）
     * @return
     */
    public static User getCurrentDemoUser() {
        User user = UserRepository.getInstance().getCurrentUser();
        if(user == null) {
            user = UserRepository.getInstance().getUserByUsername(EMClient.getInstance().getCurrentUser());
        }
        return user;
    }

    /**
     * 保存当前用户相关联的id
     */
    public static void saveUserId() {
        PreferenceManager.getInstance().saveUserId(getCurrentDemoUser().getId());
    }

    /**
     * 清除用户id
     */
    public static void clearUserId() {
        PreferenceManager.getInstance().saveUserId("");
    }

    public static String getUserId() {
        return PreferenceManager.getInstance().getUserId();
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
        User user = UserRepository.getInstance().getUserByUsername(username);
        if(user == null) {
            return defaultDrawable == 0 ? R.drawable.em_live_logo : defaultDrawable;
        }
        return user.getAvatarResource();
    }
}
