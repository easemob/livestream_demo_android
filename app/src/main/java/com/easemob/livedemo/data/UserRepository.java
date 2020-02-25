package com.easemob.livedemo.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserRepository {
    private static UserRepository instance;
    private UserRepository(){}
    private Context context;
    private List<User> mUsers;

    public static UserRepository getInstance() {
        if(instance == null) {
            synchronized (UserRepository.class) {
                if(instance == null) {
                    instance = new UserRepository();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        String users = getJsonFromAssets("users.json");
        if(TextUtils.isEmpty(users)) {
            return;
        }
        mUsers = new Gson().fromJson(users, new TypeToken<ArrayList<User>>(){}.getType());
    }

    /**
     * 获取随机用户
     * @return
     */
    public User getRandomUser() {
        if(mUsers != null) {
            return mUsers.get(getRandom(mUsers.size()));
        }
        return getDefaultUser();
    }

    /**
     * 获取默认密码
     * @return
     */
    public String getDefaultPsw() {
        return "123";
    }

    /**
     * 获取默认用户
     * @return
     */
    private User getDefaultUser() {
        User user = new User();
        user.setUsername("hxtest");
        user.setNick("测试");
        user.setAvatarResource(R.drawable.em_avatar_1);
        return user;
    }

    /**
     * 通过环信id，寻找默认用户
     * @param username
     * @return
     */
    public User getUserById(String username) {
        if(mUsers != null) {
            for(User user : mUsers) {
                if(TextUtils.equals(username, user.getUsername())) {
                    return user;
                }
            }
        }
        User defaultUser = getDefaultUser();
        if(TextUtils.equals(username, defaultUser.getUsername())) {
            return defaultUser;
        }
        return null;
    }

    /**
     * 获取随机数
     * @param max
     * @return
     */
    public int getRandom(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    public String getJsonFromAssets(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
