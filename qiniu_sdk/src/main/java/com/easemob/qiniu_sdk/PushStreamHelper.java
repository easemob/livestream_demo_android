package com.easemob.qiniu_sdk;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.text.TextUtils;
import android.util.Log;

import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.AudioSourceCallback;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * 作为推流的帮助类
 * 推流SDK设置参考：https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using
 */
public class PushStreamHelper implements StreamingStateChangedListener, StreamingSessionListener, StreamStatusCallback, AudioSourceCallback {
    private static final String GENERATE_STREAM_TEXT = "https://api-demo.qnsdk.com/v1/live/stream/";
    private static final String TAG = "PushStreamHelper";
    private static PushStreamHelper instance;

    private StreamingProfile mProfile;
    private MediaStreamingManager mMediaStreamingManager;
    private CameraStreamingSetting cameraStreamingSetting;

    private PushStreamHelper(){}

    public static PushStreamHelper getInstance() {
        if(instance == null) {
            synchronized (PushStreamHelper.class) {
                if(instance == null) {
                    instance = new PushStreamHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 根据推流官方文档，可以在application的onCreate()中进行初始化
     * @param context
     */
    public void init(Context context) {
        StreamingEnv.init(context.getApplicationContext());
    }

    /**
     * 获取推流地址
     * @param userId
     * @param callBack
     */
    public void getPublishUrl(String userId, OnCallBack<String> callBack) {
        new Thread(){
            public void run(){
                if(callBack != null) {
                    callBack.onSuccess(Util.syncRequest(GENERATE_STREAM_TEXT + userId));
                }
            }
        }.start();
    }

    public void initPublishVideo(GLSurfaceView surfaceView, String publishUrl) {
        if(!TextUtils.isEmpty(publishUrl)) {
            try {
                setCameraStreamingSetting();
                initProfile(publishUrl);
                setMediaStreamManager(surfaceView);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void initProfile(String publishUrl) throws URISyntaxException {
        //encoding setting
        Log.e(TAG, "initProfile");
        mProfile = new StreamingProfile();
        mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)              // 设置视频质量
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)            // 设置音频质量
                .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480)
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY) // 软编的EncoderRCModes,默认为EncoderRCModes.QUALITY_PRIORITY
                .setPublishUrl(publishUrl);                               // 设置推流地址
    }

    /**
     * 设置CameraStreamingSetting
     * 内置美颜流程的开启通过 CameraStreamingSetting#setBuiltInFaceBeautyEnabled(boolean eanble) 进行，
     * 注意，若希望自定义美颜，需要 disable 该接口，否则行为未知。
     * 在初始化 CameraStreamingSetting 的时候，可以初始化对应的美颜参数：
     * <pre class="prettyprint">
     *     // FaceBeautySetting 中的参数依次为：beautyLevel，whiten，redden，即磨皮程度、美白程度以及红润程度，取值范围为[0.0f, 1.0f]
     *      Setting.setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f))
     *             .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY)
     * </pre>
     * @return
     */
    private void setCameraStreamingSetting() {
        Log.e(TAG, "setCameraStreamingSetting");
        //preview setting
        cameraStreamingSetting = new CameraStreamingSetting();
        cameraStreamingSetting.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT) //默认后置摄像头，设置后置摄像头，CAMERA_FACING_FRONT为前置
                .setContinuousFocusModeEnabled(true)    //自动对焦，默认开启
                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_VIDEO)   //设置对焦模式，默认是VIDEO，可选FOCUS_MODE_CONTINUOUS_PICTURE
                // 及FOCUS_MODE_AUTO, PICTURE 对焦会比 VIDEO 更加频繁，
                // 功耗会更高，建议使用 VIDEO
                //.setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f)) //初始化美颜参数
                //.setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)
                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_PICTURE)
                .setContinuousFocusModeEnabled(true)
                .setFrontCameraPreviewMirror(false)
                .setFrontCameraMirror(false)
                .setRecordingHint(false)
                .setResetTouchFocusDelayInMs(3000);
    }

    /**
     * 设置MediaStreamManager, 核心类
     * 所有麦克风相关的配置，都在{@link com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting} 类中进行。
     * 例如，希望增加蓝牙麦克风的支持：
     * <pre class="prettyprint">
     *     mMicrophoneStreamingSetting.setBluetoothSCOEnabled(true);
     *     mMediaStreamingManager.prepare(setting, mMicrophoneStreamingSetting, mProfile);
     * </pre>
     * @param surfaceView
     */
    private void setMediaStreamManager(GLSurfaceView surfaceView) {
        //streaming engine init and setListener
        mMediaStreamingManager = new MediaStreamingManager(surfaceView.getContext(), surfaceView, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
        mMediaStreamingManager.prepare(cameraStreamingSetting, mProfile);
        Log.e(TAG, "setMediaStreamManager");
        mMediaStreamingManager.setAutoRefreshOverlay(true);
        mMediaStreamingManager.setStreamingStateListener(this);
        mMediaStreamingManager.setStreamingSessionListener(this);
        mMediaStreamingManager.setStreamStatusCallback(this);
        mMediaStreamingManager.setAudioSourceCallback(this);
        Log.e(TAG, "setMediaStreamManager end");
    }

    /**
     * 退出 MediaStreamingManager，该操作会主动断开当前的流链接，并关闭 Camera 和释放相应的资源。
     */
    public void pause() {
        if(mMediaStreamingManager != null) {
            mMediaStreamingManager.pause();
        }
    }

    /**
     * 进行 Camera 的打开操作，当成功打开后，会返回 STATE.READY 消息，用户可以在接受到 STATE.READY 之后，安全地进行推流操作
     */
    public void resume() {
        if(mMediaStreamingManager != null) {
            mMediaStreamingManager.resume();
        }
    }

    /**
     * 释放不紧要资源。
     */
    public void destroy() {
        if(mMediaStreamingManager != null) {
            mMediaStreamingManager.destroy();
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if(mMediaStreamingManager != null) {
            mMediaStreamingManager.switchCamera();
        }
    }

    /**
     * 设置是否禁音推流
     * @param isMute
     */
    public void mute(boolean isMute) {
        if(mMediaStreamingManager != null) {
            mMediaStreamingManager.mute(isMute);
        }
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        Log.e(TAG, "streamingState = " + streamingState + "extra = " + extra);
        switch (streamingState) {
            case PREPARING:
                Log.e(TAG, "PREPARING");
                break;
            case READY:
                Log.e(TAG, "READY");
                // start streaming when READY
                startStreamingInternal();
                break;
            case CONNECTING:
                Log.e(TAG, "连接中");
                break;
            case STREAMING:
                Log.e(TAG, "推流中");
                // The av packet had been sent.
                break;
            case SHUTDOWN:
                Log.e(TAG, "直播中断");
                // The streaming had been finished.
                break;
            case IOERROR:
                // Network connect error.
                Log.e(TAG, "网络连接失败");
                break;
            case OPEN_CAMERA_FAIL:
                Log.e(TAG, "摄像头打开失败");
                // Failed to open camera.
                break;
            case DISCONNECTED:
                Log.e(TAG, "已经断开连接");
                // The socket is broken while streaming
                break;
            case TORCH_INFO:
                Log.e(TAG, "开启闪光灯");
                break;
        }
    }

    @Override
    public boolean onRecordAudioFailedHandled(int i) {
        return false;
    }

    @Override
    public boolean onRestartStreamingHandled(int i) {
        startStreamingInternal();
        return true;
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        return null;
    }

    @Override
    public int onPreviewFpsSelected(List<int[]> list) {
        return -1;
    }

    @Override
    public void notifyStreamStatusChanged(StreamingProfile.StreamStatus streamStatus) {

    }

    @Override
    public void onAudioSourceAvailable(ByteBuffer byteBuffer, int i, long l, boolean b) {

    }

    private void startStreamingInternal() {
        new Thread(()-> {
            if (mMediaStreamingManager != null) {
                mMediaStreamingManager.startStreaming();
            }
        }).start();
    }
}
