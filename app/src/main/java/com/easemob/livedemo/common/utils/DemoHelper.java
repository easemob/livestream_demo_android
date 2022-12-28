package com.easemob.livedemo.common.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.easemob.livedemo.common.db.DemoDbHelper;
import com.easemob.livedemo.common.db.dao.ReceiveGiftDao;
import com.easemob.livedemo.common.db.dao.UserDao;
import com.easemob.livedemo.common.db.entity.ReceiveGiftEntity;
import com.easemob.livedemo.common.db.entity.UserEntity;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.repository.GiftRepository;
import com.easemob.livedemo.data.repository.UserRepository;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.easemob.chatroom.EaseLiveMessageConstant;
import com.easemob.chatroom.EaseLiveMessageType;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.easemob.live.FastLiveHelper;
import com.easemob.livedemo.DemoApplication;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.common.livedata.LiveDataBus;

public class DemoHelper {

    private static Map<String, EaseUser> userList;

    /**
     * is living
     *
     * @param status
     * @return
     */
    public static boolean isLiving(String status) {
        return !TextUtils.isEmpty(status) && TextUtils.equals(status, DemoConstants.LIVE_ONGOING);
    }

    /**
     * is owner
     *
     * @param username
     * @return
     */
    public static boolean isOwner(String username) {
        if (TextUtils.isEmpty(username)) {
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

    public static void removeTarget(String key) {
        FastLiveHelper.getInstance().getFastSPreferences().edit().remove(key).commit();
    }

    public static void removeSaveLivingId() {
        PreferenceManager.getInstance().removeLivingId();
    }

    /**
     * save current user
     */
    public static void saveCurrentUser() {
        PreferenceManager.getInstance().saveAgoraId(UserRepository.getInstance().getCurrentUser().getId());
        PreferenceManager.getInstance().savePwd(UserRepository.getInstance().getCurrentUser().getPwd());
    }

    /**
     * clear agora id
     */
    public static void clearUser() {
        PreferenceManager.getInstance().saveAgoraId("");
        PreferenceManager.getInstance().savePwd("");
    }

    public static String getAgoraId() {
        return PreferenceManager.getInstance().getAgoraId();
    }

    public static String getPwd() {
        return PreferenceManager.getInstance().getPwd();
    }

    /**
     * get gift
     *
     * @param giftId
     * @return
     */
    public static GiftBean getGiftById(String giftId) {
        return GiftRepository.getGiftById(giftId);
    }

    public static void init() {
        initDb();
        EaseIM.getInstance()
                .setUserProvider(new EaseUserProfileProvider() {
                    @Override
                    public EaseUser getUser(String userID) {
                        return UserRepository.getInstance().getUserInfo(userID);
                    }
                });
    }

    private static void initDb() {
        DemoDbHelper.getInstance(DemoApplication.getInstance()).initDb();
    }

    /**
     * get receive gift
     *
     * @return
     */
    public static ReceiveGiftDao getReceiveGiftDao() {
        return DemoDbHelper.getInstance(DemoApplication.getInstance()).getReceiveGiftDao();
    }

    /**
     * save gift to local
     *
     * @param message
     */
    public static void saveGiftInfo(EMMessage message) {
        if (message == null) {
            return;
        }
        EMMessageBody body = message.getBody();
        if (!(body instanceof EMCustomMessageBody)) {
            return;
        }
        String event = ((EMCustomMessageBody) body).event();
        if (!TextUtils.equals(event, EaseLiveMessageType.CHATROOM_GIFT.getName())) {
            return;
        }
        Map<String, String> params = ((EMCustomMessageBody) body).getParams();
        Set<String> keySet = params.keySet();
        String gift_id = null;
        String gift_num = null;
        if (keySet.contains(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_ID) && keySet.contains(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_NUM)) {
            gift_id = params.get(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_ID);
            gift_num = params.get(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_NUM);
            ReceiveGiftEntity entity = new ReceiveGiftEntity();
            entity.setFrom(message.getFrom());
            entity.setTo(message.getTo());
            entity.setTimestamp(message.getMsgTime());
            entity.setGift_id(gift_id);
            entity.setGift_num(Integer.parseInt(gift_num));
            List<Long> list = getReceiveGiftDao().insert(entity);
            if (list.size() <= 0) {
            } else {
                LiveDataBus.get().with(DemoConstants.REFRESH_GIFT_LIST).postValue(true);
            }
        }
    }

    public static void saveLikeInfo(EMMessage message) {
        if (message == null) {
            return;
        }
        EMMessageBody body = message.getBody();
        if (!(body instanceof EMCustomMessageBody)) {
            return;
        }
        String event = ((EMCustomMessageBody) body).event();
        if (!TextUtils.equals(event, EaseLiveMessageType.CHATROOM_PRAISE.getName())) {
            return;
        }
        Map<String, String> params = ((EMCustomMessageBody) body).getParams();
        Set<String> keySet = params.keySet();
        String num = null;
        if (keySet.contains(EaseLiveMessageConstant.LIVE_MESSAGE_PRAISE_KEY_NUM)) {
            num = params.get(EaseLiveMessageConstant.LIVE_MESSAGE_PRAISE_KEY_NUM);
        }
        if (!TextUtils.isEmpty(num)) {
            int like_num = Integer.parseInt(num);
            int total = getLikeNum(message.getTo()) + like_num;
            saveLikeNum(message.getTo(), total);
            LiveDataBus.get().with(DemoConstants.REFRESH_LIKE_NUM).postValue(true);
        }

    }

    public static void saveLikeNum(String roomId, int num) {
        PreferenceManager.getInstance().saveLikeNum(roomId, num);
    }

    public static int getLikeNum(String roomId) {
        return PreferenceManager.getInstance().getLikeNum(roomId);
    }

    public static boolean isFastLiveType(String videoType) {
        return TextUtils.equals(videoType, LiveRoom.Type.agora_speed_live.name());
    }


    public static boolean isInteractionLiveType(String videoType) {
        return TextUtils.equals(videoType, LiveRoom.Type.agora_interaction_live.name());
    }


    public static boolean isCdnLiveType(String videoType) {
        return TextUtils.equals(videoType, LiveRoom.Type.agora_cdn_live.name());
    }


    public static boolean isVod(String videoType) {
        return TextUtils.equals(videoType, LiveRoom.Type.vod.name()) || TextUtils.equals(videoType, LiveRoom.Type.agora_vod.name());
    }

    /**
     * Determine if you have logged in before
     *
     * @return
     */
    public static boolean isLoggedIn() {
        return getChatClient().isLoggedInBefore();
    }

    /**
     * Get EMClient's entity
     *
     * @return
     */
    public static EMClient getChatClient() {
        return EMClient.getInstance();
    }

    public static Map<String, EaseUser> getUserList() {
        // Fetching data directly from the local database without considering too many complex scenarios
        if (isLoggedIn()) {
            userList = getAllUserList();
        }

        // return a empty non-null object to avoid app crash
        if (userList == null) {
            return new Hashtable<String, EaseUser>();
        }
        return userList;
    }

    public static Map<String, EaseUser> getAllUserList() {
        UserDao dao = DemoDbHelper.getInstance(DemoApplication.getInstance()).getUserDao();
        if (dao == null) {
            return new HashMap<>();
        }
        Map<String, EaseUser> map = new HashMap<>();
        List<UserEntity> users = dao.loadAllEaseUsers();
        if (users != null && !users.isEmpty()) {
            for (EaseUser user : users) {
                map.put(user.getUsername(), user);
            }
        }
        return map;
    }

    public static void setDefaultLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        config.locale = Locale.ENGLISH;
        context.getResources().updateConfiguration(config, metrics);
    }
}
