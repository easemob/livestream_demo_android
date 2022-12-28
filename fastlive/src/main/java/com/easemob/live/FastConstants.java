package com.easemob.live;

import io.agora.rtc2.video.BeautyOptions;
import io.agora.rtc2.video.VideoEncoderConfiguration;

public class FastConstants {
    private static final int BEAUTY_EFFECT_DEFAULT_CONTRAST = BeautyOptions.LIGHTENING_CONTRAST_NORMAL;
    private static final float BEAUTY_EFFECT_DEFAULT_LIGHTNESS = 0.7f;
    private static final float BEAUTY_EFFECT_DEFAULT_SMOOTHNESS = 0.5f;
    private static final float BEAUTY_EFFECT_DEFAULT_REDNESS = 0.1f;

    // public static final BeautyOptions DEFAULT_BEAUTY_OPTIONS = new BeautyOptions(
    //         BEAUTY_EFFECT_DEFAULT_CONTRAST,
    //         BEAUTY_EFFECT_DEFAULT_LIGHTNESS,
    //         BEAUTY_EFFECT_DEFAULT_SMOOTHNESS,
    //         BEAUTY_EFFECT_DEFAULT_REDNESS);

    public static final BeautyOptions DEFAULT_BEAUTY_OPTIONS = new BeautyOptions();

    public static VideoEncoderConfiguration.VideoDimensions[] VIDEO_DIMENSIONS = new VideoEncoderConfiguration.VideoDimensions[]{
            VideoEncoderConfiguration.VD_320x240,
            VideoEncoderConfiguration.VD_480x360,
            VideoEncoderConfiguration.VD_640x360,
            VideoEncoderConfiguration.VD_640x480,
            new VideoEncoderConfiguration.VideoDimensions(960, 540),
            VideoEncoderConfiguration.VD_1280x720
    };

    public static int[] VIDEO_MIRROR_MODES = new int[]{
            io.agora.rtc2.Constants.VIDEO_MIRROR_MODE_AUTO,
            io.agora.rtc2.Constants.VIDEO_MIRROR_MODE_ENABLED,
            io.agora.rtc2.Constants.VIDEO_MIRROR_MODE_DISABLED,
    };

    public static final String PREF_NAME = "io.agora.live.fastlive";
    public static final int DEFAULT_PROFILE_IDX = 2;
    public static final String PREF_RESOLUTION_IDX = "pref_profile_index";
    public static final String PREF_ENABLE_STATS = "pref_enable_stats";
    public static final String PREF_MIRROR_LOCAL = "pref_mirror_local";
    public static final String PREF_MIRROR_REMOTE = "pref_mirror_remote";
    public static final String PREF_MIRROR_ENCODE = "pref_mirror_encode";

    public static final String KEY_CLIENT_ROLE = "key_client_role";


    public static final String FAST_BUNDLE_CHANNEL = "channel";
    public static final String FAST_BUNDLE_ROOMID = "roomId";
    public static final String FAST_BUNDLE_HX_ID = "hxId";
    public static final String FAST_BUNDLE_HX_APPKEY = "hxAppkey";
}
