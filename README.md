## 简介 ##
本demo演示了通过环信及ucloud sdk实现视频直播及直播室聊天

## 怎么使用本demo ##
- 初次进入app时会进入到登录页面，没有账号可以通过注册按钮注册账号，登录成功后进入主页面
- 点击“发起直播”按钮，输入标题后，点击开始直播发起直播。为了测试方便，目前只能test1-test6这六个账号能发起直播，密码都是123456
- 其他人点击主页面相应主播图片进入播放页面，**在这个页面目前可以观看直播、群聊、发送礼物、发送弹幕等**

## 功能实现 ##



### 发起直播
**具体代码见StartLiveActivity类**

1、指定直播流id，开启预览
	
 	mSettings = new LiveSettings(this);
        if (mSettings.isOpenLogRecoder()) {
            Log2FileUtil.getInstance().setLogCacheDir(mSettings.getLogCacheDir());
            Log2FileUtil.getInstance().startLog(); //
        }

        //hardcode
        UStreamingProfile.Stream stream = new UStreamingProfile.Stream(rtmpPushStreamDomain, "ucloud/" + roomId);

        mStreamingProfile = new UStreamingProfile.Builder()
                .setVideoCaptureWidth(mSettings.getVideoCaptureWidth())
                .setVideoCaptureHeight(mSettings.getVideoCaptureHeight())
                .setVideoEncodingBitrate(mSettings.getVideoEncodingBitRate()) //UStreamingProfile.VIDEO_BITRATE_NORMAL
                .setVideoEncodingFrameRate(mSettings.getVideoFrameRate())
                .setStream(stream).build();

        UEasyStreaming.UEncodingType encodingType = UEasyStreaming.UEncodingType.MEDIA_X264;
	//        if(DeviceUtils.hasJellyBeanMr2()){
	//            encodingType = UEasyStreaming.UEncodingType.MEDIA_CODEC;
	//        }
        mEasyStreaming = new UEasyStreaming(this, encodingType);
        mEasyStreaming.setStreamingStateListener(this);
        mEasyStreaming.setAspectWithStreamingProfile(mPreviewContainer, mStreamingProfile);
2、开始直播
	
	mEasyStreaming.startRecording();

3、加入聊天室

	EMClient.getInstance().chatroomManager().joinChatRoom(roomChatId, new EMValueCallBack<EMChatRoom>() {
            @Override
            public void onSuccess(EMChatRoom emChatRoom) {
                addChatRoomChangeListenr();
                onMessageListInit();
            }

            @Override
            public void onError(int i, String s) {
                showToast("加入聊天室失败");
            }
        });

4、设置消息监听

	 EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(roomChatId)) {
                    messageView.refreshSelectLast();
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            EMMessage message = messages.get(messages.size()-1);
            if(DemoConstants.CMD_GIFT.equals(((EMCmdMessageBody)message.getBody()).action())){
                showLeftGiftVeiw(message.getFrom());

            }
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            if (isMessageListInited) {
	//                messageList.refresh();
            }
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            if (isMessageListInited) {
	//                messageList.refresh();
            }
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            if (isMessageListInited) {
                messageView.refresh();
            }
        }
    };

	EMClient.getInstance().chatManager().addMessageListener(msgListener);

4、发送消息

    EMMessage message = EMMessage.createTxtSendMessage(content, roomChatId);
    message.setChatType(EMMessage.ChatType.ChatRoom);
    EMClient.getInstance().chatManager().sendMessage(message);
    message.setMessageStatusCallback(new EMCallBack() {
        @Override
        public void onSuccess() {
			//刷新消息列表
            messageView.refreshSelectLast();
        }

        @Override
        public void onError(int i, String s) {
            showToast("消息发送失败！");
        }

        @Override
        public void onProgress(int i, String s) {

        }
    });

### 观看直播
**具体代码参考demo LiveDetailsActivity**

通过指定流id观看直播

    mVideoView = (UVideoView) findViewById(R.id.videoview);

    String liveId = getIntent().getStringExtra("liveId");
    roomChatId = getIntent().getStringExtra("chatroomId");

    mVideoView.setPlayType(UVideoView.PlayType.LIVE);
    mVideoView.setPlayMode(UVideoView.PlayMode.NORMAL);
    mVideoView.setRatio(UVideoView.VIDEO_RATIO_FILL_PARENT);
    mVideoView.setDecoder(UVideoView.DECODER_VOD_SW);

    mVideoView.registerCallback(this);
    mVideoView.setVideoPath(rtmpPlayStreamUrl + liveId);

加入聊天室及接发消息同发起直播



###发送弹幕
通过设置消息扩展字段实现，UI上根据相应扩展字段做弹幕的显示，demo这里使用的是`is_barrage_msg`即`DemoConstants.EXTRA_IS_BARRAGE_MSG`，
如果为true则为弹幕消息

	EMMessage message = EMMessage.createTxtSendMessage(content, roomChatId);
    if(messageView.isBarrageShow){
        message.setAttribute(DemoConstants.EXTRA_IS_BARRAGE_MSG, true);
    }
    message.setChatType(EMMessage.ChatType.ChatRoom);
    EMClient.getInstance().chatManager().sendMessage(message);
    message.setMessageStatusCallback(new EMCallBack() {
        @Override
        public void onSuccess() {
            //刷新消息列表
            messageView.refreshSelectLast();
        }

        @Override
        public void onError(int i, String s) {
            showToast("消息发送失败！");
        }

        @Override
        public void onProgress(int i, String s) {

        }
    });


###发送礼物

通过发送透传消息实现礼物的发送，UI上根据礼物类型做相应的显示，demo这里使用的是action为`cmd_gift`即`DemoConstants.CMD_GIFT`的透传消息，


	EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
    message.setReceipt(roomChatId);
    EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(DemoConstants.CMD_GIFT);
    message.addBody(cmdMessageBody);
    message.setChatType(EMMessage.ChatType.ChatRoom);
    EMClient.getInstance().chatManager().sendMessage(message);

这里只是做一个简单演示，礼物的类型、数量等都可以类似弹幕消息一样，通过设置扩展字段实现


> 环信及ucloud文档地址：
> [http://docs.easemob.com/im/200androidclientintegration/50singlechat](http://docs.easemob.com/im/200androidclientintegration/50singlechat)，
> [https://docs.ucloud.cn/upd-docs/ulive/index.html](https://docs.ucloud.cn/upd-docs/ulive/index.html)
