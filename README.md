## 简介 ##
**1、本直播demo展示了环信SDK提供直播聊天室的能力。除了提供基本的聊天外，还提供了赠送礼物，点赞及弹幕消息三种自定义消息，开发者可以根据自己的实际需求添加新的自定义消息。**

**2、demo的核心类为：LiveAudienceActivity（观众直播间页面）及LiveAnchorActivity（主播直播页面）。为了便于集成第三方视频直播，直播页面将直播聊天室相关逻辑抽取到了LiveAudienceFragment和LiveAnchorFragment中，视频直播的相关逻辑可以直接在activity中实现。**

**3、添加远程依赖**
>```Java
>api 'com.hyphenate:hyphenate-sdk-lite:3.6.8'
>```
集成文档：</br>
 [Android SDK 导入](http://docs-im.easemob.com/im/android/sdk/import)；</br>
 [Android SDK 更新日志](http://docs-im.easemob.com/im/android/sdk/releasenote)；
 
**4、为了便于开发者使用自定义消息，demo中将自定义消息相关的逻辑放到custom message library中。**

    开发者可以根据自己的需求对这个library进行更改。
    EmCustomMsgHelper用于监听接收自定义消息，发送自定义消息。
    EmCustomMsgType用户定义了demo中用到的自定义消息类型（礼物消息，点赞消息及弹幕消息）。
具体用法：</br>
>(1)首先在程序入口或者其他合适的地方，进行初始化。
>```Java
>EmCustomMsgHelper.getInstance().init();
>```
>(2)在直播间页面初始化时，设置房间信息。
>```Java
>EmCustomMsgHelper.getInstance().setChatRoomInfo(chatroomId);
>```
>(3)设置自定义消息监听
>```Java
>EmCustomMsgHelper.getInstance().setOnCustomMsgReceiveListener(new OnCustomMsgReceiveListener() {
>    @Override
>    public void onReceiveGiftMsg(EMMessage message) {
>        //接收到礼物消息的处理逻辑
>    }
>
>    @Override
>    public void onReceivePraiseMsg(EMMessage message) {
>        //接收到点赞消息的处理逻辑
>    }
>
>    @Override
>    public void onReceiveBarrageMsg(EMMessage message) {
>        //接收到弹幕消息的处理逻辑
>    }
>});
>```
>(4)发送自定义消息可以调用如下方法
>```Java
>//如果所传参数与library中相同，可以直接调用此方法
>public void sendGiftMsg(String giftId, int num, OnMsgCallBack callBack);        //礼物消息
>    
>public void sendPraiseMsg(int num, OnMsgCallBack callBack);                     //点赞消息
>    
>public void sendBarrageMsg(String content, OnMsgCallBack callBack);             //弹幕消息
>    
>//有其他参数或者与demo中定义的参数不同，调用此方法
>public void sendGiftMsg(Map<String, String> params, OnMsgCallBack callBack);    //礼物消息
>    
>public void sendPraiseMsg(Map<String, String> params, OnMsgCallBack callBack);  //点赞消息
>    
>public void sendBarrageMsg(Map<String, String> params, OnMsgCallBack callBack); //弹幕消息
>    
>//甚至也可以调用如下方法发送自定义消息
>public void sendCustomMsg(String event, Map<String, String> params, OnMsgCallBack callBack);
>    
>public void sendCustomMsg(String to, EMMessage.ChatType chatType, String event
>, Map<String, String> params, OnMsgCallBack callBack);
>```
>(5)解析自定义消息</br>
>a、如果发送的自定义参数与library中相同，可以直接调用如下方法，获得所传的数据
>```Java
>//获取礼物消息中礼物的id
>public String getMsgGiftId(EMMessage msg);
>//获取礼物消息中礼物的数量
>public int getMsgGiftNum(EMMessage msg);
>//获取点赞消息中点赞的数目
>public int getMsgPraiseNum(EMMessage msg);
>//获取弹幕消息中的文本内容
>public String getMsgBarrageTxt(EMMessage msg);
>```
>b、如果自定义消息参数与library中不同，可以调用如下方法，获取消息中的参数
>```Java
>public Map<String, String> getCustomMsgParams(EMMessage message);
>```
>(6)library中还提供了，判断自定义消息类型的方法
>```Java
>public boolean isGiftMsg(EMMessage msg);    //礼物消息判断
>
>public boolean isPraiseMsg(EMMessage msg);  //点赞消息判断
>
>public boolean isBarrageMsg(EMMessage msg); //弹幕消息判断
>```
**5、本直播demo采用Google推荐的架构进行开发。**
![](https://developer.android.google.cn/topic/libraries/architecture/images/final-architecture.png)
    demo中有两个repository，EmClientRepository及AppServerRepository。其中EmClientRepository用户处理环信SDK提供     的相关请求，AppServerRepository用户处理app server提供的接口。每个页面有相应的ViewModel以生命周期的方式存储和管    理与UI相关的数据。LiveData是一个具有生命周期感知特性的可观察的数据保持类，一般位于ViewModel中，用于观察数据变化。</br>

**6、集成三方视频直播说明(以七牛为例)。**</br>

demo中视频直播采用的是七牛直播SDK，集成了直播推流SDK和播放器SDK，展示了视频直播与直播聊天室结合的场景。</br>
集成七牛直播SDK步骤如下（其他直播SDK可参考）：</br>
>(1)注册七牛账号，并通过七牛官网申请并开通直播权限。</br>
>(2)从七牛直播云获取到直播推流SDK和播放器SDK，将jar包拷贝到libs目录下，so文件拷贝到jniLibs目录下。</br>
>(3)配置相关权限。如网络权限等。</br>
>(4)初始化SDK。</br>
>(5)修改布局，将SDK提供的推流及拉流View加入到布局中。demo中分别对这些View进行了继承，以便根据项目要求，做适当的修改。</br>
>(6)在LiveAnchorActivity添加推流逻辑，在LiveAudienceActivity中添加拉流逻辑。</br>

为了便于使用，demo中将七牛直播云的相关jar包，so文件及相关逻辑放到了qiniu_sdk library中，方便拆解及使用。</br>
qiuniu_sdk中PushStreamHelper作为推流的帮助类，提供了初始化及设置等逻辑，在LiveAnchorActivity通过PushStreamHelper进行使用。EncodingConfig为推流的配置类，用户可以根据自己项目的具体要求对默认配置进行修改或者设置。LiveCameraView为本地视频展示View，继承自SDK提供的GLSurfaceView类。LiveVideoView为视频播放类，继承自SDK提供的PLVideoTextureView，基本的设置逻辑也放在这个类中。</br>
注意事项：</br>

>(1)需注册七牛账号，并通过七牛官网申请并开通直播权限。</br>
>(2)SDK v3.0.0 及以后版本需要获取授权才可使用，即SDK会校验包名，需联系七牛客服获取授权。</br>
>(3)demo中推拉流地址需替换为自己的域名。</br>
## 工具要求 ##
demo中用到了Jetpack库，而使用Jetpack有如下要求：
>（1）Android Studio 3.2或更高版本。</br>
>（2）SDK targetVersion至少为26。
## 文档 ##
> 环信文档地址：
> [http://docs-im.easemob.com/im/other/integrationcases/live-chatroom](http://docs-im.easemob.com/im/other/integrationcases/live-chatroom)。
## 针对非AndroidX构建的方案 ##
**情形一、在非AndroidX构建的情况下运行demo，可进行如下工作：**</br>
>（1）注释掉demo中gradle.properties的如下设置：
>```Java
>#android.enableJetifier=true //Android 插件会通过重写现有第三方库的二进制文件，自动将这些库迁移为使用 AndroidX
>#android.useAndroidX=true    //Android 插件会使用对应的 AndroidX 库而非支持库
>```
>（2）将AndroidX构建工件替换为旧构建工件
>```Java
>dependencies {
>    ...
>    implementation "com.jakewharton:butterknife:$butterknife_version"
>    annotationProcessor "com.jakewharton:butterknife-compiler:$butterknife_version"
>    implementation 'com.google.android.material:material:1.1.0'
>    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
>    implementation "androidx.lifecycle:lifecycle-livedata:$ax_lifecycle_version"
>    implementation "androidx.lifecycle:lifecycle-viewmodel:$ax_lifecycle_version"
>    implementation "androidx.lifecycle:lifecycle-extensions:$ax_lifecycle_version"
>    implementation "androidx.room:room-runtime:$ax_room_version"
>    annotationProcessor "androidx.room:room-compiler:$ax_room_version"
>    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
>    ...
>}
>```
>修改为：
>```Java
>dependencies {
>    ...
>    implementation "com.jakewharton:butterknife:9.0.0"
>    annotationProcessor "com.jakewharton:butterknife-compiler:9.0.0"
>    implementation 'com.android.support:design:28.0.0'
>    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
>    implementation "android.arch.lifecycle:livedata:$ax_lifecycle_version"
>    implementation "android.arch.lifecycle:viewmodel:$ax_lifecycle_version"
>    implementation "android.arch.lifecycle:extensions:$ax_lifecycle_version"
>    implementation "android.arch.persistence.room:runtime:$ax_room_version"
>    annotationProcessor "android.arch.persistence.room:compiler:$ax_room_version"
>    implementation 'com.android.support:support-v4:28.0.0'
>    ...
>}
>```
>注：a、butterknife因10.0.0以上支持androidX，故需降为9.0.0。b、ax_lifecycle_version等的版本号，可以通过Android Stuido的Add Library Dependency去搜索。File ->Project Structure ->app ->Dependencies ->点击右上角添加+ ->Library  dependency ->输入要搜索的远程库名称，如 design。</br>
>如果遇到与迁移有关的问题，请参考下面这些表来确定从支持库到对应的 AndroidX 工件和类的正确映射：</br>
>[Maven 工件映射](https://developer.android.google.cn/jetpack/androidx/migrate/artifact-mappings)</br>
>[类映射](https://developer.android.google.cn/jetpack/androidx/migrate/class-mappings)</br>
>（3）全局替换androidX下的控件的引用路径及xml中的控件路径，如androidx.recyclerview.widget.RecyclerView -> android.support.v7.widget.RecyclerView。</br>
>（4）替换ViewPager2为ViewPager，参考：[Migrate from ViewPager to ViewPager2](https://developer.android.google.cn/training/animation/vp2-migration?hl=zh_cn)</br>
>（5）其他未提到的事项。</br>

**情形二、仅使用demo中的核心类**</br>

>如果只打算使用demo的核心类，建议您关注于com.easemob.livedemo.ui.live目录下相关类，核心类为LiveAnchorActivity和LiveAudienceActivity，以及他们相应的fragment。然后从这两个activity出发，逐步替换需要的类中有关androidX的控件。

