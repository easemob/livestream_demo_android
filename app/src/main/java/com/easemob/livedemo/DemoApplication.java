package com.easemob.livedemo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import com.easemob.custommessage.EmCustomMsgHelper;
import com.easemob.fastlive.FastLiveHelper;
import com.easemob.livedemo.common.LiveDataBus;
import com.easemob.livedemo.common.UserActivityLifecycleCallbacks;
import com.easemob.livedemo.ui.MainActivity;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

/**
 * Created by wei on 2016/5/27.
 */
public class DemoApplication extends Application implements Thread.UncaughtExceptionHandler {
  private static final String TAG = DemoApplication.class.getSimpleName();
  private static DemoApplication instance;
  private UserActivityLifecycleCallbacks mLifecycleCallbacks = new UserActivityLifecycleCallbacks();

  static {
      if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
          AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
      }
  }

  @Override public void onCreate() {
    super.onCreate();
    instance = this;
    registerActivityLifecycleCallbacks();
    registerUncaughtExceptionHandler();
    initChatSdk();
    initAgora();

    //UEasyStreaming.initStreaming("publish3-key");

//    UStreamingContext.init(getApplicationContext(), "publish3-key");
  }

  private void initAgora() {
    //第二个参数为声网appId
    FastLiveHelper.getInstance().init(this, getString(R.string.agora_app_id));
    FastLiveHelper.getInstance().getEngineConfig().setLowLatency(false);
  }

  private void registerUncaughtExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler(this);
  }

  public static DemoApplication getInstance(){
    return instance;
  }

  private void initChatSdk(){
    EMOptions options = new EMOptions();
//    options.enableDNSConfig(false);
//    options.setRestServer("a1-hsb.easemob.com");
//    options.setIMServer("106.75.100.247");
//    options.setImPort(6717);

    EmClientInit(this, options);

    EMClient.getInstance().setDebugMode(BuildConfig.DEBUG);

    EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
      @Override public void onConnected() {
         LiveDataBus.get().with(DemoConstants.NETWORK_CONNECTED).postValue(true);
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

  private void EmClientInit(DemoApplication context, EMOptions options) {
      int pid = android.os.Process.myPid();
      String processAppName = getAppName(context, pid);

      Log.d("", "process app name : " + processAppName);

      // if there is application has remote service, application:onCreate() maybe called twice
      // this check is to make sure SDK will initialized only once
      // return if process name is not application's name since the package name is the default process name
      if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
        Log.e(TAG, "enter the service process!");
        return;
      }
      if(options == null){
        EMClient.getInstance().init(context, initChatOptions());
      }else{
        EMClient.getInstance().init(context, options);
      }

  }

  private EMOptions initChatOptions() {
    Log.d(TAG, "init HuanXin Options");

    EMOptions options = new EMOptions();
    // change to need confirm contact invitation
    options.setAcceptInvitationAlways(false);
    // set if need read ack
    options.setRequireAck(true);
    // set if need delivery ack
    options.setRequireDeliveryAck(false);

    return options;
  }

  /**
   * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
   * @param pID
   * @return
   */
  private String getAppName(Context context, int pID) {
    String processName = null;
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List l = am.getRunningAppProcesses();
    Iterator i = l.iterator();
    PackageManager pm = context.getPackageManager();
    while (i.hasNext()) {
      ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
      try {
        if (info.pid == pID) {
          CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
          // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
          // info.processName +"  Label: "+c.toString());
          // processName = c.toString();
          processName = info.processName;
          return processName;
        }
      } catch (Exception e) {
        // Log.d("Process", "Error>> :"+ e.toString());
      }
    }
    return processName;
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
