package com.easemob.livedemo.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.easemob.livedemo.DemoApplication;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.data.model.User;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

public class TestGiftRepository {
    static int SIZE = 8;
    static String[] names = {};

    public static List<GiftBean> getDefaultGifts() {
        Context context = DemoApplication.getInstance().getApplicationContext();
        List<GiftBean> gifts = new ArrayList<>();
        GiftBean bean;
        User user;
        for(int i = 1; i <= SIZE; i++){
            bean = new GiftBean();
            String name = "gift_default_"+i;
            int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            int nameId = context.getResources().getIdentifier("em_gift_default_name_" + i, "string", context.getPackageName());
            bean.setResource(resId);
            bean.setName(context.getString(nameId));
            bean.setId("gift_"+i);
            user = new User();
            user.setUsername(EMClient.getInstance().getCurrentUser());
            bean.setUser(user);
            gifts.add(bean);
        }
        return gifts;
    }

    /**
     * 获取GiftBean
     * @param giftId
     * @return
     */
    public static GiftBean getGiftById(String giftId) {
        List<GiftBean> gifts = getDefaultGifts();
        for (GiftBean bean : gifts) {
            if(TextUtils.equals(bean.getId(), giftId)) {
                return bean;
            }
        }
        return null;
    }
}
