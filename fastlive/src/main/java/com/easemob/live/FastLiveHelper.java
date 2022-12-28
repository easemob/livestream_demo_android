package com.easemob.live;

import static io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15;
import static io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_640x360;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.SurfaceView;

import com.easemob.live.rtc.AgoraRtcHandler;
import com.easemob.live.rtc.RtcEventHandler;
import com.easemob.live.stats.StatsManager;
import com.easemob.live.widgets.VideoGridContainer;

import io.agora.mediaplayer.IMediaPlayer;
import io.agora.mediaplayer.IMediaPlayerObserver;
import io.agora.mediaplayer.data.PlayerUpdatedInfo;
import io.agora.mediaplayer.data.SrcInfo;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.DirectCdnStreamingError;
import io.agora.rtc2.DirectCdnStreamingMediaOptions;
import io.agora.rtc2.DirectCdnStreamingState;
import io.agora.rtc2.DirectCdnStreamingStats;
import io.agora.rtc2.IDirectCdnStreamingEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

public class FastLiveHelper {
    private static final String TAG = "lives";
    private Context mContext;
    private AgoraEngine mAgoraEngine;
    private StatsManager mStatsManager;
    private EngineConfig mGlobalConfig;
    private boolean isPaused;
    private boolean isLiving;
    private int lastUid = -1;
    private final VideoEncoderConfiguration encoderConfiguration = new VideoEncoderConfiguration(
            VD_640x360,
            FRAME_RATE_FPS_15,
            700,
            ORIENTATION_MODE_FIXED_PORTRAIT
    );
    private IMediaPlayer mMediaPlayer;
    private Runnable pendingDirectCDNStoppedRun = null;
    private final IDirectCdnStreamingEventHandler iDirectCdnStreamingEventHandler = new IDirectCdnStreamingEventHandler() {
        @Override
        public void onDirectCdnStreamingStateChanged(DirectCdnStreamingState directCdnStreamingState, DirectCdnStreamingError directCdnStreamingError, String s) {
            Log.d(TAG, String.format("Stream Publish(DirectCdnStreaming): onDirectCdnStreamingStateChanged directCdnStreamingState=%s directCdnStreamingError=%s", directCdnStreamingState.toString(), directCdnStreamingError.toString()));
            switch (directCdnStreamingState) {
                case STOPPED:
                    if (pendingDirectCDNStoppedRun != null) {
                        pendingDirectCDNStoppedRun.run();
                        pendingDirectCDNStoppedRun = null;
                    }
                    break;
            }
        }

        @Override
        public void onDirectCdnStreamingStats(DirectCdnStreamingStats directCdnStreamingStats) {

        }
    };

    private static class FastLiveHelperInstance {
        private static final FastLiveHelper instance = new FastLiveHelper();
    }

    private FastLiveHelper() {
    }

    public static FastLiveHelper getInstance() {
        return FastLiveHelperInstance.instance;
    }

    /**
     * initialize
     * (1) Initialize the object {@link AgoraEngine}, and set {@link RtcEngine#setChannelProfile(int)} to the live mode in it {@link Constants#CHANNEL_PROFILE_LIVE_BROADCASTING}
     * (2) Initialize the object {@link StatsManager}, which is used to display various status information
     * (3) Initialize the configuration object {@link EngineConfig}
     *
     * @param context
     * @param appId
     */
    public void init(Context context, String appId) {
        this.mContext = context;
        mAgoraEngine = new AgoraEngine(context, appId);
        mStatsManager = new StatsManager();
        mGlobalConfig = new EngineConfig();
        mGlobalConfig.setAppId(appId);
    }

    /**
     * Whether to display video parameters and other status on the live page
     *
     * @param showStats
     */
    public void showVideoStats(boolean showStats) {
        mStatsManager.enableStats(showStats);
    }

    /**
     * Get live broadcast status management class
     *
     * @return
     */
    public StatsManager getStatsManager() {
        return mStatsManager;
    }

    /**
     * get rtcEngine
     *
     * @return
     */
    public RtcEngine rtcEngine() {
        return mAgoraEngine != null ? mAgoraEngine.rtcEngine() : null;
    }

    /**
     * Get the configuration in the fast live broadcast
     *
     * @return
     */
    public EngineConfig getEngineConfig() {
        return mGlobalConfig;
    }

    public SharedPreferences getFastSPreferences() {
        return FastPrefManager.getPreferences(mContext);
    }

    public void registerRtcHandler(RtcEventHandler handler) {
        mAgoraEngine.registerRtcHandler(handler);
    }


    public void removeRtcHandler(RtcEventHandler handler) {
        mAgoraEngine.removeRtcHandler(handler);
    }

    public void onResume() {
        if (isPaused) {
            isPaused = false;
            isLiving = true;
            setVideoMuted(false);
            setAudioMuted(false);
        }
    }


    public void onPause() {
        if (isLiving) {
            isLiving = false;
            isPaused = true;
            setVideoMuted(true);
            setAudioMuted(true);
        }
    }

    public void onDestroy(RtcEventHandler handler) {
        Log.i("fast", "removeRtcHandler and leave channel");
        removeRtcHandler(handler);
        rtcEngine().stopPreview();
        rtcEngine().leaveChannel();
    }


    public void setClientRole(int role) {
        if (role == Constants.CLIENT_ROLE_AUDIENCE) {
            // ClientRoleOptions clientRoleOptions = new ClientRoleOptions();
            // clientRoleOptions.audienceLatencyLevel = getEngineConfig().isLowLatency() ? Constants.AUDIENCE_LATENCY_LEVEL_ULTRA_LOW_LATENCY : Constants.AUDIENCE_LATENCY_LEVEL_LOW_LATENCY;
            rtcEngine().setClientRole(role);
        } else {
            rtcEngine().setClientRole(role);
        }
    }

    /**
     * join channel
     *
     * @param role  user role
     * @param token Pass in the Token used for authentication. Tokens are generally generated on your server side.
     * @param uid   uid is the ID of the local user. The data type is integer, and the uid of each user within the channel must be unique.
     *              If the uid is set to 0, the SDK will automatically assign a uid and report it in the {@link AgoraRtcHandler#onJoinChannelSuccess(String, int, int)} callback.
     */
    public void joinRtcChannel(int role, String token, int uid) {
        setClientRole(role);
        rtcEngine().enableVideo();
        configVideo();
        // rtcEngine().joinChannel(token, getEngineConfig().getChannelName(), null, uid);
        ChannelMediaOptions channelMediaOptions = new ChannelMediaOptions();
        channelMediaOptions.publishAudioTrack = true;
        channelMediaOptions.publishCameraTrack = true;
        channelMediaOptions.clientRoleType = role;
        rtcEngine().joinChannel(token, getEngineConfig().getChannelName(), uid, channelMediaOptions);
    }

    /**
     * update token
     *
     * @param token
     */
    public void renewRtcToken(String token) {
        rtcEngine().renewToken(token);
    }

    private void configVideo() {
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(
                FastConstants.VIDEO_DIMENSIONS[getEngineConfig().getVideoDimenIndex()],
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        );
        // configuration.mirrorMode = FastConstants.VIDEO_MIRROR_MODES[getEngineConfig().getMirrorEncodeIndex()];
        rtcEngine().setVideoEncoderConfiguration(configuration);
    }

    /**
     * start live stream
     *
     * @param container
     */
    public void startBroadcast(VideoGridContainer container, int uid) {
        rtcEngine().enableAudio();
        rtcEngine().enableVideo();
        setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        SurfaceView surfaceView = prepareRtcVideo(uid, true);
        surfaceView.setZOrderMediaOverlay(true);
        container.addUserVideoSurface(uid, surfaceView, true);
        rtcEngine().startPreview();
        isLiving = true;
    }

    public void startCdnBroadcast(VideoGridContainer container, int uid, String url, IDirectCdnStreamingEventHandler handler) {
        rtcEngine().enableAudio();
        rtcEngine().enableVideo();
        setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        SurfaceView surfaceView = prepareRtcVideo(uid, true);
        surfaceView.setZOrderMediaOverlay(true);
        container.addUserVideoSurface(uid, surfaceView, true);
        rtcEngine().startPreview();

        rtcEngine().setDirectCdnStreamingVideoConfiguration(encoderConfiguration);
        DirectCdnStreamingMediaOptions directCdnStreamingMediaOptions = new DirectCdnStreamingMediaOptions();
        directCdnStreamingMediaOptions.publishCameraTrack = true;
        directCdnStreamingMediaOptions.publishMicrophoneTrack = true;
        Log.i(TAG, "startCdnBroadcast url=" + url);
        rtcEngine().startDirectCdnStreaming(iDirectCdnStreamingEventHandler, url, directCdnStreamingMediaOptions);
        isLiving = true;
    }

    public void startPullCdn(VideoGridContainer container, int uid, String url) {
        setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        rtcEngine().enableAudio();
        rtcEngine().enableVideo();
        if (mMediaPlayer == null) {
            mMediaPlayer = rtcEngine().createMediaPlayer();
            mMediaPlayer.registerPlayerObserver(new IMediaPlayerObserver() {
                @Override
                public void onPlayerStateChanged(io.agora.mediaplayer.Constants.MediaPlayerState mediaPlayerState, io.agora.mediaplayer.Constants.MediaPlayerError mediaPlayerError) {
                    // Log.d(TAG, "MediaPlayer onPlayerStateChanged -- url=" + mMediaPlayer.getPlaySrc() + "state=" + mediaPlayerState + ", error=" + mediaPlayerError);
                    if (mediaPlayerState == io.agora.mediaplayer.Constants.MediaPlayerState.PLAYER_STATE_OPEN_COMPLETED) {
                        if (mMediaPlayer != null) {
                            mMediaPlayer.play();
                        }
                    }
                }

                @Override
                public void onPositionChanged(long l) {

                }

                @Override
                public void onPlayerEvent(io.agora.mediaplayer.Constants.MediaPlayerEvent mediaPlayerEvent, long l, String s) {

                }

                @Override
                public void onMetaData(io.agora.mediaplayer.Constants.MediaPlayerMetadataType mediaPlayerMetadataType, byte[] bytes) {

                }

                @Override
                public void onPlayBufferUpdated(long l) {

                }

                @Override
                public void onPreloadEvent(String s, io.agora.mediaplayer.Constants.MediaPlayerPreloadEvent mediaPlayerPreloadEvent) {

                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onAgoraCDNTokenWillExpire() {

                }

                @Override
                public void onPlayerSrcInfoChanged(SrcInfo srcInfo, SrcInfo srcInfo1) {

                }

                @Override
                public void onPlayerInfoUpdated(PlayerUpdatedInfo playerUpdatedInfo) {

                }

                @Override
                public void onAudioVolumeIndication(int volume) {

                }

            });
            rtcEngine().setDefaultAudioRoutetoSpeakerphone(true);
        }
        mMediaPlayer.stop();
        mMediaPlayer.openWithAgoraCDNSrc(url, uid);

        if (lastUid != -1 && lastUid != uid) {
            removeRemoteVideo(lastUid, container);
        }
        lastUid = uid;
        if (!container.containUid(uid)) {
            SurfaceView surface = RtcEngine.CreateRendererView(mContext);
            rtcEngine().setupLocalVideo(new VideoCanvas(surface, Constants.RENDER_MODE_HIDDEN,
                    Constants.VIDEO_MIRROR_MODE_AUTO,
                    Constants.VIDEO_SOURCE_MEDIA_PLAYER,
                    mMediaPlayer.getMediaPlayerId(),
                    uid
            ));
            //container.addUserVideoSurface(uid, surface, false);
        }
    }

    public void stopPullCdn() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public void stopDirectCDNStreaming(Runnable onDirectCDNStopped) {
        if (rtcEngine() == null) {
            return;
        }
        rtcEngine().disableAudio();
        rtcEngine().disableVideo();
        pendingDirectCDNStoppedRun = onDirectCDNStopped;
        rtcEngine().stopDirectCdnStreaming();
    }

    /**
     * Set whether to stop streaming video
     *
     * @param muted
     */
    public void setVideoMuted(boolean muted) {
        rtcEngine().muteLocalVideoStream(muted);
        getEngineConfig().setVideoMuted(muted);
    }

    /**
     * Set whether to mute
     *
     * @param muted
     */
    public void setAudioMuted(boolean muted) {
        rtcEngine().muteLocalAudioStream(muted);
        getEngineConfig().setAudioMuted(muted);
    }

    /**
     * setup remote video view
     *
     * @param uid remote user UID
     */
    public void setupRemoteVideo(int uid, VideoGridContainer container) {
        SurfaceView surface = prepareRtcVideo(uid, false);
        container.addUserVideoSurface(uid, surface, false);
    }

    /**
     * Show only one streamer view
     *
     * @param uid
     * @param container
     * @param onlyOne
     */
    public void setupRemoteVideo(int uid, VideoGridContainer container, boolean onlyOne) {
        if (onlyOne) {
            if (lastUid != -1 && lastUid != uid) {
                removeRemoteVideo(lastUid, container);
            }
            if (!container.containUid(uid)) {
                setupRemoteVideo(uid, container);
            }
            lastUid = uid;
        } else {
            setupRemoteVideo(uid, container);
        }
    }

    /**
     * Remove remote host view
     *
     * @param uid
     * @param container
     */
    public void removeRemoteVideo(int uid, VideoGridContainer container) {
        removeRtcVideo(uid, false);
        container.removeUserVideo(uid, false);
    }


    public void switchCamera() {
        rtcEngine().switchCamera();
    }

    /**
     * prepare rtc live stream
     *
     * @param uid
     * @param local
     * @return
     */
    public SurfaceView prepareRtcVideo(int uid, boolean local) {
        SurfaceView surface = RtcEngine.CreateRendererView(mContext);
        if (local) {
            rtcEngine().setupLocalVideo(
                    // new VideoCanvas(
                    //         surface,
                    //         VideoCanvas.RENDER_MODE_HIDDEN,
                    //         0,
                    //         FastConstants.VIDEO_MIRROR_MODES[getEngineConfig().getMirrorLocalIndex()]
                    // )
                    new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, uid)
            );
        } else {
            rtcEngine().setupRemoteVideo(
                    // new VideoCanvas(
                    //         surface,
                    //         VideoCanvas.RENDER_MODE_HIDDEN,
                    //         uid,
                    //         FastConstants.VIDEO_MIRROR_MODES[getEngineConfig().getMirrorRemoteIndex()]
                    // )
                    new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, uid)
            );
        }
        return surface;
    }

    /**
     * remove rtc video
     *
     * @param uid
     * @param local
     */
    public void removeRtcVideo(int uid, boolean local) {
        if (local) {
            rtcEngine().setupLocalVideo(null);
        } else {
            rtcEngine().setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }
    }

    /**
     * This method is called when the process ends, or when needed
     */
    public void release() {
        mAgoraEngine.release();
    }
}

