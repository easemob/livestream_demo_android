## 简介 ##
**1、本直播demo展示了环信SDK提供直播聊天室的能力。除了提供基本的聊天外，还提供了赠送礼物，点赞及弹幕消息三种自定义消息，开发者可以根据自己的实际需求添加新的自定义消息。**

**2、本demo主要包含直播列表页面（LivingListFragment及LiveListFragment），观众直播间页面（LiveAudienceActivity）及主播直播页面（LiveAnchorActivity）。为了便于集成第三方视频直播，直播页面将直播聊天室相关逻辑抽取到了LiveAudienceFragment和LiveAnchorFragment中，视频直播的相关逻辑可以直接在activity中实现。**

**3、为了便于开发者使用自定义消息，demo中将自定义消息相关的逻辑放到custom message library中。**

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
**4、本直播demo采用Google推荐的架构进行开发。**
![](https://developer.android.google.cn/topic/libraries/architecture/images/final-architecture.png)
    demo中有两个repository，EmClientRepository及AppServerRepository。其中EmClientRepository用户处理环信SDK提供     的相关请求，AppServerRepository用户处理app server提供的接口。每个页面有相应的ViewModel以生命周期的方式存储和管    理与UI相关的数据。LiveData是一个具有生命周期感知特性的可观察的数据保持类，一般位于ViewModel中，用于观察数据变化。
## 工具要求 ##
demo中用到了Jetpack库，而使用Jetpack有如下要求：
>（1）Android Studio 3.2或更高版本。</br>
>（2）如果使用Androidx官方建议使用支持库的最终版本：版本28.0.0。需要SDK targetVersion至少为26。
## 文档 ##
> 环信文档地址：
> [http://docs-im.easemob.com/im/other/integrationcases/live-chatroom](http://docs-im.easemob.com/im/other/integrationcases/live-chatroom)。
