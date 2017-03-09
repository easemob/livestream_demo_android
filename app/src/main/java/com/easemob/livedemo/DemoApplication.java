package com.easemob.livedemo;

import android.app.Application;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.ucloud.live.UEasyStreaming;

/**
 * Created by wei on 2016/5/27.
 */
public class DemoApplication extends Application{

  private static DemoApplication instance;


  @Override public void onCreate() {
    super.onCreate();
    instance = this;
    EMOptions options = new EMOptions();
    options.enableDNSConfig(false);
    options.setRestServer("120.26.4.73:81");
    options.setIMServer("120.26.4.73");
    options.setImPort(6717);

    EaseUI.getInstance().init(this, options);
    EMClient.getInstance().setDebugMode(true);

    UEasyStreaming.initStreaming("publish3-key");


  }

  public static DemoApplication getInstance(){
    return instance;
  }

}
