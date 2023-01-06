package com.easemob.livedemo.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtils {

    /**
     * 设置默认的系统语言
     * @param context
     * @param language
     */
    public static void setDefaultLanguage(Context context, String language) {
        if(context == null || TextUtils.isEmpty(language)) {
            return;
        }

        Configuration configuration = context.getResources().getConfiguration();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        Locale loc = Locale.CHINA;
        if(!TextUtils.equals(language, "zh")) {
            loc = new Locale(language);
        }
        Locale.setDefault(loc);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(loc);
        }else {
            configuration.locale = loc;
        }

        context.getResources().updateConfiguration(configuration, displayMetrics);
    }

    /**
     * 获取系统默认语言
     * @param context
     * @return
     */
    public static String getDefaultLanguage(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * 判断当前是否是汉语环境
     * @param context
     * @return
     */
    public static boolean isZhLanguage(Context context) {
        return TextUtils.equals(getDefaultLanguage(context), new Locale("zh").getLanguage());
    }
}
