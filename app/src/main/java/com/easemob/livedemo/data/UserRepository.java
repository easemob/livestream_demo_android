package com.easemob.livedemo.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.data.model.User;
import com.easemob.livedemo.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 用于解析及获取模拟用户数据
 */
public class UserRepository {
    private static UserRepository instance;
    private UserRepository(){}
    private Context context;
    private List<User> mUsers;
    private User currentUser;
    private Map<String, User> userMap = new HashMap<>();

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
        User user;
        if(mUsers != null) {
            user = mUsers.get(getRandom(mUsers.size()));
        }else {
            user = getDefaultUser();
        }
        user.setUsername(Utils.getStringRandom(8));
        currentUser = user;
        userMap.put(user.getUsername(), user);
        return user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 获取默认密码
     * @return
     */
    public String getDefaultPsw() {
        return "000000";
    }

    /**
     * 获取默认用户
     * @return
     */
    private User getDefaultUser() {
        User user = new User();
        user.setId("hxtest");
        user.setNick("测试");
        user.setAvatarResource(R.drawable.em_avatar_1);
        return user;
    }

    /**
     * 通过环信id，寻找默认用户
     * @param username
     * @return
     */
    public User getUserByUsername(String username) {
        //先检查map中是否有数据
        if(userMap.keySet().contains(username)) {
            return userMap.get(username);
        }
        //再检查currentUser是否存在，以及是否是需要的
        if(currentUser != null && TextUtils.equals(username, currentUser.getUsername())) {
            userMap.put(username, currentUser);
            return currentUser;
        }
        //再检查是否是当前用户，如果是的话，检查是否有保存的用户id
        if(TextUtils.equals(username, EMClient.getInstance().getCurrentUser())) {
            String userId = DemoHelper.getUserId();
            if(!TextUtils.isEmpty(userId)) {
                User user = getUserById(userId);
                if(user != null) {
                    currentUser = user;
                    userMap.put(username, user);
                    return user;
                }
            }
        }
        //如果以上均没有则返回一个随机用户数据
        if(mUsers != null) {
            User user = mUsers.get(getRandom(mUsers.size()));
            userMap.put(username, user);
            if(TextUtils.equals(username, EMClient.getInstance().getCurrentUser())) {
                currentUser = user;
            }
            return user;
        }
        User defaultUser = getDefaultUser();
        if(TextUtils.equals(username, defaultUser.getUsername())) {
            userMap.put(username, defaultUser);
            if(TextUtils.equals(username, EMClient.getInstance().getCurrentUser())) {
                currentUser = defaultUser;
            }
            return defaultUser;
        }
        return null;
    }

    /**
     * 通过模拟数据id，返回用户数据
     * @param id
     * @return
     */
    public User getUserById(String id) {
        if(mUsers != null) {
            for(User user : mUsers) {
                if(TextUtils.equals(id, user.getId())) {
                    return user;
                }
            }
        }
        User defaultUser = getDefaultUser();
        if(TextUtils.equals(id, defaultUser.getId())) {
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
