package com.easemob.livedemo.qiniu;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.ThreadManager;
import com.easemob.livedemo.ui.live.LiveBaseActivity;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.AudioSourceCallback;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by lw.tan on 2017/3/1.
 * 推流SDK设置参考：https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using
 */

public class LiveCameraView extends GLSurfaceView implements StreamingStateChangedListener,
                                                            StreamingSessionListener, StreamStatusCallback, AudioSourceCallback {
    private static final String TAG = LiveCameraView.class.getSimpleName();
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private Listener mListener;

    private StreamingProfile mProfile;
    private MediaStreamingManager mMediaStreamingManager;
    private String publishURLFromServer;
    private boolean isMute;//是否禁音推流

    public LiveCameraView(Context context) {
        this(context, null);
    }

    public LiveCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void initialize(Context context) {
        Log.i(TAG, "initialize");
        mScaleDetector = new ScaleGestureDetector(context, mScaleListener);
        mGestureDetector = new GestureDetector(context, mGestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mGestureDetector.onTouchEvent(event)) {
            return mScaleDetector.onTouchEvent(event);
        }
        return false;
    }

    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mListener != null) {
                mListener.onSingleTapUp(e);
            }
            return false;
        }
    };

    private ScaleGestureDetector.SimpleOnScaleGestureListener mScaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private float mScaleFactor = 1.0f;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // factor > 1, zoom
            // factor < 1, pinch
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.01f, Math.min(mScaleFactor, 1.0f));

            return mListener != null && mListener.onZoomValueChanged(mScaleFactor);
        }
    };

    public interface Listener {
        boolean onSingleTapUp(MotionEvent e);
        boolean onZoomValueChanged(float factor);
    }


    public void init(String url) {
        Log.i(TAG, "push url = "+url);
        publishURLFromServer = url;
        if(TextUtils.isEmpty(publishURLFromServer)) {
            Log.e(TAG, "publishURLFromServer = "+publishURLFromServer);
            return;
        }
        try {
            initProfile();
            setMediaStreamManager(getCameraStreamingSetting());
        } catch (URISyntaxException e) {
            e.printStackTrace();

        }
    }

    /**
     * 设置StreamingProfile(推流参数)<br/>
     * 所有推流相关的参数配置，都在{@link StreamingProfile}类中进行。
     * <h4>1、当需要自定义 video 的 fps、bitrate、profile 或者 audio 的 sample rate、bitrate，可以通过 AVProfile 设置</h4>
     * <pre class"prettyprint">
     *  // audio sample rate is 44100, audio bitrate is 48 * 1024 bps
     *  StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 48 * 1024);
     *  // fps is 20, video bitrate is 1000 * 1024 bps, maxKeyFrameInterval is 60, profile is HIGH
     *  StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(20, 1000 * 1024, 60, StreamingProfile.H264Profile.HIGH);
     *  StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
     *  mStreamingProfile.setAVProfile(avProfile)
     * </pre>
     * 注：44100 是 Android 平台唯一保证所以设备支持的采样率，为了避免音频兼容性问题，建议设置为 44100。<br/>
     * StreamingProfile#setAVProfile 的优先级高于 Quality，也就是说，当同时调用了 Quality 和 AVProfile 的设置，AVProfile 会覆盖 Quality 的设置值。
     *
     * <h4>2、HappyDns 支持</h4>
     * 为了防止 Dns 被劫持，SDK 加入了 HappyDns 支持。<br/>
     * 通过 StreamingProfile 设定自定义 DnsManager，如下：<br/>
     * <pre class="prettyprint">
     *   public static DnsManager getMyDnsManager() {
     *       IResolver r0 = new DnspodFree();
     *       IResolver r1 = AndroidDnsServer.defaultResolver();
     *       IResolver r2 = null;
     *       try {
     *           r2 = new Resolver(InetAddress.getByName("119.29.29.29"));
     *       } catch (IOException ex) {
     *           ex.printStackTrace();
     *       }
     *       return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
     *   }
     *   StreamingProfile mProfile = new StreamingProfile();
     *   // Setting null explicitly, means give up {@link com.qiniu.android.dns.DnsManager} and access by the original host.
     *   mStreamingProfile.setDnsManager(getMyDnsManager()); // set your DnsManager
     * </pre>
     * 若显示地设置为 null，即：
     * <pre class="prettyprint">
     *     mStreamingProfile.setDnsManager(null);
     * </pre>
     * SDK 会使用系统 DNS 解析，而不会使用 DnsManager 来进行 Dns 解析。<br/>
     * 若不调用 StreamingProfile#setDnsManager 方法，SDK 会默认的创建一个 DnsManager 来对 Dns 进行解析。
     * <h4>3、软编的 EncoderRCModes</h4>
     * 目前 RC mode 支持的类型：
     * <ul>
     * <li>EncoderRCModes.QUALITY_PRIORITY: 质量优先，实际的码率可能高于设置的码率
     * <li>EncoderRCModes.BITRATE_PRIORITY: 码率优先，更精确地码率控制
     * </ul>
     * 默认值为 EncoderRCModes.QUALITY_PRIORITY
     *
     * @throws URISyntaxException
     */
    private void initProfile() throws URISyntaxException {
        //encoding setting
        mProfile = new StreamingProfile();
        mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)              // 设置视频质量
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)            // 设置音频质量
                .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480)
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY) // 软编的EncoderRCModes,默认为EncoderRCModes.QUALITY_PRIORITY
                .setPublishUrl(publishURLFromServer);                               // 设置推流地址
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
    private CameraStreamingSetting getCameraStreamingSetting() {
        //preview setting
        CameraStreamingSetting setting = new CameraStreamingSetting();
        setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK) //默认后置摄像头，设置后置摄像头，CAMERA_FACING_FRONT为前置
                .setContinuousFocusModeEnabled(true)    //自动对焦，默认开启
                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_VIDEO)   //设置对焦模式，默认是VIDEO，可选FOCUS_MODE_CONTINUOUS_PICTURE
                                                                                    // 及FOCUS_MODE_AUTO, PICTURE 对焦会比 VIDEO 更加频繁，
                                                                                    // 功耗会更高，建议使用 VIDEO
                //.setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f)) //初始化美颜参数
                //.setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);
        return setting;
    }

    /**
     * 设置MediaStreamManager, 核心类
     * 所有麦克风相关的配置，都在{@link com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting} 类中进行。
     * 例如，希望增加蓝牙麦克风的支持：
     * <pre class="prettyprint">
     *     mMicrophoneStreamingSetting.setBluetoothSCOEnabled(true);
     *     mMediaStreamingManager.prepare(setting, mMicrophoneStreamingSetting, mProfile);
     * </pre>
     * @param setting
     */
    private void setMediaStreamManager(CameraStreamingSetting setting) {
        //streaming engine init and setListener
        mMediaStreamingManager = new MediaStreamingManager(getContext(), this, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
        mMediaStreamingManager.prepare(setting, mProfile);
        mMediaStreamingManager.setStreamingStateListener(this);
        mMediaStreamingManager.setStreamingSessionListener(this);
        mMediaStreamingManager.setStreamStatusCallback(this);
        mMediaStreamingManager.setAudioSourceCallback(this);
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
            this.isMute = isMute;
        }
    }

    /**
     * 获取是否禁音推流
     * @return
     */
    public boolean isMute() {
        return isMute;
    }

    /**
     * 对应activity的onResume
     */
    public void onResume() {
        if(mMediaStreamingManager != null) {
            super.onResume();
            mMediaStreamingManager.resume();
        }
    }

    /**
     * 对应activity的onPause
     */
    public void onPause() {
        if(mMediaStreamingManager != null) {
            super.onPause();
            // You must invoke pause here.
            mMediaStreamingManager.pause();
        }
    }

    /**
     * 对应activity的onDestroy
     */
    public void onDestroy() {
        if(mMediaStreamingManager != null) {
            mMediaStreamingManager.destroy();
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
                ThreadManager.getInstance().runOnIOThread(()-> {
                    if (mMediaStreamingManager != null) {
                        mMediaStreamingManager.startStreaming();
                    }
                });
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
        Log.i(TAG, "onRecordAudioFailedHandled");
        return false;
    }

    @Override
    public boolean onRestartStreamingHandled(int i) {
        Log.i(TAG, "onRestartStreamingHandled");
        ThreadManager.getInstance().runOnIOThread(()-> {
            if (mMediaStreamingManager != null) {
                mMediaStreamingManager.startStreaming();
            }
        });
        return false;
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        return null;
    }

    /**
     * SDK 默认会根据推流帧率自动选择一个合适的采集帧率.
     * 用户也可以通过 StreamingSessionListener#onPreviewFpsSelected 自定义选择一个合适的预览 FPS，
     * onPreviewFpsSelected 的参数 list 是 Camera 系统支持的预览 FPS 列表（Camera.Parameters#getSupportedPreviewFpsRange()）。
     * 如果 onPreviewFpsSelected 返回为 -1，代表放弃自定义选择，那么 SDK 会使用前面的策略选择一个合适的预览 FPS，
     * 否则使用 onPreviewFpsSelected 的返回值。
     * @param list
     * @return
     */
    @Override
    public int onPreviewFpsSelected(List<int[]> list) {
        return -1;
    }

    @Override
    public void notifyStreamStatusChanged(StreamingProfile.StreamStatus streamStatus) {
        Log.e(TAG, "StreamStatus = " + streamStatus);
    }

    @Override
    public void onAudioSourceAvailable(ByteBuffer byteBuffer, int i, long l, boolean b) {

    }
}
