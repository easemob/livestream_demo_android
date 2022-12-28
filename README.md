# 环信直播聊天室简介

## 简介

**环信直播聊天室（以下简称直播聊天室）展示了环信 IM SDK直播聊天室的能力。除了提供基本的聊天外，还提供了赠送礼物的自定义消息，开发者可以根据自己的实际需求添加新的自定义消息。**

**核心类介绍：**

- CdnLiveAudienceActivity：观众直播间页面</br>
- CdnLiveHostActivity：主播直播页面</br>
- LiveAudienceFragment：集成观众端聊天室相关逻辑</br>
- LiveAnchorFragment：集成主播端聊天室相关逻辑</br>

**其他端开源地址：**

- iOS:    https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/livestream-ios
- Web:    https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/livestream-web
- App Server:     https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/backend

## 集成环信 Chat SDK

### 开发环境要求

- Android Studio 3.6或更高版本。</br>
- SDK targetVersion至少为26。

### 添加远程依赖

```
implementation 'io.hyphenate:hyphenate-chat:3.9.9'
implementation 'io.hyphenate:ease-im-kit:3.9.9'
```
可通过一下地址查看最新SDK版本号：
- [hyphenate-chat](https://search.maven.org/search?q=g:io.hyphenate%20AND%20a:hyphenate-chat)；</br>
- [ease-im-kit](https://search.maven.org/search?q=g:io.hyphenate%20AND%20a:ease-im-kit)；

**集成文档：**</br>

- [Android SDK 集成](http://docs-im-beta.easemob.com/document/android/quickstart.html)；</br>
- [Android UIKit 集成](http://docs-im-beta.easemob.com/document/android/easeimkit.html)；

## 使用环信聊天室

为方便开发者使用，项目中将聊天室相关逻辑放到了`chatroom` library 中，开发者可以根据自己的需求对这个库进行更改。

### 核心类介绍

- EaseChatRoomMessagesView：直播聊天室消息UI，用户可以自定义相关属性设置UI显示。</br>
- EaseLiveMessageHelper：用于监听接收自定义消息，发送自定义消息。</br>
- EaseLiveMessageType：用户定义了demo中用到的自定义消息类型（礼物消息）。</br>
- OnLiveMessageListener：监听聊天室消息接受相关事件。

### 具体用法

#### 1. 在聊天室加载后，进行初始化，设置房间信息。

```Java
EaseLiveMessageHelper.getInstance().init(chatroomId);
```

#### 2. 增加和取消聊天室自定义消息监听

```Java
EaseLiveMessageHelper.getInstance().addLiveMessageListener(new OnLiveMessageListener() {
    @Override
    public void onGiftMessageReceived(EMMessage message) {
        
    }
});
EaseLiveMessageHelper.getInstance().removeLiveMessageListener(this);
```

#### 3. 发送聊天室消息可以调用如下方法

```Java
public void sendTxtMsg(String content, OnSendLiveMessageCallBack callBack);                                                       //文本消息

public void sendGiftMsg(String chatRoomId, String giftId, int num, OnSendLiveMessageCallBack callBack);                           //礼物消息

public void sendCustomMsg(String chatRoomId, String event, Map<String, String> params, final OnSendLiveMessageCallBack callBack); //自定义消息
```

#### 4. 解析消息相关参数</br>

（1）如果发送的自定义参数与library中相同，可以直接调用如下方法，获得所传的数据

```Java
//获取礼物消息中礼物的id
public String getMsgGiftId(EMMessage msg);
//获取礼物消息中礼物的数量
public int getMsgGiftNum(EMMessage msg);
```

（2）如果自定义消息参数与library中不同，可以调用如下方法，获取消息中的参数

```Java
public Map<String, String> getCustomMsgParams(EMMessage message);
```

#### 5. library中还提供了，判断自定义消息类型的方法

```Java
public boolean isGiftMsg(EMMessage msg);    //礼物消息判断
```

## 环信直播聊天室架构介绍

![](https://developer.android.google.cn/topic/libraries/architecture/images/final-architecture.png)</br>
环信聊天室中有两个repository，ClientRepository及AppServerRepository。其中ClientRepository用户处理环信 SDK提供
的相关请求，AppServerRepository用户处理app server提供的接口。每个页面有相应的ViewModel以生命周期的方式存储和管
理与UI相关的数据。LiveData是一个具有生命周期感知特性的可观察的数据保持类，一般位于ViewModel中，用于观察数据变化。</br>

## 集成Agora视频直播SDK

环信聊天室提供了Agora CDN直播能力(https://docs.agora.io/cn/live-streaming/landing-page?platform=Android)。


## 运行DEMO工程

按照以下步骤运行示例项目：project:\
### 1. 克隆repo到本地。
```java
    git clone git@github.com:easemob/livestream_demo_android.git
```

### 2. 使用 Android Studio 打开 Android 项目(livestream_demo_android)。

### 3. 配置APPKEY。
在local.properties中配置 `CHAT_APPKEY` 和 `AGORA_APP_ID`，你可以在[开发者控制台](https://console.easemob.com/user/login/)申请相应的APPKEY。

```Java
CHAT_APPKEY=*******

AGORA_APP_ID=*******
```
### 4. 使用AndroidStudio运行即可。

## 文档

- [iOS端开源地址](https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/livestream-ios)
- [Web端开源地址](https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/livestream-web)
- [App Server开源地址](https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/backend)
- [Android SDK 集成](https://docs-preprod.agora.io/cn/agora-chat/enable_agora_chat?platform=Android)</br>
- [Android UIKit 集成](https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android)

