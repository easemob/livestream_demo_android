# Integrated CDN live streaming

The **CDN live streaming** in the Agora live chat room (hereinafter referred to as the Agora chat room) adopts the Agora CDN live streaming solution, which shows the combination of video live streaming and live streaming chat room. </br>

**
Agora CDN live streaming official integration documentation: **[CDN live streaming](https://docs.agora.io/cn/live-streaming/start_live_standard_android?platform=Android)</br>

## Integrate Agora CDN Live Streaming Steps

### Integration steps

> (1) Register an Agora account, create a project and obtain an App ID. </br>
> (2) Integrate the Agora CDN Live SDK: JCenter automatically integrates or manually copies the SDK files. </br>
> (3) Configure relevant permissions. Such as network permissions, etc. </br>
> (4) Initialize SDK. Usually placed in the program entry. </br>
> (5) Set the channel scene and set the user role. </br>
> (6) Join the channel, the host sets the local view to start the live streaming, and the audience sets the remote video to watch the live streaming. </br>

For ease of use, the Agora CDN live streaming dependencies and related auxiliary classes are placed in the fastlive library in the Agora chat room for the convenience of developers. </br>

### Core class introduction

- FastLiveHelper is a helper class for CDN live streaming, which provides initialization and setup logic. </br>
- FastLiveHostFragment The anchor live fragment, which encapsulates the relevant logic of the anchor live streaming. Through the implementation class of FastHostPresenter, functions such as pushing the stream and leaving the channel can be operated from the outside. </br>
-
FastLiveAudienceFragment viewers watch the live streaming fragment, which encapsulates the relevant logic for viewers to watch the live streaming. Through the implementation class of FastAudiencePresenter, you can close the live streaming, leave the channel, etc. from the outside. </br>

When developers use it, they can add the corresponding fragment to the corresponding activity and provide the implementation class of the corresponding presenter.

## Example:

Take the use of fastlive library as an example.

### 1. Add project permissions

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

**Note: Microphone, camera and storage permissions need to be applied for, please apply where you need to use them. **

### 2. Initialization

```
private void initAgora() {
  //The second parameter is agora appId
  FastLiveHelper.getInstance().init(this, BuildConfig.AGORA_APP_ID);
}
```

### 3. Add FastLiveHostFragment or LiveAudienceFragment in the activity

(1) Add FastLiveHostFragment to FastLiveHostActivity

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

(2) Add FastLiveAudienceFragment to FastLiveAudienceActivity

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

### 4. Integrate and implement FastHostPresenter and FastAudiencePresenter respectively

(1) Implement FastHostPresenter

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

**Method introduction:**

- onStartCamera() is used by the host to start the live streaming;</br>
- switchCamera() is used to switch cameras;</br>
- leaveChannel() is used to leave the current channel. </br>
- getFastToken(String hxId, String channel, String hxAppkey, int uid) is used to pass the app
  The server obtains the Agora authentication token. The parameter is hxId is Easemob id, channel is the channel to join Agora cdn live streaming, hxAppkey is Easemob app
  key (need to register Easemob account application), uid is the ID of the local user (the uid of each user in the channel must be unique). </br>

**Note: mView is the interface class IFastHostView object implemented by FastLiveHostFragment. **

(2) Implement FastAudiencePresenter

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

**Method introduction:**

- onLiveClosed() exits the live streaming after receiving the host;</br>
- leaveChannel() and getFastToken() are the same as FastLiveHostPresenterImpl.

**Note: mView is the interface class IFastAudienceView object implemented by FastLiveAudienceFragment. **

**Note:**</br>

- To obtain Agora token, you need to build server-side logic. If it is for testing, you can use the console to generate a temporary token for testing, or use the unverified token mode</br>
