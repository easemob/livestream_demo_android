package com.easemob.qiniu_sdk;

import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.WatermarkSetting;

import java.io.Serializable;

/**
 * 推流相关设置
 */
public class EncodingConfig implements Serializable {
    public AVCodecType mCodecType = AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC;
    public boolean mIsAudioOnly = false;

    // 是否对视频质量进行预设
    public boolean mIsVideoQualityPreset = true;
    // 设置视频质量
    public int mVideoQualityPreset = StreamingProfile.VIDEO_QUALITY_HIGH1;
    // 当需要自定义 video 的 fps、bitrate、profile 或者 audio 的 sample rate、bitrate，可以通过 AVProfile 设置
    // fps
    public int mVideoQualityCustomFPS = 20;
    // video bitrate
    public int mVideoQualityCustomBitrate = 1000;
    // maxKeyFrameInterval
    public int mVideoQualityCustomMaxKeyFrameInterval = 60;
    public StreamingProfile.H264Profile mVideoQualityCustomProfile = StreamingProfile.H264Profile.HIGH;

    // 是否预设视频encoding size
    public boolean mIsVideoSizePreset;
    // encoding size编码时候的 size，即播放端不做处理时候看到视频的 size
    // 使用内置的 encoding size level
    public int mVideoSizePreset = StreamingProfile.VIDEO_ENCODING_HEIGHT_480;

    // 设定一个 encoding size 偏好值
    public int mVideoSizeCustomWidth = 480;
    public int mVideoSizeCustomHeight = 848;

    // 是否修改视频方向
    public boolean mVideoOrientationPortrait;

    // 是否质量优先
    public boolean mVideoRateControlQuality;
    // 默认质量优先
    public StreamingProfile.EncoderRCModes mEncoderRCMode = StreamingProfile.EncoderRCModes.BITRATE_PRIORITY;

    // 自适应码率，sdk默认是关闭的
    public StreamingProfile.BitrateAdjustMode mBitrateAdjustMode = StreamingProfile.BitrateAdjustMode.Auto;
    // 控制自适应码率调节的范围
    public int mAdaptiveBitrateMin = -1;
    public int mAdaptiveBitrateMax = -1;

    public boolean mVideoFPSControl;

    // 水印相关设置，若要设置水印，可以设置默认值
    public boolean mIsWatermarkEnabled;
    public int mWatermarkAlpha;
    public WatermarkSetting.WATERMARK_SIZE mWatermarkSize;
    public int mWatermarkCustomWidth;
    public int mWatermarkCustomHeight;
    public boolean mIsWatermarkLocationPreset;
    public WatermarkSetting.WATERMARK_LOCATION mWatermarkLocationPreset;
    public float mWatermarkLocationCustomX;
    public float mWatermarkLocationCustomY;

    // 是否设置推流图片
    public boolean mIsPictureStreamingEnabled = true;
    // 加载时的展位图片地址
    public String mPictureStreamingFilePath;

    // 是否对音频质量进行预设
    public boolean mIsAudioQualityPreset;
    public int mAudioQualityPreset = StreamingProfile.AUDIO_QUALITY_MEDIUM2;
    // 当需要自定义 audio 的 sample rate、bitrate，可以通过 AVProfile 设置
    public int mAudioQualityCustomSampleRate = 44100;
    // 如果设置为48则设置时为 48 * 1024
    public int mAudioQualityCustomBitrate = 48;

    // 当图像采集尺寸与推流尺寸不一致时，SDK 会对采集图像进行 resize 操作，通过 FilterMode 参数，可以对 resize 算法进行设置
    // 该参数只对软编和硬编 YUV 有效
    public StreamingProfile.YuvFilterMode mYuvFilterMode;

    // 以下为设置camera相关设置
    // 是否使用前置摄像头
    public boolean mFrontFacing = true;

    public CameraStreamingSetting.PREVIEW_SIZE_LEVEL mSizeLevel = CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM;
    public CameraStreamingSetting.PREVIEW_SIZE_RATIO mSizeRatio = CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9;
    public String mFocusMode = CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_PICTURE;
    // 是否开启美颜
    public boolean mIsFaceBeautyEnabled;
    // 是否使用内置美颜
    public boolean mIsCustomFaceBeauty = false;
    // 是否关闭自动对焦功能，默认开启，false为关闭
    public boolean mContinuousAutoFocus = true;
    public boolean mPreviewMirror = false;
    public boolean mEncodingMirror = false;
}
