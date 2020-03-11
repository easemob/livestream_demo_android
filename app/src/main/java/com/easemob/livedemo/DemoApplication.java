package com.easemob.livedemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.easemob.custommessage.EmCustomMsgHelper;
import com.easemob.livedemo.common.UserActivityLifecycleCallbacks;
import com.easemob.livedemo.ui.activity.MainActivity;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.ucloud.ulive.UStreamingContext;

import androidx.multidex.MultiDex;

/**
 * Created by wei on 2016/5/27.
 */
public class DemoApplication extends Application{
  private static DemoApplication instance;
  private UserActivityLifecycleCallbacks mLifecycleCallbacks = new UserActivityLifecycleCallbacks();

  @Override public void onCreate() {
    super.onCreate();
    instance = this;
    registerActivityLifecycleCallbacks();

    initChatSdk();

    EmCustomMsgHelper.getInstance().init(this);

    //UEasyStreaming.initStreaming("publish3-key");

    UStreamingContext.init(getApplicationContext(), "publish3-key");
  }

  public static DemoApplication getInstance(){
    return instance;
  }

  private void initChatSdk(){
    EMOptions options = new EMOptions();
    options.enableDNSConfig(true);
    options.setRestServer("a1-hsb.easemob.com");
    options.setIMServer("39.107.54.56");
    options.setImPort(6717);

    EaseUI.getInstance().init(this, options);
    EMClient.getInstance().setDebugMode(true);

    EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
      @Override public void onConnected() {

      }

      @Override public void onDisconnected(int errorCode) {
        if(errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE)
        {
          Intent intent = new Intent(getApplicationContext(), MainActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.putExtra("conflict", true);
          startActivity(intent);
        }
      }
    });
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

}
