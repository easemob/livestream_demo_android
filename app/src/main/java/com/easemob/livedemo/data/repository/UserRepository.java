package com.easemob.livedemo.data.repository;

import android.text.TextUtils;
import android.util.Log;

import com.easemob.livedemo.DemoApplication;
import com.easemob.livedemo.common.db.DemoDbHelper;
import com.easemob.livedemo.common.db.dao.UserDao;
import com.easemob.livedemo.common.db.entity.UserEntity;
import com.easemob.livedemo.common.inf.OnUpdateUserInfoListener;
import com.easemob.livedemo.data.model.HeadImageInfo;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private static volatile UserRepository mInstance;
    private static final String DEFAULT_BIRTHDAY = "2004-01-01";
    private static final String DEFAULT_GENDER = "1";
    private static final long USER_INFO_EXPIRED_TIME = 0;//60 * 1000;

    private List<HeadImageInfo> mHeadImageList;

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        if (mInstance == null) {
            synchronized (UserRepository.class) {
                if (mInstance == null) {
                    mInstance = new UserRepository();
                }
            }
        }
        return mInstance;
    }

    public void setHeadImageList(List<HeadImageInfo> headImageList) {
        this.mHeadImageList = headImageList;
    }

    public EaseUser getUserInfo(String username) {
        return getUserInfoFromDb(username);
    }

    public UserEntity getUserInfoFromDb(String username) {
        UserDao userDao = DemoDbHelper.getInstance(DemoApplication.getInstance()).getUserDao();
        if (null != userDao) {
            List<UserEntity> list = userDao.loadUserByUserId(username);
            if (null != list && list.size() > 0) {
                return list.get(0);
            }
        }
        return new UserEntity(username);
    }

    public void fetchUserInfo(List<String> usernameList, OnUpdateUserInfoListener listener) {
        EMLog.i("lives", "fetchUserInfo,list=" + usernameList);
        if (null == usernameList || usernameList.size() == 0) {
            if (null != listener) {
                listener.onError(EMError.GENERAL_ERROR, "");
            }
            return;
        }
        //avoid fetch self info
        usernameList.remove(EMClient.getInstance().getCurrentUser());

        Iterator<String> iterator = usernameList.iterator();
        UserEntity user;
        while (iterator.hasNext()) {
            user = getUserInfoFromDb(iterator.next());
            if (user != null && System.currentTimeMillis() - user.getUserInitialTimestamp() < USER_INFO_EXPIRED_TIME) {
                iterator.remove();
            }
        }
        EMLog.i("lives", "getUserInfoFromServer,list=" + usernameList);
        if (usernameList.size() == 0) {
            listener.onSuccess(null);
        } else {
            getUserInfoFromServer(usernameList, listener);
        }
    }

    public void saveUserInfoToDb(EaseUser easeUser) {
        if (null == DemoDbHelper.getInstance(DemoApplication.getInstance()).getUserDao()) {
            return;
        }
        UserEntity userEntity = UserEntity.parseParent(easeUser);
        userEntity.setUserInitialTimestamp(System.currentTimeMillis());
        DemoDbHelper.getInstance(DemoApplication.getInstance()).getUserDao().insert(userEntity);
    }

    private void getUserInfoFromServer(final List<String> usernameList,
                                       final OnUpdateUserInfoListener listener) {
        if (usernameList.size() == 0) {
            return;
        }
        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(usernameList.toArray(new String[0]), new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> value) {
                Log.i("lives", "getUserInfoById success size=" + value.size());
                if (null != listener) {
                    listener.onSuccess(value);
                }
                for (Map.Entry<String, EMUserInfo> entity : value.entrySet()) {
                    EaseUser easeUser = transformUserInfo(entity.getValue());
                    addDefaultAvatar(easeUser, null);
                    saveUserInfoToDb(easeUser);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                Log.e("lives", "getUserInfoById onError error msg=" + errorMsg);
                if (null != listener) {
                    listener.onError(error, errorMsg);
                }
            }
        });
    }

    private void addDefaultAvatar(EaseUser item, List<String> localUsers) {
        if (null == DemoDbHelper.getInstance(DemoApplication.getInstance()).getUserDao()) {
            return;
        }
        if (localUsers == null) {
            localUsers = DemoDbHelper.getInstance(DemoApplication.getInstance()).getUserDao().loadAllUsers();
        }
        if (TextUtils.isEmpty(item.getAvatar())) {
            if (localUsers.contains(item.getUsername())) {
                String avatar = DemoDbHelper.getInstance(DemoApplication.getInstance()).getUserDao().loadUserByUserId(item.getUsername()).get(0).getAvatar();
                if (!TextUtils.isEmpty(avatar)) {
                    item.setAvatar(avatar);
                } else {
                    if (null != mHeadImageList && mHeadImageList.size() > 0) {
                        item.setAvatar(mHeadImageList.get(0).getUrl());
                    }
                }
            } else {
                if (null != mHeadImageList && mHeadImageList.size() > 0) {
                    item.setAvatar(mHeadImageList.get(0).getUrl());
                }
            }
        }
    }

    private EaseUser transformUserInfo(EMUserInfo info) {
        if (info != null) {
            EaseUser userEntity = new EaseUser();
            userEntity.setUsername(info.getUserId());
            userEntity.setNickname(info.getNickname());
            userEntity.setEmail(info.getEmail());
            userEntity.setAvatar(info.getAvatarUrl());
            userEntity.setBirth(info.getBirth());
            userEntity.setGender(info.getGender());
            userEntity.setExt(info.getExt());
            userEntity.setSign(info.getSignature());
            EaseCommonUtils.setUserInitialLetter(userEntity);
            return userEntity;
        }
        return null;
    }
}
