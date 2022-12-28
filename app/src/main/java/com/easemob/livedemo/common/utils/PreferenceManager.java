package com.easemob.livedemo.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.easemob.livedemo.DemoApplication;

public class PreferenceManager {
    private static SharedPreferences mSharedPreferences;
    private static PreferenceManager mPreferenceManager;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences mDefaultSp;
    private static SharedPreferences.Editor mDefaultEditor;

    private static final String KEY_LIVING_ID = "key_living_id";
    private static final String KEY_AGORA_ID = "key_agora_id";
    private static final String KEY_PWD = "key_pwd";

    @SuppressLint("CommitPrefEdits")
    private PreferenceManager(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences("live_stream", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    private static void getDefaultSp(Context context) {
        if (mDefaultSp == null) {
            mDefaultSp = context.getSharedPreferences("demo", Context.MODE_PRIVATE);
            mDefaultEditor = mDefaultSp.edit();
        }
    }

    /**
     * @param cxt
     */
    public static synchronized void init(Context cxt) {
        if (mPreferenceManager == null) {
            mPreferenceManager = new PreferenceManager(cxt);
        }
    }

    /**
     * get instance of PreferenceManager
     *
     * @param
     * @return
     */
    public synchronized static PreferenceManager getInstance() {
        if (mPreferenceManager == null) {
            init(DemoApplication.getInstance());
            if (mPreferenceManager == null) {
                throw new RuntimeException("please init first!");
            }
        }

        return mPreferenceManager;
    }

    /**
     * save the living of id
     *
     * @param liveId
     */
    public void saveLivingId(String liveId) {
        editor.putString(KEY_LIVING_ID, liveId);
        editor.apply();
    }

    public String getLivingId() {
        if (null != mSharedPreferences) {
            return mSharedPreferences.getString(KEY_LIVING_ID, null);
        } else {
            return "";
        }
    }

    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        if (null != mSharedPreferences) {
            return mSharedPreferences.getInt(key, -1);
        } else {
            return -1;
        }
    }

    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * save agora id
     *
     * @param id
     */
    public void saveAgoraId(String id) {
        editor.putString(KEY_AGORA_ID, id);
        editor.apply();
    }

    public String getAgoraId() {
        if (null != mSharedPreferences) {
            return mSharedPreferences.getString(KEY_AGORA_ID, null);
        } else {
            return "";
        }
    }

    public void savePwd(String pwd) {
        editor.putString(KEY_PWD, pwd);
        editor.apply();
    }

    public String getPwd() {
        if (null != mSharedPreferences) {
            return mSharedPreferences.getString(KEY_PWD, null);
        } else {
            return "";
        }
    }

    public void saveLikeNum(String roomId, int num) {
        if (TextUtils.isEmpty(roomId)) {
            return;
        }
        editor.putInt(roomId, num);
        editor.apply();
    }

    /**
     * get the number of like
     *
     * @return
     */
    public int getLikeNum(String roomId) {
        return mSharedPreferences.getInt(roomId, 0);
    }

    public void removeLivingId() {
        remove(KEY_LIVING_ID);
    }
}
