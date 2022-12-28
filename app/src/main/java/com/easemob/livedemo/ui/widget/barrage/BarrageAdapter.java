package com.easemob.livedemo.ui.widget.barrage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.easemob.livedemo.R;

@SuppressWarnings({"unchecked"})
public abstract class BarrageAdapter<T extends DataSource>
        implements View.OnClickListener {

    private static final int MSG_CREATE_VIEW = 1;

    private AdapterListener<T> mAdapterListener;
    private Set<Integer> mTypeList;
    private IBarrageView barrageView;
    private LinkedList<T> mDataList;

    private Context mContext;
    private long interval;
    private int repeat;
    private AtomicBoolean isDestroy = new AtomicBoolean(false);

    private ExecutorService mService = Executors.newSingleThreadExecutor();
    private BarrageAdapterHandler<T> mHandler = new BarrageAdapterHandler<>(Looper.getMainLooper(), this);


    @SuppressWarnings("WeakerAccess")
    public BarrageAdapter(AdapterListener<T> adapterListener, Context context) {
        this.mAdapterListener = adapterListener;
        this.mTypeList = new HashSet<>();
        this.mContext = context;
        this.mDataList = new LinkedList<>();
    }

    public void setAdapterListener(AdapterListener<T> adapterListener) {
        this.mAdapterListener = adapterListener;
    }


    public void setBarrageView(IBarrageView barrageView) {
        this.barrageView = barrageView;
        this.interval = barrageView.getInterval();
        this.repeat = barrageView.getRepeat();
    }

    private void createItemView(T data, View cacheView) {
        int layoutType = getItemLayout(data);
        BarrageViewHolder<T> holder = null;
        if (cacheView != null) {
            holder = (BarrageViewHolder<T>) cacheView.getTag(R.id.barrage_view_holder);
        }
        if (null == holder) {
            holder = createViewHolder(mContext, layoutType);
            mTypeList.add(data.getType());
        }
        bindViewHolder(holder, data);
        if (barrageView != null)
            barrageView.addBarrageItem(holder.getItemView());
    }


    private BarrageViewHolder<T> createViewHolder(Context context, int type) {
        View root = LayoutInflater.from(context).inflate(type, null);
        BarrageViewHolder<T> holder = onCreateViewHolder(root, type);


        root.setTag(R.id.barrage_view_holder, holder);
        root.setOnClickListener(this);
        return holder;
    }


    protected abstract BarrageViewHolder<T> onCreateViewHolder(View root, int type);


    public abstract @LayoutRes
    int getItemLayout(T t);


    private void bindViewHolder(BarrageViewHolder<T> holder, T data) {
        if (null == data)
            return;
        holder.bind(data);
    }

    public Set<Integer> getTypeList() {
        return mTypeList;
    }

    @Override
    public void onClick(View v) {
        BarrageViewHolder<T> holder = (BarrageViewHolder<T>) v.getTag(R.id.barrage_view_holder);
        if (holder != null) {
            if (mAdapterListener != null) {
                mAdapterListener.onItemClick(holder, holder.mData);
            }
        }
    }


    public void add(T data) {
        if (data == null)
            return;
        mDataList.add(data);
        mService.submit(new DelayRunnable(1));
    }


    public void addList(List<T> dataList) {
        if (dataList == null || dataList.size() == 0)
            return;
        int len = dataList.size();
        mDataList.clear();
        mDataList.addAll(dataList);
        mService.submit(new DelayRunnable(len));
    }

    public void destroy() {
        while (!isDestroy.get())
            isDestroy.compareAndSet(false, true);
        mDataList.clear();
        if (!mService.isShutdown())
            mService.shutdownNow();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        barrageView = null;
    }

    public abstract static class BarrageViewHolder<T> {
        public T mData;
        private View itemView;

        public BarrageViewHolder(View itemView) {
            this.itemView = itemView;
        }

        View getItemView() {
            return itemView;
        }

        void bind(T data) {
            mData = data;
            onBind(data);
        }

        protected abstract void onBind(T data);
    }


    public class DelayRunnable implements Runnable {

        private int len;

        DelayRunnable(int len) {
            this.len = len;
        }

        @Override
        public void run() {
            if (repeat != -1 && repeat > 0) {
                for (int j = 0; j < repeat; j++) {
                    sendMsg(len);
                }
            } else if (repeat == -1) {
                while (!isDestroy.get()) {
                    sendMsg(len);
                }
            }
        }
    }

    private void sendMsg(int len) {
        for (int i = 0; i < len; i++) {
            mHandler.sendEmptyMessage(MSG_CREATE_VIEW);
            try {
                Thread.sleep(interval * 20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class BarrageAdapterHandler<T extends DataSource> extends Handler {
        private WeakReference<BarrageAdapter> adapterReference;

        BarrageAdapterHandler(Looper looper, BarrageAdapter adapter) {
            super(looper);
            adapterReference = new WeakReference<>(adapter);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_CREATE_VIEW: {
                    T data = (T) adapterReference.get().mDataList.remove();
                    if (data == null)
                        break;
                    if (adapterReference.get().barrageView == null)
                        throw new RuntimeException("please set barrageView,barrageView can't be null");
                    // get from cache
                    View cacheView = adapterReference.get().barrageView.getCacheView(data.getType());
                    adapterReference.get().createItemView(data, cacheView);
                    if (adapterReference.get().repeat != 1)
                        adapterReference.get().mDataList.addLast(data);
                }
            }

        }

    }
}
