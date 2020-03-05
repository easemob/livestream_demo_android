/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.livedemo.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.easemob.livedemo.DemoApplication;

/**
 * 鉴于使用用户的username作为文件名，故需要在用户登录后，再进行初始化
 */
public class PreferenceManager {
	private static SharedPreferences mSharedPreferences;
	private static PreferenceManager mPreferencemManager;
	private static SharedPreferences.Editor editor;
	private static SharedPreferences mDefaultSp;
	private static SharedPreferences.Editor mDefaultEditor;

	private static final String KEY_LIVING_ID = "key_living_id";
	private static final String KEY_CAN_REGISTER = "key_can_register";
	private static final String KEY_USER_ID = "key_user_id";
	private static final String KEY_LIKE_NUM = "key_like_num";

	@SuppressLint("CommitPrefEdits")
	private PreferenceManager(Context cxt, String username) {
		mSharedPreferences = cxt.getSharedPreferences(username, Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	private static void getDefaultSp(Context context) {
		if(mDefaultSp == null) {
			mDefaultSp = context.getSharedPreferences("demo", Context.MODE_PRIVATE);
			mDefaultEditor = mDefaultSp.edit();
		}
	}

	/**
	 * 鉴于使用用户的username作为文件名，故需要在用户登录后，再进行初始化
	 * @param cxt
	 * @param username
	 */
	public static synchronized void init(Context cxt, String username){
	    if(mPreferencemManager == null){
	        mPreferencemManager = new PreferenceManager(cxt, username);
	    }
	}

	/**
	 * get instance of PreferenceManager
	 *
	 * @param
	 * @return
	 */
	public synchronized static PreferenceManager getInstance() {
		if (mPreferencemManager == null) {
			throw new RuntimeException("please init first!");
		}

		return mPreferencemManager;
	}

	/**
	 * 保存正在直播的id
	 * @param liveId
	 */
	public void saveLivingId(String liveId) {
		editor.putString(KEY_LIVING_ID, liveId);
		editor.apply();
	}

	public String getLivingId() {
		return mSharedPreferences.getString(KEY_LIVING_ID, null);
	}

	/**
	 * 设置是否显示登陆注册页面
	 * @param canRegister
	 */
	public static void setCanRegister(boolean canRegister) {
		getDefaultSp(DemoApplication.getInstance());
		mDefaultEditor.putBoolean(KEY_CAN_REGISTER, canRegister);
		mDefaultEditor.apply();
	}

	public static boolean isCanRegister() {
		getDefaultSp(DemoApplication.getInstance());
		return mDefaultSp.getBoolean(KEY_CAN_REGISTER, false);
	}

	/**
	 * 保存用户id(模拟数据id，非环信id)
	 * @param id
	 */
	public void saveUserId(String id) {
		editor.putString(KEY_USER_ID, id);
		editor.apply();
	}

	public String getUserId() {
		return mSharedPreferences.getString(KEY_USER_ID, null);
	}

	public void saveLikeNum(int num) {
		editor.putInt(KEY_LIKE_NUM, num);
		editor.apply();
	}

	/**
	 * 获取点赞数目
	 * @return
	 */
	public int getLikeNum() {
		return mSharedPreferences.getInt(KEY_LIKE_NUM, 0);
	}
}
