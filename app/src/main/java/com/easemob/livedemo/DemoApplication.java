package com.easemob.livedemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.easemob.livedemo.common.callback.UserActivityLifecycleCallbacks;
import com.easemob.livedemo.common.livedata.LiveDataBus;
import com.easemob.livedemo.ui.LoginActivity;
import com.easemob.livedemo.ui.MainActivity;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.util.EMLog;

import com.easemob.live.FastLiveHelper;


public class DemoApplication extends Application implements Thread.UncaughtExceptionHandler {
    private static final String TAG = DemoApplication.class.getSimpleName();
    private static DemoApplication instance;
    private final UserActivityLifecycleCallbacks mLifecycleCallbacks = new UserActivityLifecycleCallbacks();
    public boolean isSDKInit;

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks();
        registerUncaughtExceptionHandler();
        initChatSdk(this.getApplicationContext());
        initAgora();
    }

    private void initAgora() {
        FastLiveHelper.getInstance().init(this, BuildConfig.AGORA_APP_ID);
        FastLiveHelper.getInstance().getEngineConfig().setLowLatency(false);
    }

    private void registerUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static DemoApplication getInstance() {
        return instance;
    }

    private void initChatSdk(Context context) {
        if (initSDK(context)) {
            EMClient.getInstance().setDebugMode(BuildConfig.DEBUG);

            EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
                @Override
                public void onConnected() {
                    LiveDataBus.get().with(DemoConstants.NETWORK_CONNECTED).postValue(true);
                }

                @Override
                public void onDisconnected(int errorCode) {

                }

                @Override
                public void onLogout(int errorCode) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("errorCode", errorCode);
                    getActivityLifecycle().skipToTarget(intent);
                }
            });
        }
    }

    /**
     * Initialize Agora Chat SDK
     *
     * @param context
     * @return
     */
    private boolean initSDK(Context context) {
        // Set Chat Options
        EMOptions options = initChatOptions(context);
        if (options == null) {
            return false;
        }

        // Configure custom rest server and im server
        //options.setRestServer(BuildConfig.APP_SERVER_DOMAIN);
        //options.setIMServer("106.75.100.247");
        //options.setImPort(6717);
        //options.setUsingHttpsOnly(false);
        isSDKInit = EaseIM.getInstance().init(context, options);
        return isSDKInit();
    }


    private EMOptions initChatOptions(Context context) {
        Log.d(TAG, "init Agora Chat Options");

        EMOptions options = new EMOptions();
        // You can set your AppKey by options.setAppKey(appkey)
        if (!checkAgoraChatAppKey(context)) {
            EMLog.e(TAG, "no agora chat app key and return");
            return null;
        }
        options.setAutoLogin(true);
        return options;
    }

    private boolean checkAgoraChatAppKey(Context context) {
        String appPackageName = context.getPackageName();
        ApplicationInfo ai = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(appPackageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if (ai != null) {
            Bundle metaData = ai.metaData;
            if (metaData == null) {
                return false;
            }
            // read appkey
            String appKeyFromConfig = metaData.getString("EASEMOB_APPKEY");
            return !TextUtils.isEmpty(appKeyFromConfig);
        }
        return false;
    }

    public boolean isSDKInit() {
        return EMClient.getInstance().isSdkInited();
    }

    private void registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }

    public UserActivityLifecycleCallbacks getActivityLifecycle() {
        return mLifecycleCallbacks;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        e.printStackTrace();
        System.exit(1);
        Process.killProcess(Process.myPid());
    }
}
