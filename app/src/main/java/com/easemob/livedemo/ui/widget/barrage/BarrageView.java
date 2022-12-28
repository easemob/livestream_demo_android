package com.easemob.livedemo.ui.widget.barrage;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.easemob.livedemo.R;


@SuppressWarnings({"unchecked", "FieldCanBeLocal", "unused", "MismatchedReadAndWriteOfArray"})
public class BarrageView extends ViewGroup implements IBarrageView {

    public static final String TAG = "BarrageView";

    public final static int MODEL_COLLISION_DETECTION = 1;
    public final static int MODEL_RANDOM = 2;
    public final static int GRAVITY_TOP = 1;
    public final static int GRAVITY_MIDDLE = 2;
    public final static int GRAVITY_BOTTOM = 4;
    public final static int GRAVITY_FULL = 7;
    public final static int MAX_COUNT = 500;
    public final static int DEFAULT_SPEED = 200;
    public final static int DEFAULT_WAVE_SPEED = 20;

    private BarrageHandler mHandler;
    public int count = 0;
    public long interval;
    private int model = MODEL_RANDOM;

    private int speed = 200;
    private int speedWaveValue = 20;
    private int[] speedArray;

    private boolean cancel = false;

    private int gravity = GRAVITY_TOP;
    private int barrageLines;
    private int repeat;
    private int width, height;
    private List<View> barrageList;
    private BarrageAdapter mAdapter;
    private int singleLineHeight = -1;
    private boolean isInterceptTouchEvent = false;
    private int barrageDistance;
    private SparseArray<LinkedList<View>> mArray;
    private Random random = new Random();
    private CountDownLatch countDownLatch = new CountDownLatch(1);


    public BarrageView(Context context) {
        this(context, null);
    }

    public BarrageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("HandlerLeak")
    public BarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.barrageList = new ArrayList<>();
        this.mArray = new SparseArray<>();
        mHandler = new BarrageHandler(this);

    }

    public void setAdapter(BarrageAdapter adapter) {
        this.mAdapter = adapter;
        mAdapter.setBarrageView(this);
    }

    public void setOptions(Options options) {
        if (options != null) {
            if (options.config.gravity != -1) {
                this.gravity = options.config.gravity;
            }

            if (options.config.interval > 0) {
                this.interval = options.config.interval;
            }

            if (options.config.speed != 0 && options.config.waveSpeed != 0) {
                this.speed = options.config.speed;
                this.speedWaveValue = options.config.waveSpeed;
            }

            if (options.config.model != 0) {
                this.model = options.config.model;
            }

            if (options.config.repeat != 0) {
                this.repeat = options.config.repeat;
            }

            this.isInterceptTouchEvent = options.config.isInterceptTouchEvent;
        }
    }

    public synchronized void addViewToCaches(int type, View root) {
        if (mArray.get(type) == null) {
            LinkedList<View> linkedList = new LinkedList<>();
            linkedList.add(root);
            mArray.put(type, linkedList);
        } else {
            mArray.get(type).add(root);
        }
    }

    public synchronized View removeViewFromCaches(int type) {
        if (mArray.indexOfKey(type) >= 0) {
            return mArray.get(type).poll();
        } else {
            return null;
        }
    }

    public int getCacheSize() {
        int sum = 0;
        Set<Integer> mTypeList = mAdapter.getTypeList();
        for (Integer type : mTypeList) {
            if (mArray.indexOfKey(type) >= 0) {
                sum += mArray.get(type).size();
            }
        }
        return sum;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isInterceptTouchEvent)
            return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.width = width;
        this.height = height;
        //countDownLatch.countDown();
    }

    private void initBarrageListAndSpeedArray() {
        barrageDistance = DeviceUtils.dp2px(getContext(), 12);
        /*try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        barrageLines = height / (singleLineHeight + barrageDistance);
        for (int i = 0; i < barrageLines; i++) {
            barrageList.add(i, null);
        }
        speedArray = new int[barrageLines];
        for (int i = 0; i < barrageLines; i++) {
            speedArray[i] = 0;
        }
    }

    public void setSingleLineHeight(int singleLineHeight) {
        this.singleLineHeight = singleLineHeight;
    }

    @Override
    public void addBarrageItem(final View view) {
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        final int itemWidth = view.getMeasuredWidth();
        final int itemHeight = view.getMeasuredHeight();

        if (singleLineHeight == -1) {

            singleLineHeight = itemHeight;
            initBarrageListAndSpeedArray();
        }
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(width, -itemWidth);

        final int line = getBestLine(itemHeight);
        int curSpeed = getSpeed(line, itemWidth);
        long duration = (int) ((float) (width + itemWidth) / (float) curSpeed + 1) * 1000;
        //Log.i(TAG,"duration:"+duration);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = animation.getAnimatedFraction();
                //animation.getAnimatedValue()
                //Log.e(TAG, "value:" + value);
                if (cancel) {
                    valueAnimator.cancel();
                    BarrageView.this.removeView(view);
                }
                //view.layout(value, line * (singleLineHeight + barrageDistance) + barrageDistance / 2, value + itemWidth, line * (singleLineHeight + barrageDistance) + barrageDistance / 2 + itemHeight);
                view.layout((int) (width - (width + itemWidth) * value)
                        , line * (singleLineHeight + barrageDistance) + barrageDistance / 2
                        , (int) (width - (width + itemWidth) * value) + itemWidth
                        , line * (singleLineHeight + barrageDistance) + barrageDistance / 2 + itemHeight);
            }
        });
        valueAnimator.addListener(new SimpleAnimationListener() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                BarrageView.this.removeView(view);
                BarrageAdapter.BarrageViewHolder holder = (BarrageAdapter.BarrageViewHolder) view.getTag(R.id.barrage_view_holder);
                DataSource d = (DataSource) holder.mData;
                int type = d.getType();
                addViewToCaches(type, view);
                mHandler.sendEmptyMessage(0);
            }
        });
        addView(view);
        speedArray[line] = curSpeed;
        view.layout(width, line * (singleLineHeight + barrageDistance) + barrageDistance / 2, width + itemWidth, line * (singleLineHeight + barrageDistance) + barrageDistance / 2 + itemHeight);
        barrageList.set(line, view);
        valueAnimator.start();
    }


    private int getSpeed(int line, int itemWidth) {
        if (model == MODEL_RANDOM) {
            return speed - speedWaveValue + random.nextInt(2 * speedWaveValue);
        } else {
            int lastSpeed = speedArray[line];
            View view = barrageList.get(line);
            int curSpeed;
            if (view == null) {
                curSpeed = speed - speedWaveValue + random.nextInt(2 * speedWaveValue);
                return curSpeed;
            }
            int slideLength = (int) (width - view.getX());
            if (view.getWidth() > slideLength) {
                return lastSpeed;
            }
            int lastLeavedSlidingTime = (int) ((view.getX() + view.getWidth()) / (float) lastSpeed) + 1;
            int fastestSpeed = (width) / lastLeavedSlidingTime;
            fastestSpeed = Math.min(fastestSpeed, speed + speedWaveValue);
            if (fastestSpeed <= speed - speedWaveValue) {
                curSpeed = speed - speedWaveValue;
            } else
                curSpeed = speed - speedWaveValue + random.nextInt(fastestSpeed - (speed - speedWaveValue));
            return curSpeed;
        }
    }


    private int getBestLine(int currentItemHeight) {
        if (currentItemHeight <= singleLineHeight) {
            return realGetBestLine(1);
        } else {
            int v = currentItemHeight / singleLineHeight;
            if (v * singleLineHeight < currentItemHeight)
                v++;
            return realGetBestLine(v);
        }
    }

    private int realGetBestLine(int v) {
        int gewei = gravity % 2;
        int temp = gravity / 2;
        int shiwei = temp % 2;
        temp = temp / 2;
        int baiwei = temp % 2;

        int firstPart = barrageLines;

        List<Integer> legalLines = new ArrayList<>();
        if (gewei == 1) {
            for (int i = 0; i < firstPart; i++)
                if (i % v == 0)
                    legalLines.add(i);
        }
        if (shiwei == 1) {
            for (int i = firstPart; i < 2 * firstPart; i++)
                if (i % v == 0)
                    legalLines.add(i);
        }
        if (baiwei == 1) {
            for (int i = 2 * firstPart; i < barrageLines; i++)
                if (i % v == 0 && i <= barrageLines - v)
                    legalLines.add(i);
        }


        int bestLine = 0;
        for (int i = 0; i < barrageLines; i++) {
            if (barrageList.get(i) == null && i % v == 0) {
                bestLine = i;
                if (legalLines.contains(bestLine))
                    return bestLine;
            }
        }
        float minSpace = Integer.MAX_VALUE;
        for (int i = barrageLines - 1; i >= 0; i--) {
            if (i % v == 0 && i <= barrageLines - v)
                if (legalLines.contains(i)) {
                    if (barrageList.get(i).getX() + barrageList.get(i).getWidth() <= minSpace) {
                        minSpace = barrageList.get(i).getX() + barrageList.get(i).getWidth();
                        bestLine = i;
                    }
                }
        }
        return bestLine;
    }

    public void destroy() {
        cancel = true;
        mHandler.removeCallbacksAndMessages(null);
        mAdapter.destroy();
    }

    public synchronized void shrinkCacheSize() {
        Set<Integer> mTypeList = mAdapter.getTypeList();
        for (Integer type : mTypeList) {
            if (mArray.indexOfKey(type) >= 0) {
                LinkedList<View> list = mArray.get(type);
                int len = list.size();
                while (list.size() > (len / 2.0 + 0.5)) {
                    list.pop();
                }
                mArray.put(type, list);
            }
        }
    }

    @Override
    public View getCacheView(int type) {
        return removeViewFromCaches(type);
    }

    @Override
    public long getInterval() {
        return interval;
    }

    @Override
    public int getRepeat() {
        return repeat;
    }

    private static class BarrageHandler extends Handler {
        private WeakReference<BarrageView> barrageViewReference;

        BarrageHandler(BarrageView barrageView) {
            this.barrageViewReference = new WeakReference(barrageView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (barrageViewReference.get().count < MAX_COUNT) {
                        barrageViewReference.get().count++;
                    } else {
                        barrageViewReference.get().shrinkCacheSize();
                        barrageViewReference.get().count = barrageViewReference.get().getCacheSize();
                    }
            }
        }
    }

    static class Config {
        int gravity = -1;
        long interval;
        int speed;
        int waveSpeed;
        int model;
        boolean isInterceptTouchEvent = true;
        int repeat = 1;
    }

    public static class Options {

        Config config;

        public Options() {
            config = new Config();
        }


        public Options setGravity(int gravity) {
            this.config.gravity = gravity;
            return this;
        }


        public Options setInterval(long interval) {
            this.config.interval = interval;
            return this;
        }


        public Options setSpeed(int speed, int waveValue) {
            if (speed < waveValue
                    || speed <= 0
                    || waveValue < 0)
                throw new RuntimeException("duration or wavValue is not correct!");
            this.config.speed = speed;
            this.config.waveSpeed = waveValue;
            return this;
        }


        public Options setModel(int model) {
            this.config.model = model;
            return this;
        }


        public Options setRepeat(int repeat) {
            this.config.repeat = repeat;
            return this;
        }


        public Options setClick(boolean isInterceptTouchEvent) {
            this.config.isInterceptTouchEvent = !isInterceptTouchEvent;
            return this;
        }

    }

}
