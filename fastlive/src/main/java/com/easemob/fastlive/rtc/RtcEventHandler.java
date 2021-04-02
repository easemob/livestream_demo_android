package com.easemob.fastlive.rtc;

import android.util.Log;

import io.agora.rtc.IRtcEngineEventHandler;

public interface RtcEventHandler {
    String TAG = "fast";

    /**
     * 发生警告回调。
     * @param warn
     */
    default void onRtcWarning(int warn) {}

    /**
     * 发生错误回调。
     * @param err
     */
    default void onRtcError(int err) {}

    /**
     * 已显示远端视频首帧回调。 第一帧远端视频显示在视图上时，触发此调用。App 可在此调用中获知出图时间（elapsed）。
     * @param uid       用户 ID，指定是哪个用户的视频流
     * @param width     视频流宽（像素）
     * @param height    视频流高（像素）
     * @param elapsed   从本地用户调用 joinChannel 加入频道开始到发生此事件过去的时间（毫秒）
     */
    default void onRtcFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
        Log.e(TAG, "onRtcFirstRemoteVideoFrame uid: "+uid + " elapsed: "+elapsed);
    }

    /**
     * 加入频道回调。
     * @param channel  频道名
     * @param uid      用户 ID 。如果 joinChannel 中指定了 uid，则此处返回该 ID；否则使用 Agora 服务器自动分配的 ID
     * @param elapsed  从 joinChannel 开始到发生此事件过去的时间（毫秒)
     */
    void onRtcJoinChannelSuccess(String channel, int uid, int elapsed);

    /**
     * 远端用户视频状态发生改变回调。
     * 当频道内的用户（通信场景）或主播（直播场景）的人数超过 17 时，该回调可能不准确。
     * @param uid       发生视频状态改变的远端用户 ID
     * @param state     远端视频流状态
     * @param reason    远端视频流状态改变的具体原因
     * @param elapsed   从本地用户调用 joinChannel 方法到发生本事件经历的时间，单位为 ms
     */
    default void onRtcRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {}

    /**
     * 本地视频状态发生改变回调。
     * @param localVideoState
     * @param error
     */
    default void onRtcLocalVideoStateChanged(int localVideoState, int error) {}

    /**
     * 当前通话统计回调。 该回调在通话中每两秒触发一次。
     * @param stats
     */
    void onRtcStats(IRtcEngineEventHandler.RtcStats stats);

    /**
     * 网络连接状态已改变回调
     * @param state
     * @param reason
     */
    void onRtcConnectionStateChanged(int state, int reason);

    /**
     * Token服务即将过期回调
     * 在调用 joinChannel 时如果指定了 Token，由于 Token 具有一定的时效，在通话过程中如果 Token 即将失效，
     * SDK 会提前 30 秒触发该回调，提醒 App 更新 Token。当收到该回调时，你需要重新在服务端生成新的 Token，然后调用 renewToken 将新生成的 Token 传给 SDK。
     * @param token
     */
    void onRtcTokenPrivilegeWillExpire(String token);

    /**
     * 离开频道回调。
     * App 调用 leaveChannel 方法时，SDK 提示 App 离开频道成功。 在该回调方法中，App 可以得到此次通话的总通话时长、SDK 收发数据的流量等信息。
     * @param stats 通话相关的统计信息：RtcStats
     */
    default void onRtcLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {}

    /**
     * 通话中本地视频流的统计信息回调。
     * 该回调描述本地设备发送视频流的统计信息，每 2 秒触发一次。
     * @param stats
     */
    default void onRtcLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {}

    /**
     * 通话中远端视频流的统计信息回调。
     * 该回调描述远端用户在通话中端到端的视频流状态，针对每个远端用户/主播每 2 秒触发一次。如果远端同时存在多个用户/主播，该回调每 2 秒会被触发多次。
     * @param stats
     */
    default void onRtcRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {}

    /**
     * 通话中本地音频流的统计信息回调。SDK 每 2 秒触发该回调一次。
     * @param stats
     */
    default void onRtcLocalAudioStats(IRtcEngineEventHandler.LocalAudioStats stats) {}

    /**
     * 通话中远端音频流的统计信息回调。
     * 该回调描述远端用户在通话中端到端的音频流统计信息，针对每个远端用户/主播每 2 秒触发一次。如果远端同时存在多个用户/主播，该回调每 2 秒会被触发多次。
     * @param stats
     */
    default void onRtcRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {}

    /**
     * 直播场景下用户角色已切换回调。如从观众切换为主播，反之亦然。
     * 该回调由本地用户在加入频道后调用 setClientRole 改变用户角色触发的。
     * @param oldRole 切换前的角色
     * @param newRole 切换后的角色
     */
    default void onRtcClientRoleChanged(int oldRole, int newRole) {}

    /**
     * 跨频道媒体流转发状态发生改变回调。
     * @param state
     * @param code
     */
    default void onRtcChannelMediaRelayStateChanged(int state, int code) {}

    /**
     * 跨频道媒体流转发事件回调。
     * @param code
     */
    default void onRtcChannelMediaRelayEvent(int code) {}

    /**
     * 用户音量提示回调。
     * 该回调默认禁用。可以通过 enableAudioVolumeIndication 方法开启。
     * @param speakers      用户音量信息
     * @param totalVolume   混音后的总音量，取值范围为 [0,255]。
     */
    default void onRtcAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {}

    /**
     * 语音路由已变更回调。
     * @param routing
     */
    default void onRtcAudioRouteChanged(int routing) {}

    /**
     * 远端用户（通信场景）/主播（直播场景）加入当前频道回调。
     * 直播场景下，该回调提示有主播加入了频道，并返回该主播的用户 ID。如果在加入之前，已经有主播在频道中了，
     * 新加入的用户也会收到已有主播加入频道的回调。Agora 建议连麦主播不超过 17 人
     * 该回调在如下情况下会被触发：
     * 远端用户/主播调用 joinChannel 方法加入频道。
     * 远端用户加入频道后调用 setClientRole 将用户角色改变为主播。
     * 远端用户/主播网络中断后重新加入频道。
     * 主播通过调用 addInjectStreamUrl 方法成功输入在线媒体流。
     * @param uid       新加入频道的远端用户/主播 ID
     * @param elapsed   从本地用户调用 joinChannel/setClientRole 到触发该回调的延迟（毫秒）
     */
    default void onRtcUserJoined(int uid, int elapsed) {
        Log.e(TAG, "onRtcUserJoined uid: "+uid + " elapsed: "+elapsed);
    }

    /**
     * 远端用户（通信场景）/主播（直播场景）离开当前频道回调。
     * 提示有远端用户/主播离开了频道（或掉线）。用户离开频道有两个原因，即正常离开和超时掉线：
     *
     * (1)正常离开的时候，远端用户/主播会收到类似“再见”的消息，接收此消息后，判断用户离开频道
     * (2)超时掉线的依据是，在一定时间内（约 20 秒），用户没有收到对方的任何数据包，则判定为对方掉线。
     * 在网络较差的情况下，有可能会误报。Agora 建议使用实时消息 SDK 来做可靠的掉线检测。
     * @param uid       主播 ID
     * @param reason    离线原因：
     *                  USER_OFFLINE_QUIT(0)：用户主动离开
     *                  USER_OFFLINE_DROPPED(1)：因过长时间收不到对方数据包，超时掉线。注意：由于 SDK 使用的是不可靠通道，也有可能对方主动离开本方没收到对方离开消息而误判为超时掉线
     *                  USER_OFFLINE_BECOME_AUDIENCE(2)：直播场景下，用户身份从主播切换为观众
     */
    default void onRtcUserOffline(int uid, int reason) {
        Log.e(TAG, "onRtcUserOffline uid: "+uid + " reason: "+reason);
    }
}
