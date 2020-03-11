package com.easemob.livedemo.common;

import android.text.TextUtils;
import android.util.Log;

import com.easemob.custommessage.EmCustomMsgType;
import com.easemob.custommessage.MsgConstant;
import com.easemob.livedemo.DemoApplication;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.db.DemoDbHelper;
import com.easemob.livedemo.common.db.dao.ReceiveGiftDao;
import com.easemob.livedemo.common.db.entity.ReceiveGiftEntity;
import com.easemob.livedemo.data.TestGiftRepository;
import com.easemob.livedemo.data.UserRepository;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.data.model.User;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * 通过id获取gift对象
     * @param giftId
     * @return
     */
    public static GiftBean getGiftById(String giftId) {
        return TestGiftRepository.getGiftById(giftId);
    }

    /**
     * 初始化数据库
     */
    public static void initDb() {
        DemoDbHelper.getInstance(DemoApplication.getInstance()).initDb(EMClient.getInstance().getCurrentUser());
    }

    /**
     * 获取ReceiveGiftDao
     * @return
     */
    public static ReceiveGiftDao getReceiveGiftDao() {
        return DemoDbHelper.getInstance(DemoApplication.getInstance()).getReceiveGiftDao();
    }

    /**
     * 保存礼物消息到本地
     * @param message
     */
    public static void saveGiftInfo(EMMessage message) {
        if(message == null) {
            return;
        }
        EMMessageBody body = message.getBody();
        if(!(body instanceof EMCustomMessageBody)) {
            return;
        }
        String event = ((EMCustomMessageBody) body).event();
        if(!TextUtils.equals(event, EmCustomMsgType.CHATROOM_GIFT.getName())) {
            return;
        }
        Map<String, String> params = ((EMCustomMessageBody) body).getParams();
        Set<String> keySet = params.keySet();
        String gift_id = null;
        String gift_num = null;
        if(keySet.contains(MsgConstant.CUSTOM_GIFT_KEY_ID) && keySet.contains(MsgConstant.CUSTOM_GIFT_KEY_NUM)) {
            gift_id = params.get(MsgConstant.CUSTOM_GIFT_KEY_ID);
            gift_num = params.get(MsgConstant.CUSTOM_GIFT_KEY_NUM);
            ReceiveGiftEntity entity = new ReceiveGiftEntity();
            entity.setFrom(message.getFrom());
            entity.setTo(message.getTo());
            entity.setTimestamp(message.getMsgTime());
            entity.setGift_id(gift_id);
            entity.setGift_num(Integer.valueOf(gift_num));
            List<Long> list = getReceiveGiftDao().insert(entity);
            if(list.size() <= 0) {
                Log.e("TAG", "保存数据失败！");
            }else {
                Log.i("TAG", "保存数据成功");
                LiveDataBus.get().with(DemoConstants.REFRESH_GIFT_LIST).postValue(true);
            }
        }
    }

    /**
     * 保存点赞数量
     * @param message
     */
    public static void saveLikeInfo(EMMessage message) {
        if(message == null) {
            return;
        }
        EMMessageBody body = message.getBody();
        if(!(body instanceof EMCustomMessageBody)) {
            return;
        }
        String event = ((EMCustomMessageBody) body).event();
        if(!TextUtils.equals(event, EmCustomMsgType.CHATROOM_LIKE.getName())) {
            return;
        }
        Map<String, String> params = ((EMCustomMessageBody) body).getParams();
        Set<String> keySet = params.keySet();
        String num = null;
        if(keySet.contains(MsgConstant.CUSTOM_LIKE_KEY_NUM)) {
            num = params.get(MsgConstant.CUSTOM_LIKE_KEY_NUM);
        }
        if(!TextUtils.isEmpty(num)) {
            int like_num = Integer.valueOf(num);
            int total = getLikeNum() + like_num;
            saveLikeNum(total);
            LiveDataBus.get().with(DemoConstants.REFRESH_LIKE_NUM).postValue(true);
        }

    }

    public static void saveLikeNum(int num) {
        PreferenceManager.getInstance().saveLikeNum(num);
    }

    public static int getLikeNum() {
        return PreferenceManager.getInstance().getLikeNum();
    }
}
