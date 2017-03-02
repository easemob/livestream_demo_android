package com.easemob.livedemo;

import android.app.Application;
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
    EaseUI.getInstance().init(this, null);
    UEasyStreaming.initStreaming("publish3-key");

  }

  public static DemoApplication getInstance(){
    return instance;
  }
}
