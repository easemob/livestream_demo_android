package com.easemob.livedemo.qiniu;

import android.hardware.Camera;


/**
 * Created by lw.tan on 2017/3/2.
 */

public class StreamProfileUtil {

    public static class AVOptionsHolder {

        /**
         * 默认开启的摄像头，默认为后置摄像头
         */
        public static int DefaultCameraIndex = Camera.CameraInfo.CAMERA_FACING_FRONT;

        public static int DefaultVideoCodecType = 1;

        public static int DefaultVideoCaptureOrientation = 1;

        public static int DefaultVideoBitrate = 1;

        public static int DefaultVideoCaptureFps = 20;

        public static int DefaultVideoRenderMode = 1;

        public static int DefaultAudioBitrate = 1;

        public static int DefaultAudioChannels = 1;

        public static int DefaultAudioSamplerate = 1;

//        public static UVideoProfile.Resolution DefaultVideoResolution = UVideoProfile.Resolution.RATIO_AUTO;
    }

//    public static UStreamingProfile buildDefault() {
//        return build(AVOptionsHolder.DefaultVideoCaptureFps
//                , AVOptionsHolder.DefaultVideoBitrate
//                , AVOptionsHolder.DefaultVideoResolution
//                , AVOptionsHolder.DefaultVideoCodecType
//                , AVOptionsHolder.DefaultVideoCaptureOrientation
//                , AVOptionsHolder.DefaultAudioBitrate
//                , AVOptionsHolder.DefaultAudioChannels
//                , AVOptionsHolder.DefaultAudioSamplerate
//                , AVOptionsHolder.DefaultVideoRenderMode
//                , AVOptionsHolder.DefaultCameraIndex, null);
//    }

//    public static UStreamingProfile build(AVOption profile) {
//        return build(profile.videoFramerate,
//                profile.videoBitrate,
//                UVideoProfile.Resolution.valueOf(profile.videoResolution),
//                profile.videoCodecType,
//                profile.videoCaptureOrientation,
//                profile.audioBitrate,
//                profile.audioChannels,
//                profile.audioSampleRate,
//                profile.videoFilterMode,
//                profile.cameraIndex,
//                profile.streamUrl
//                );
//    }

//    public static UStreamingProfile build(int fps,
//                                          int videoBitrate,
//                                          UVideoProfile.Resolution videoResolution,
//                                          int videoCodecType,
//                                          int captureOrientation, int audioBitrate, int audioChannels, int audioSampleRate,
//                                          int videoRenderMode,
//                                          int cameraIndex,
//                                          String streamUrl){
//        UVideoProfile videoProfile = new UVideoProfile().fps(fps)
//                .bitrate(videoBitrate)
//                .resolution(videoResolution)
//                .codecMode(videoCodecType)
//                .captureOrientation(captureOrientation);
//
//        UAudioProfile audioProfile = new UAudioProfile()
//                .bitrate(audioBitrate)
//                .channels(audioChannels)
//                .samplerate(audioSampleRate);
//
//        UFilterProfile filterProfile = new UFilterProfile().mode(videoRenderMode);
//
//        UCameraProfile cameraProfile = new UCameraProfile()
//                .setCameraIndex(cameraIndex);
//
//        UStreamingProfile streamingProfile = new UStreamingProfile.Builder()
//                .setAudioProfile(audioProfile)
//                .setVideoProfile(videoProfile)
//                .setFilterProfile(filterProfile)
//                .setCameraProfile(cameraProfile)
//                .build(streamUrl);
//        return streamingProfile;
//    }
}