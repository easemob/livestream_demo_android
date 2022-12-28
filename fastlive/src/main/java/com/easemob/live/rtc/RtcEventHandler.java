package com.easemob.live.rtc;

import android.util.Log;

import io.agora.rtc2.IRtcEngineEventHandler;

public interface RtcEventHandler {
    String TAG = "fast";

    default void onRtcWarning(int warn) {
    }


    default void onRtcError(int err) {
    }

    /**
     * The callback of the first frame of the remote video has been displayed. This call is fired when the first frame of the remote video is displayed on the view. The app can know the plot time (elapsed) in this call.
     *
     * @param uid     user ID, specifying which user's video stream
     * @param width   video stream width (pixels)
     * @param height  video stream height (pixels)
     * @param elapsed The time (in milliseconds) that has elapsed since the local user called joinChannel to join the channel until this event occurs
     */
    default void onRtcFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
        Log.e(TAG, "onRtcFirstRemoteVideoFrame uid: " + uid + " elapsed: " + elapsed);
    }

    /**
     * Added channel callback.
     *
     * @param channel channel name
     * @param uid     User ID . If the uid is specified in joinChannel, the ID is returned here; otherwise, the ID automatically assigned by the Agora server is used
     * @param elapsed Elapsed time (milliseconds) since the start of joinChannel to the occurrence of this event
     */
    void onRtcJoinChannelSuccess(String channel, int uid, int elapsed);

    /**
     * Callback when the video state of the remote user changes.
     * The callback may be inaccurate when the number of users (communication scenarios) or hosts (live scenarios) in a channel exceeds 17.
     *
     * @param uid     The remote user ID where the video state change occurred
     * @param state   Remote video stream status
     * @param reason  The specific reason for the state change of the remote video stream
     * @param elapsed The time from when the local user calls the joinChannel method to the occurrence of this event, in ms
     */
    default void onRtcRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
    }

    /**
     * The local video state changes callback.
     *
     * @param localVideoState
     * @param error
     */
    default void onRtcLocalVideoStateChanged(int localVideoState, int error) {
    }

    /**
     * Callback for current call statistics. This callback fires every two seconds during the call.
     *
     * @param stats
     */
    void onRtcStats(IRtcEngineEventHandler.RtcStats stats);

    /**
     * Network connection state changed callback
     *
     * @param state
     * @param reason
     */
    void onRtcConnectionStateChanged(int state, int reason);

    /**
     * Token service is about to expire and call back
     * If a Token is specified when calling joinChannel, since the Token has a certain time limit, if the Token is about to expire during the call,
     * The SDK will trigger this callback 30 seconds in advance to remind the App to update the Token. When receiving this callback, you need to regenerate a new Token on the server, and then call renewToken to pass the newly generated Token to the SDK.
     *
     * @param token
     */
    void onRtcTokenPrivilegeWillExpire(String token);

    /**
     * Leave the channel callback.
     * When the App calls the leaveChannel method, the SDK prompts the App to leave the channel successfully. In this callback method, the App can obtain information such as the total call duration of the call, the flow of data sent and received by the SDK, and so on.
     *
     * @param stats Call related statistics: RtcStats
     */
    default void onRtcLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
    }

    /**
     * Statistics callback for local video streams in calls.
     * This callback describes the statistics of the video stream sent by the local device, and is triggered every 2 seconds.
     *
     * @param stats
     */
    default void onRtcLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
    }

    /**
     * The statistics callback of the remote video stream in the call.
     * This callback describes the end-to-end video stream status of the remote user during the call, and is triggered every 2 seconds for each remote user/host. If there are multiple users/hosts at the remote end at the same time, this callback will be triggered multiple times every 2 seconds.
     *
     * @param stats
     */
    default void onRtcRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
    }

    /**
     * Statistics callback for local audio streams in calls. The SDK fires this callback every 2 seconds.
     *
     * @param stats
     */
    default void onRtcLocalAudioStats(IRtcEngineEventHandler.LocalAudioStats stats) {
    }

    /**
     * The statistics callback of the remote audio stream in the call.
     * This callback describes the end-to-end audio stream statistics of the remote user during the call, and is triggered every 2 seconds for each remote user/host. If there are multiple users/hosts at the remote end at the same time, this callback will be triggered multiple times every 2 seconds.
     *
     * @param stats
     */
    default void onRtcRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
    }

    /**
     * The user role has been switched callback in the live broadcast scene. Such as switching from viewer to broadcaster and vice versa.
     * This callback is triggered by the local user calling setClientRole to change the user role after joining the channel.
     *
     * @param oldRole The role before switching
     * @param newRole The role after switching
     */
    default void onRtcClientRoleChanged(int oldRole, int newRole) {
    }

    /**
     * Callback when the forwarding status of cross-channel media stream is changed.
     *
     * @param state
     * @param code
     */
    default void onRtcChannelMediaRelayStateChanged(int state, int code) {
    }

    /**
     * Cross-channel media stream forwarding event callback.
     *
     * @param code
     */
    default void onRtcChannelMediaRelayEvent(int code) {
    }

    /**
     * User volume prompt callback.
     * This callback is disabled by default. It can be turned on by the enableAudioVolumeIndication method.
     *
     * @param speakers    User volume information
     * @param totalVolume The total volume after mixing, the value range is [0,255].
     */
    default void onRtcAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
    }

    /**
     * Voice routing changed callback.
     *
     * @param routing
     */
    default void onRtcAudioRouteChanged(int routing) {
    }

    /**
     * The remote user (communication scene)/host (live broadcast scene) joins the current channel callback.
     * In the live broadcast scenario, this callback prompts a host to join the channel and returns the host's user ID. If there is already a host in the channel before joining,
     * Newly joined users will also receive a callback for existing hosts to join the channel. Agora recommends that there be no more than 17 Lianmai anchors
     * The callback will be triggered in the following cases:
     * The remote user/host calls the joinChannel method to join the channel.
     * After the remote user joins the channel, call setClientRole to change the user role to the host.
     * Rejoin the channel after the remote user/host network is interrupted.
     * The anchor successfully enters the online media stream by calling the addInjectStreamUrl method.
     *
     * @param uid     the remote user/host ID of the newly added channel
     * @param elapsed The delay (milliseconds) from when the local user calls joinChannel/setClientRole until the callback is fired
     */
    default void onRtcUserJoined(int uid, int elapsed) {
        Log.e(TAG, "onRtcUserJoined uid: " + uid + " elapsed: " + elapsed);
    }

    /**
     * Callback when the remote user (communication scene)/host (live broadcast scene) leaves the current channel.
     * Prompt that a remote user/host has left the channel (or dropped). There are two reasons for a user to leave a channel, normal leave and timeout drop:
     * <p>
     * (1) When leaving normally, the remote user/host will receive a message similar to "Goodbye". After receiving this message, it is judged that the user has left the channel
     * (2) The basis for the disconnection over time is that within a certain period of time (about 20 seconds), if the user does not receive any data packets from the other party, it is determined that the other party is disconnected.
     * In the case of poor network, there may be false positives. Agora recommends using the Realtime Messaging SDK for reliable drop detection.
     *
     * @param uid    streamer ID
     * @param reason offline reason:
     *               USER_OFFLINE_QUIT(0): The user leaves voluntarily
     *               USER_OFFLINE_DROPPED(1): Because the data packet from the other party cannot be received for a long time, the connection is dropped due to timeout. Note: Since the SDK uses an unreliable channel, it is also possible that the other party voluntarily leaves the party and does not receive the message of the other party's departure, and it is misjudged as a timeout and disconnected
     *               USER_OFFLINE_BECOME_AUDIENCE(2): In the live broadcast scenario, the user identity is switched from the host to the audience
     */
    default void onRtcUserOffline(int uid, int reason) {
        Log.e(TAG, "onRtcUserOffline uid: " + uid + " reason: " + reason);
    }
}
