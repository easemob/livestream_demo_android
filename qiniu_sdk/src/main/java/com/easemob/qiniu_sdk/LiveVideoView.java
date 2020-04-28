package com.easemob.qiniu_sdk;

import android.content.Context;
import android.media.session.MediaController;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

/**
 * SDK设置文档：https://developer.qiniu.com/pili/sdk/1210/the-android-client-sdk
 */
public class LiveVideoView extends PLVideoTextureView implements PLOnPreparedListener, PLOnInfoListener, PLOnCompletionListener, PLOnVideoSizeChangedListener, PLOnErrorListener {

    private static final String TAG = LiveVideoView.class.getSimpleName();
    private OnVideoListener videoListener;

    public LiveVideoView(Context context) {
        this(context, null);
    }

    public LiveVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置加载动画
     * 在播放器进入缓冲状态时，自动显示加载界面，缓冲结束后，自动隐藏加载界面
     * @param loadingView
     */
    public void setLoadingView(View loadingView) {
        this.setBufferingIndicator(loadingView);
    }

    public void attachView() {
        setOnPreparedListener(this);
        setOnInfoListener(this);
        setOnCompletionListener(this);
        setOnVideoSizeChangedListener(this);
        setOnErrorListener(this);
    }

    @Override
    public void onPrepared(int i) {
        if(this.videoListener != null) {
            videoListener.onPrepared(i);
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        switch (what) {
            case MEDIA_INFO_UNKNOWN ://未知消息
                Log.i(TAG, "未知消息");
                break;
            case MEDIA_INFO_CONNECTED ://连接成功
                Log.i(TAG, "连接成功");
                break;
            case MEDIA_INFO_BUFFERING_START ://开始缓冲
                Log.i(TAG, "开始缓冲");
                break;
            case MEDIA_INFO_BUFFERING_END ://停止缓冲
                Log.i(TAG, "停止缓冲");
                break;
            case MEDIA_INFO_SWITCHING_SW_DECODE ://硬解失败，自动切换软解
                Log.i(TAG, "硬解失败，自动切换软解");
                break;
            case MEDIA_INFO_VIDEO_ROTATION_CHANGED ://获取到视频的播放角度
                Log.i(TAG, "获取到视频的播放角度:"+extra);
                break;
            case MEDIA_INFO_VIDEO_GOP_TIME ://获取视频的I帧间隔
                Log.i(TAG, "获取视频的I帧间隔:"+extra);
                break;
            case MEDIA_INFO_VIDEO_BITRATE ://视频的码率统计结果
                Log.i(TAG, "视频的码率统计结果:"+extra);
                break;
            case MEDIA_INFO_VIDEO_FPS ://视频的帧率统计结果
                Log.i(TAG, "视频的帧率统计结果:"+extra);
                break;
            case MEDIA_INFO_AUDIO_BITRATE ://音频的帧率统计结果
                Log.i(TAG, "音频的帧率统计结果:"+extra);
                break;
            case MEDIA_INFO_AUDIO_FPS ://音频的帧率统计结果
                Log.i(TAG, "音频的帧率统计结果:"+extra);
                break;
        }
    }

    @Override
    public void onCompletion() {
        if(videoListener != null) {
            videoListener.onCompletion();
        }
    }

    @Override
    public void onVideoSizeChanged(int i, int i1) {
        if(videoListener != null) {
            videoListener.onVideoSizeChanged(i, i1);
        }
    }

    @Override
    public boolean onError(int errorCode) {
        Log.e("TAG", "errorCode = "+errorCode);
        switch (errorCode) {
            case MEDIA_ERROR_UNKNOWN ://未知错误
                Log.e(TAG, "未知错误");
                break;
            case ERROR_CODE_OPEN_FAILED ://播放器打开失败
                Log.e(TAG, "播放器打开失败");
                break;
            case ERROR_CODE_IO_ERROR ://网络异常
                Log.e(TAG, "网络异常");
                break;
            case ERROR_CODE_CACHE_FAILED ://预加载失败
                Log.e(TAG, "预加载失败");
                break;
            case ERROR_CODE_HW_DECODE_FAILURE ://硬解失败
                Log.e(TAG, "硬解失败");
                break;
            case ERROR_CODE_PLAYER_DESTROYED ://播放器已被销毁，需要再次 setVideoURL 或 prepareAsync
                Log.e(TAG, "播放器已被销毁");
                break;
            case ERROR_CODE_PLAYER_VERSION_NOT_MATCH ://so 库版本不匹配，需要升级
                Log.e(TAG, "so 库版本不匹配，需要升级");
                break;
        }
        if(videoListener != null) {
            return videoListener.onError(errorCode);
        }
        return false;
    }

    /**
     * 需要在开始播放之前设置
     */
    public void setAvOptions() {
        AVOptions options = new AVOptions();
        /**
         * DNS 服务器设置
         * 若不设置此项，则默认使用 DNSPod 的 httpdns 服务
         * 若设置为 127.0.0.1，则会使用系统的 DNS 服务器
         * 若设置为其他 DNS 服务器地址，则会使用设置的服务器
         */
        //options.setString(AVOptions.KEY_DNS_SERVER, server);

        /**
         * DNS 缓存设置
         * 若不设置此项，则每次播放未缓存的域名时都会进行 DNS 解析，并将结果缓存
         * 参数为 String[]，包含了要缓存 DNS 结果的域名列表
         * SDK 在初始化时会解析列表中的域名，并将结果缓存
         */
        //options.setStringArray(AVOptions.KEY_DOMAIN_LIST, domainList);

        /**
         * 解码方式
         * 具体用法见：https://developer.qiniu.com/pili/sdk/1210/the-android-client-sdk /5.1 播放参数配置
         */
        //options.setInteger(AVOptions.KEY_MEDIACODEC, codec);
        // 若设置为 1，则底层会进行一些针对直播流的优化
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        // 快开模式，启用后会加快该播放器实例再次打开相同协议的视频流的速度
        options.setInteger(AVOptions.KEY_FAST_OPEN, 1);
        // 打开重试次数，设置后若打开流地址失败，则会进行重试
        options.setInteger(AVOptions.KEY_OPEN_RETRY_TIMES, 5);
        // 预设置 SDK 的 log 等级， 0-4 分别为 v/d/i/w/e
        options.setInteger(AVOptions.KEY_LOG_LEVEL, 2);
        // 打开视频时单次 http 请求的超时时间，一次打开过程最多尝试五次，单位为 ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);//
        // 解码方式
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE);
        // 默认的缓存大小，单位是 ms，默认值是：500
        //options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 500);
        // 最大的缓存大小，单位是 ms
        // 默认值是：2000，若设置值小于 KEY_CACHE_BUFFER_DURATION 则不会生效
        //options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 4000);
        // 是否开启直播优化，1 为开启，0 为关闭。若开启，视频暂停后再次开始播放时会触发追帧机制
        // 默认为 0
        //options.setInteger(AVOptions.KEY_LIVE_STREAMING, 0);
        // 设置拖动模式，1 位精准模式，即会拖动到时间戳的那一秒；0 为普通模式，会拖动到时间戳最近的关键帧。默认为 0
        //options.setInteger(AVOptions.KEY_SEEK_MODE, 0);
        // 开启解码后的视频数据回调
        // 默认值为 0，设置为 1 则开启
        //options.setInteger(AVOptions.KEY_VIDEO_DATA_CALLBACK, 1);
        // 开启解码后的音频数据回调
        // 默认值为 0，设置为 1 则开启
        //options.setInteger(AVOptions.KEY_VIDEO_DATA_CALLBACK, 1);
        // 设置开始播放位置
        // 默认不开启，单位为 ms
        //options.setInteger(AVOptions.KEY_START_POSITION, 10 * 1000);

        // 请在开始播放之前配置
        this.setAVOptions(options);
    }

    public void setOnVideoListener(OnVideoListener listener) {
        this.videoListener = listener;
    }

    public interface OnVideoListener {
        /**
         * 当 prepare 完成后，SDK 会回调该对象的 onPrepared 接口，下一步则可以调用播放器的 start() 启动播放
         * @param preparedTime
         */
        void onPrepared(int preparedTime);

        /**
         * 该对象用于监听播放结束的消息
         */
        void onCompletion();

        boolean onError(int errorCode);

        /**
         * 该回调用于监听当前播放的视频流的尺寸信息，在 SDK 解析出视频的尺寸信息后，会触发该回调，开发者可以在该回调中调整 UI 的视图尺寸
         * @param width
         * @param height
         */
        void onVideoSizeChanged(int width, int height);
    }
}
