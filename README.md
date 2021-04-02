# 环信直播聊天室简介

## 简介
**环信直播聊天室（以下简称环信聊天室）展示了环信SDK提供直播聊天室的能力。除了提供基本的聊天外，还提供了赠送礼物，点赞及弹幕消息三种自定义消息，开发者可以根据自己的实际需求添加新的自定义消息。**

**核心类介绍：**
- LiveAudienceActivity：观众直播间页面</br>
- LiveAnchorActivity：主播直播页面</br>
- LiveAudienceFragment：集成观众端聊天室相关逻辑</br>
- LiveAnchorFragment：集成主播端聊天室相关逻辑</br>

## 集成环信IM SDK
### 开发环境要求
- Android Studio 3.2或更高版本。</br>
- SDK targetVersion至少为26。

### 添加远程依赖

```
api 'com.hyphenate:hyphenate-chat:3.8.0'
```

**集成文档：**</br>
- [Android SDK 导入](http://docs-im.easemob.com/im/android/sdk/import)；</br>
- [Android SDK 更新日志](http://docs-im.easemob.com/im/android/sdk/releasenote)；

## 使用Custom Message Library
**为了便于开发者使用自定义消息，环信聊天室中将自定义消息相关的逻辑封装到custom message library中。**

开发者可以根据自己的需求对这个library进行更改。

### 核心类介绍
- EmCustomMsgHelper：用于监听接收自定义消息，发送自定义消息。</br>
- EmCustomMsgType：用户定义了demo中用到的自定义消息类型（礼物消息，点赞消息及弹幕消息）。


### 具体用法
#### 1. 在程序入口或者其他合适的地方，进行初始化。
```Java
EmCustomMsgHelper.getInstance().init();
```
#### 2. 在直播间页面初始化时，设置房间信息。
```Java
EmCustomMsgHelper.getInstance().setChatRoomInfo(chatroomId);
```
#### 3. 设置自定义消息监听
```Java
EmCustomMsgHelper.getInstance().setOnCustomMsgReceiveListener(new OnCustomMsgReceiveListener() {
    @Override
    public void onReceiveGiftMsg(EMMessage message) {
        //接收到礼物消息的处理逻辑
    }

    @Override
    public void onReceivePraiseMsg(EMMessage message) {
        //接收到点赞消息的处理逻辑
    }

    @Override
    public void onReceiveBarrageMsg(EMMessage message) {
        //接收到弹幕消息的处理逻辑
    }
});
```
#### 4. 发送自定义消息可以调用如下方法
```Java
//如果所传参数与library中相同，可以直接调用此方法
public void sendGiftMsg(String giftId, int num, OnMsgCallBack callBack);        //礼物消息

public void sendPraiseMsg(int num, OnMsgCallBack callBack);                     //点赞消息

public void sendBarrageMsg(String content, OnMsgCallBack callBack);             //弹幕消息

//有其他参数或者与demo中定义的参数不同，调用此方法
public void sendGiftMsg(Map<String, String> params, OnMsgCallBack callBack);    //礼物消息

public void sendPraiseMsg(Map<String, String> params, OnMsgCallBack callBack);  //点赞消息

public void sendBarrageMsg(Map<String, String> params, OnMsgCallBack callBack); //弹幕消息

//甚至也可以调用如下方法发送自定义消息
public void sendCustomMsg(String event, Map<String, String> params, OnMsgCallBack callBack);

public void sendCustomMsg(String to, EMMessage.ChatType chatType, String event
, Map<String, String> params, OnMsgCallBack callBack);
```
#### 5. 解析自定义消息</br>
（1）如果发送的自定义参数与library中相同，可以直接调用如下方法，获得所传的数据
```Java
//获取礼物消息中礼物的id
public String getMsgGiftId(EMMessage msg);
//获取礼物消息中礼物的数量
public int getMsgGiftNum(EMMessage msg);
//获取点赞消息中点赞的数目
public int getMsgPraiseNum(EMMessage msg);
//获取弹幕消息中的文本内容
public String getMsgBarrageTxt(EMMessage msg);
```
（2）如果自定义消息参数与library中不同，可以调用如下方法，获取消息中的参数
```Java
public Map<String, String> getCustomMsgParams(EMMessage message);
```
#### 6. library中还提供了，判断自定义消息类型的方法
```Java
public boolean isGiftMsg(EMMessage msg);    //礼物消息判断

public boolean isPraiseMsg(EMMessage msg);  //点赞消息判断

public boolean isBarrageMsg(EMMessage msg); //弹幕消息判断
```
## 环信直播聊天室架构介绍
![](https://developer.android.google.cn/topic/libraries/architecture/images/final-architecture.png)</br>
环信聊天室中有两个repository，EmClientRepository及AppServerRepository。其中EmClientRepository用户处理环信SDK提供     的相关请求，AppServerRepository用户处理app server提供的接口。每个页面有相应的ViewModel以生命周期的方式存储和管    理与UI相关的数据。LiveData是一个具有生命周期感知特性的可观察的数据保持类，一般位于ViewModel中，用于观察数据变化。</br>

## 集成视频直播SDK
环信聊天室提供了两种直播类型：**传统直播**和**极速直播**

#### [极速直播](https://github.com/easemob/livestream_demo_android/blob/master/fastlive/fastlive.md)
直播延时小于3秒，适用于直播答题、互动大班课等对延迟有较高要求的场景。</br>
环信聊天室在极速直播中以集成声网极速直播为例，展示了集成极速直播与聊天室结合的场景。</br>
声网极速直播集成文档：[声网极速直播](https://github.com/easemob/livestream_demo_android/blob/master/fastlive/fastlive.md)


#### [传统直播](https://github.com/easemob/livestream_demo_android/blob/master/qiniu_sdk/qiniu.md)
单直播间可达百万观众，适合弱互动，对延迟没有要求的场景。</br>
环信聊天室在传统直播中以集成七牛直播SDK为例，展示了集成传统直播与聊天室结合的场景。</br>
七牛直播集成文档：[七牛直播](https://github.com/easemob/livestream_demo_android/blob/master/qiniu_sdk/qiniu.md)

## 文档
- [环信直播聊天室集成介绍](http://docs-im.easemob.com/im/other/integrationcases/live-chatroom)
- [环信Android SDK 导入](http://docs-im.easemob.com/im/android/sdk/import)
- [声网极速直播快速集成](https://docs.agora.io/cn/live-streaming/start_live_standard_android?platform=Android)
- [七牛Android播放端SDK介绍](https://developer.qiniu.com/pili/1210/the-android-client-sdk)
- [七牛Android推流端SDK介绍](https://developer.qiniu.com/pili/3718/PLDroidMediaStreaming-quick-start)


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
- ax_lifecycle_version等的版本号，可以通过Android Stuido的Add Library Dependency去搜索。File ->Project Structure ->app ->Dependencies ->点击右上角添加+ ->Library  dependency ->输入要搜索的远程库名称，如 design。

如果遇到与迁移有关的问题，请参考下面这些表来确定从支持库到对应的 AndroidX 工件和类的正确映射：</br>
- [Maven 工件映射](https://developer.android.google.cn/jetpack/androidx/migrate/artifact-mappings)</br>
- [类映射](https://developer.android.google.cn/jetpack/androidx/migrate/class-mappings)</br>

#### 3. 全局替换androidX下的控件的引用路径及xml中的控件路径，如androidx.recyclerview.widget.RecyclerView -> android.support.v7.widget.RecyclerView。</br>
#### 4. 替换ViewPager2为ViewPager，参考：[Migrate from ViewPager to ViewPager2](https://developer.android.google.cn/training/animation/vp2-migration?hl=zh_cn)</br>
#### 5. 其他未提到的事项。</br>

### 二、仅使用demo中的核心类

>如果只打算使用demo的核心类，建议您关注于com.easemob.livedemo.ui.live目录下相关类，核心类为LiveAnchorActivity和LiveAudienceActivity，以及他们相应的fragment。然后从这两个activity出发，逐步替换需要的类中有关androidX的控件。

