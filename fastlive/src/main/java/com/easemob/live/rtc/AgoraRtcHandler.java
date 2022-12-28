package com.easemob.live.rtc;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc2.IRtcEngineEventHandler;

/**
 * Agora {@link IRtcEngineEventHandler}api reference:
 * https://docs.agora.io/cn/live-streaming/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler.html#a452db6df4938c8dd598d470a06bbccb6
 */
public class AgoraRtcHandler extends IRtcEngineEventHandler {
    private List<RtcEventHandler> mHandlers;

    public AgoraRtcHandler() {
        mHandlers = new ArrayList<>();
    }

    public void registerEventHandler(RtcEventHandler handler) {
        if (!mHandlers.contains(handler)) {
            mHandlers.add(handler);
        }
    }

    public void removeEventHandler(RtcEventHandler handler) {
        mHandlers.remove(handler);
    }

    @Override
    public void onWarning(int warn) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcWarning(warn);
        }
    }

    @Override
    public void onError(int err) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcError(err);
        }
    }


    @Override
    public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcFirstRemoteVideoFrame(uid, width, height, elapsed);
        }
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcJoinChannelSuccess(channel, uid, elapsed);
        }
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcRemoteVideoStateChanged(uid, state, reason, elapsed);
        }
    }

    @Override
    public void onLocalVideoStateChanged(int localVideoState, int error) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcLocalVideoStateChanged(localVideoState, error);
        }
    }

    @Override
    public void onRtcStats(RtcStats stats) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcStats(stats);
        }
    }

    @Override
    public void onConnectionStateChanged(int state, int reason) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcConnectionStateChanged(state, reason);
        }
    }

    @Override
    public void onTokenPrivilegeWillExpire(String token) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcTokenPrivilegeWillExpire(token);
        }
    }

    @Override
    public void onLeaveChannel(RtcStats stats) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcLeaveChannel(stats);
        }
    }

    @Override
    public void onLocalVideoStats(LocalVideoStats stats) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcLocalVideoStats(stats);
        }
    }

    @Override
    public void onRemoteVideoStats(RemoteVideoStats stats) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcRemoteVideoStats(stats);
        }
    }

    @Override
    public void onLocalAudioStats(LocalAudioStats stats) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcLocalAudioStats(stats);
        }
    }

    @Override
    public void onRemoteAudioStats(RemoteAudioStats stats) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcRemoteAudioStats(stats);
        }
    }

    @Override
    public void onClientRoleChanged(int oldRole, int newRole) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcClientRoleChanged(oldRole, newRole);
        }
    }

    @Override
    public void onChannelMediaRelayStateChanged(int state, int code) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcChannelMediaRelayStateChanged(state, code);
        }
    }

    @Override
    public void onChannelMediaRelayEvent(int code) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcChannelMediaRelayEvent(code);
        }
    }

    @Override
    public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcAudioVolumeIndication(speakers, totalVolume);
        }
    }

    @Override
    public void onAudioRouteChanged(int routing) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcAudioRouteChanged(routing);
        }
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcUserJoined(uid, elapsed);
        }
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        for (RtcEventHandler handler : mHandlers) {
            handler.onRtcUserOffline(uid, reason);
        }
    }
}
