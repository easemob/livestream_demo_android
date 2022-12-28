# Agora直播聊天室简介

## 简介

**Agora直播聊天室（以下简称直播聊天室）展示了Agora SDK提供直播聊天室的能力。除了提供基本的聊天外，还提供了赠送礼物的自定义消息，开发者可以根据自己的实际需求添加新的自定义消息。**

**核心类介绍：**

- CdnLiveAudienceActivity：观众直播间页面</br>
- CdnLiveHostActivity：主播直播页面</br>
- LiveAudienceFragment：集成观众端聊天室相关逻辑</br>
- LiveAnchorFragment：集成主播端聊天室相关逻辑</br>

**其他端开源地址：**

- iOS:    https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/livestream-ios
- Web:    https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/livestream-web
- App Server:     https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/backend

## 集成Agora Chat SDK

### 开发环境要求

- Android Studio 3.2或更高版本。</br>
- SDK targetVersion至少为26。

### 添加远程依赖

```
implementation 'io.agora.rtc:chat-sdk:1.0.6'
implementation 'io.agora.rtc:chat-uikit:1.0.6'
```

**集成文档：**</br>

- [Android SDK 集成](https://docs-preprod.agora.io/cn/agora-chat/enable_agora_chat?platform=Android)；</br>
- [Android UIKit 集成](https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android)；

## 使用AgoraChat-UIKit

**为了便于开发者使用自定义消息，Agora聊天室中将自定义消息相关的逻辑封装到AgoraChat-UIKit-android库中。**

开发者可以根据自己的需求对这个UIKit库进行更改。

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

（1）如果发送的自定义参数与UIKit中相同，可以直接调用如下方法，获得所传的数据

```Java
//获取礼物消息中礼物的id
public String getMsgGiftId(EMMessage msg);
//获取礼物消息中礼物的数量
public int getMsgGiftNum(EMMessage msg);
```

（2）如果自定义消息参数与UIKit中不同，可以调用如下方法，获取消息中的参数

```Java
public Map<String, String> getCustomMsgParams(EMMessage message);
```

#### 5. UIKit中还提供了，判断自定义消息类型的方法

```Java
public boolean isGiftMsg(EMMessage msg);    //礼物消息判断
```

## Agora直播聊天室架构介绍

![](https://developer.android.google.cn/topic/libraries/architecture/images/final-architecture.png)</br>
Agora聊天室中有两个repository，ClientRepository及AppServerRepository。其中ClientRepository用户处理Agora SDK提供
的相关请求，AppServerRepository用户处理app server提供的接口。每个页面有相应的ViewModel以生命周期的方式存储和管
理与UI相关的数据。LiveData是一个具有生命周期感知特性的可观察的数据保持类，一般位于ViewModel中，用于观察数据变化。</br>

## 集成Agora视频直播SDK

Agora聊天室提供了Agora CDN直播能力(https://docs.agora.io/cn/live-streaming/landing-page?platform=Android)。


## 运行DEMO工程

按照以下步骤运行示例项目：project:\
### 1. 克隆repo到本地。
```java
    git clone git@github.com:AgoraIO-Usecase/AgoraChat-Livestream.git
```

### 2. 使用 Android Studio 打开 Android 项目(AgoraChat-Livestream/livestream-android)。

### 3. 配置APPKEY。
在local.properties中配置AGORA_CHAT_APPKEY和AGORA_APP_ID，你可以在[Agora开发者控制台](https://console.agora.io/)申请相应的APPKEY。

```Java
AGORA_CHAT_APPKEY=*******

AGORA_APP_ID=*******
```
### 4. 使用AndroidStudio运行即可。

## 文档

- [iOS端开源地址](https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/livestream-ios)
- [Web端开源地址](https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/livestream-web)
- [App Server开源地址](https://github.com/AgoraIO-Usecase/AgoraChat-Livestream/tree/main/backend)
- [Android SDK 集成](https://docs-preprod.agora.io/cn/agora-chat/enable_agora_chat?platform=Android)</br>
- [Android UIKit 集成](https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android)

## 针对非AndroidX构建的方案 ##

### 一、在非AndroidX构建的情况下运行demo，可进行如下工作：

#### 1. 注释掉demo中gradle.properties的如下设置：

```Java
#android.enableJetifier=true //Android 插件会通过重写现有第三方库的二进制文件，自动将这些库迁移为使用 AndroidX
#android.useAndroidX=true    //Android 插件会使用对应的 AndroidX 库而非支持库
```

#### 2. 将AndroidX构建工件替换为旧构建工件

```Java
dependencies {
        ...
        implementation "com.jakewharton:butterknife:$butterknife_version"
        annotationProcessor "com.jakewharton:butterknife-compiler:$butterknife_version"
        implementation 'com.google.android.material:material:1.1.0'
        implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
        implementation "androidx.lifecycle:lifecycle-livedata:$ax_lifecycle_version"
        implementation "androidx.lifecycle:lifecycle-viewmodel:$ax_lifecycle_version"
        implementation "androidx.lifecycle:lifecycle-extensions:$ax_lifecycle_version"
        implementation "androidx.room:room-runtime:$ax_room_version"
        annotationProcessor "androidx.room:room-compiler:$ax_room_version"
        implementation 'androidx.legacy:legacy-support-v4:1.0.0'
        ...
        }
```

修改为：

```Java
dependencies {
        ...
        implementation "com.jakewharton:butterknife:9.0.0"
        annotationProcessor "com.jakewharton:butterknife-compiler:9.0.0"
        implementation 'com.android.support:design:28.0.0'
        implementation 'com.android.support.constraint:constraint-layout:1.1.3'
        implementation "android.arch.lifecycle:livedata:$ax_lifecycle_version"
        implementation "android.arch.lifecycle:viewmodel:$ax_lifecycle_version"
        implementation "android.arch.lifecycle:extensions:$ax_lifecycle_version"
        implementation "android.arch.persistence.room:runtime:$ax_room_version"
        annotationProcessor "android.arch.persistence.room:compiler:$ax_room_version"
        implementation 'com.android.support:support-v4:28.0.0'
        ...
        }
```

注：

- butterknife因10.0.0以上支持androidX，故需降为9.0.0。
- ax_lifecycle_version等的版本号，可以通过Android Stuido的Add Library Dependency去搜索。File ->Project Structure ->
  app ->Dependencies ->点击右上角添加+ ->Library dependency ->输入要搜索的远程库名称，如 design。

如果遇到与迁移有关的问题，请参考下面这些表来确定从支持库到对应的 AndroidX 工件和类的正确映射：</br>

- [Maven 工件映射](https://developer.android.google.cn/jetpack/androidx/migrate/artifact-mappings)</br>
- [类映射](https://developer.android.google.cn/jetpack/androidx/migrate/class-mappings)</br>

#### 3. 全局替换androidX下的控件的引用路径及xml中的控件路径，如androidx.recyclerview.widget.RecyclerView -> android.support.v7.widget.RecyclerView。</br>

#### 4. 替换ViewPager2为ViewPager，参考：[Migrate from ViewPager to ViewPager2](https://developer.android.google.cn/training/animation/vp2-migration?hl=zh_cn)</br>

#### 5. 其他未提到的事项。</br>

### 二、仅使用demo中的核心类

> 如果只打算使用demo的核心类，建议您关注于io.agora.livedemo.ui.cdn目录下相关类，核心类为CdnLiveHostActivity和CdnLiveAudienceActivity，以及他们相应的fragment。然后从这两个activity出发，逐步替换需要的类中有关androidX的控件。

