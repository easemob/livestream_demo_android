# 集成CDN直播

Agora直播聊天室（以下称Agora聊天室）中 **CDN直播** 采用的是Agora CDN直播的方案，展示了视频直播与直播聊天室结合的场景。</br>

**
Agora CDN直播官方集成文档：**[CDN直播](https://docs.agora.io/cn/live-streaming/start_live_standard_android?platform=Android)</br>

## 集成Agora CDN直播步骤

### 集成步骤

> (1)注册Agora账号，创建项目并获取App ID。</br>
> (2)集成Agora CDN直播SDK:JCenter自动集成或者手动复制SDK文件。</br>
> (3)配置相关权限。如网络权限等。</br>
> (4)初始化SDK。一般放在程序入口。</br>
> (5)设置频道场景，并设置用户角色。</br>
> (6)加入频道，主播设置本地视图开始直播，观众设置远端视频观看直播。</br>

为了便于使用，Agora聊天室中将Agora CDN直播的依赖及相关辅助类放到了fastlive library中，以方便开发者使用。</br>

### 核心类介绍

- FastLiveHelper是CDN直播的帮助类，提供了初始化及设置等逻辑。</br>
- FastLiveHostFragment主播直播fragment，封装了主播直播的相关逻辑。通过FastHostPresenter的实现类，可以从外部操作推流，离开频道等功能。</br>
-
FastLiveAudienceFragment观众观看直播fragment，封装了观众观看直播的相关逻辑。通过FastAudiencePresenter的实现类，可以从外部操作关闭直播，离开频道等。</br>

开发者使用时，将相应的fragment加入到相应的activity中，并提供相应的presenter的实现类即可。

## 示例：

以使用fastlive library为例介绍。

### 1、添加项目权限

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
   package="io.agora.livedemo">

   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.RECORD_AUDIO" />
   <uses-permission android:name="android.permission.CAMERA" />
   <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
...
</manifest>
```

**注：麦克风，摄像头及存储权限需要申请，请在需要使用的地方进行申请。**

### 2、初始化

```
private void initAgora() {
  //第二个参数为agora appId
  FastLiveHelper.getInstance().init(this, BuildConfig.AGORA_APP_ID);
}
```

### 3、在activity中添加FastLiveHostFragment或者LiveAudienceFragment

（1）FastLiveHostActivity中添加FastLiveHostFragment

```
 private void initVideoFragment() {
        fastFragment = (FastLiveHostFragment) getSupportFragmentManager().findFragmentByTag("fast_live_host_video");
        presenter = new FastLiveHostPresenterImpl();
        if(fastFragment == null) {
            fastFragment = new FastLiveHostFragment(presenter);
            Bundle bundle = new Bundle();
            bundle.putString("channel", liveRoom.getChannel());
            bundle.putString("roomId", liveRoom.getId());
            bundle.putString("hxId", EMClient.getInstance().getCurrentUser());
            bundle.putString("hxAppkey", EMClient.getInstance().getOptions().getAppKey());
            fastFragment.setArguments(bundle);
        }else {
            fastFragment.setPresenter(presenter);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_video_fragment, fastFragment, "fast_live_host_video").commit();
    }
```

（2）FastLiveAudienceActivity中添加FastLiveAudienceFragment

```
    private void initVideoFragment() {
        fastFragment = (FastLiveAudienceFragment) getSupportFragmentManager().findFragmentByTag("fast_live_audience_video");
        presenter = new FastLiveAudiencePresenterImpl();
        if(fastFragment == null) {
            fastFragment = new FastLiveAudienceFragment(presenter);
            Bundle bundle = new Bundle();
            bundle.putString("channel", liveRoom.getChannel());
            bundle.putString("roomId", liveRoom.getId());
            bundle.putString("hxId", EMClient.getInstance().getCurrentUser());
            bundle.putString("hxAppkey", EMClient.getInstance().getOptions().getAppKey());
            fastFragment.setArguments(bundle);
        }else {
            fastFragment.setPresenter(presenter);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_video_fragment, fastFragment, "fast_live_audience_video").commit();
    }
```

### 4、分别集成并实现FastHostPresenter和FastAudiencePresenter

（1）实现FastHostPresenter

```
public class FastLiveHostPresenterImpl extends FastHostPresenter {
    @Override
    public void onStartCamera() {
        runOnUI(()-> {
            if(isActive()) {
                mView.onStartBroadcast();
            }
        });
    }

    @Override
    public void switchCamera() {
        runOnUI(()-> {
            if(isActive()) {
                mView.switchCamera();
            }
        });
    }

    @Override
    public void leaveChannel() {
        runOnUI(()-> {
            if(isActive()) {
                mView.onLeaveChannel();
            }
        });
    }

    @Override
    public void getFastToken(String hxId, String channel, String hxAppkey, int uid) {
        ThreadManager.getInstance().runOnIOThread(()-> {
            try {
                Response<AgoraTokenBean> response = LiveManager.getInstance().getAgoraToken(hxId, channel, hxAppkey, uid);
                runOnUI(()-> {
                    if(isActive()) {
                        mView.onGetTokenSuccess(response.body().getAccessToken());
                    }
                });
            } catch (LiveException e) {
                e.printStackTrace();
                runOnUI(()-> {
                    if(isActive()) {
                        mView.onGetTokenFail(e.getDescription());
                    }
                });
            }
        });
    }
}

```

**方法介绍：**

- onStartCamera()用于主播开始直播；</br>
- switchCamera()用于切换摄像头；</br>
- leaveChannel()用于离开当前频道。</br>
- getFastToken(String hxId, String channel, String hxAppkey, int uid)用于通过app
  server获取Agora验证token。其中参数为hxId为Easemob id，channel为要加入Agora cdn直播的频道，hxAppkey为Easemob app
  key（需要注册Easemob账号申请），uid为本地用户的ID（频道内每个用户的 uid 必须是唯一的）。</br>

**注：mView为FastLiveHostFragment实现的接口类IFastHostView对象。**

（2）实现FastAudiencePresenter

```
public class FastLiveAudiencePresenterImpl extends FastAudiencePresenter {
    @Override
    public void onLiveClosed() {
        runOnUI(()-> {
            if(isActive()) {
                mView.onLiveClosed();
            }
        });
    }

    @Override
    public void leaveChannel() {
        runOnUI(()-> {
            if(isActive()) {
                mView.onLeaveChannel();
            }
        });
    }

    @Override
    public void getFastToken(String hxId, String channel, String hxAppkey, int uid) {
        ThreadManager.getInstance().runOnIOThread(()-> {
            try {
                Response<AgoraTokenBean> response = LiveManager.getInstance().getAgoraToken(hxId, channel, hxAppkey, uid);
                runOnUI(()-> {
                    if(isActive()) {
                        mView.onGetTokenSuccess(response.body().getAccessToken());
                    }
                });
            } catch (LiveException e) {
                e.printStackTrace();
                runOnUI(()-> {
                    if(isActive()) {
                        mView.onGetTokenFail(e.getDescription());
                    }
                });
            }
        });
    }
}

```

**方法介绍：**

- onLiveClosed()为收到主播退出直播；</br>
- leaveChannel()及getFastToken()与FastLiveHostPresenterImpl相同。

**注：mView为FastLiveAudienceFragment实现的接口类IFastAudienceView对象。**

**注意事项：**</br>

- 获取Agora token，需要搭建服务端逻辑。如果是为了测试，可以使用console生成临时token进行测试，或者使用不验证token模式</br>
