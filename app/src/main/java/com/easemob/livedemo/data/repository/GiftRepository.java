package com.easemob.livedemo.data.repository;

import android.content.Context;
import android.text.TextUtils;

import com.easemob.livedemo.DemoApplication;
import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.data.model.User;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;


public class GiftRepository {

    public static List<GiftBean> getDefaultGifts() {
        Context context = DemoApplication.getInstance().getApplicationContext();
        List<GiftBean> gifts = new ArrayList<>();
        GiftBean bean;
        User user;
        String[] giftNames = context.getResources().getStringArray(R.array.gift_names);
        String[] giftResNames = context.getResources().getStringArray(R.array.gift_res_names);
        int[] giftValues = context.getResources().getIntArray(R.array.gift_values);
        for (int i = 0; i < giftNames.length; i++) {
            bean = new GiftBean();
            int resId = context.getResources().getIdentifier(giftResNames[i], "drawable", context.getPackageName());
            bean.setResource(resId);
            bean.setName(giftNames[i]);
            bean.setId("gift_" + (i + 1));
            bean.setValue(giftValues[i]);
            user = new User();
            user.setId(EMClient.getInstance().getCurrentUser());
            bean.setUser(user);
            bean.setLeftTime(0);
            gifts.add(bean);
        }
        return gifts;
    }

    public static GiftBean getGiftById(String giftId) {
        List<GiftBean> gifts = getDefaultGifts();
        for (GiftBean bean : gifts) {
            if (TextUtils.equals(bean.getId(), giftId)) {
                return bean;
            }
        }
        return null;
    }
}
