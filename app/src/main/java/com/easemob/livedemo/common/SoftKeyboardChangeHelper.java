package com.easemob.livedemo.common;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * 参考博文：https://blog.csdn.net/auccy/article/details/80664234
 */
public class SoftKeyboardChangeHelper {
    //视图高度变化量，如果超过这个量，视为软键盘状态发生改变
    private static final int CHANG_VALUE = 200;
    /**
     * activity的根视图
     */
    private View rootView;
    private int rootViewVisibleHeight;
    private OnSoftKeyboardChangeListener listener;

    public SoftKeyboardChangeHelper(Activity activity) {
        addRootViewListener(activity);
    }

    private void addRootViewListener(Activity activity) {
        rootView = activity.getWindow().getDecorView();
        //监听视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int visibleHeight = r.height();
                if(rootViewVisibleHeight == 0) {
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }

                //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                if(rootViewVisibleHeight == visibleHeight) {
                    return;
                }

                //根视图显示高度变小且超过设定值，则认为软键盘显示
                if(rootViewVisibleHeight - visibleHeight > CHANG_VALUE) {
                    if(listener != null) {
                        listener.keyboardShow(rootViewVisibleHeight - visibleHeight);
                    }
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }

                //根视图显示高度变大且超过设定值，则认为软键盘隐藏
                if(visibleHeight - rootViewVisibleHeight > CHANG_VALUE) {
                    if(listener != null) {
                        listener.keyboardHide(visibleHeight - rootViewVisibleHeight);
                    }
                    rootViewVisibleHeight = visibleHeight;
                }
            }
        });
    }

    /**
     * 设置监听
     * @param activity
     * @param listener
     */
    public static void setOnSoftKeyboardChangeListener(Activity activity, OnSoftKeyboardChangeListener listener) {
        SoftKeyboardChangeHelper helper = new SoftKeyboardChangeHelper(activity);
        helper.setOnSoftKeyboardChangeListener(listener);
    }

    /**
     * 设置软键盘监听
     * @param listener
     */
    public void setOnSoftKeyboardChangeListener(OnSoftKeyboardChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSoftKeyboardChangeListener {
        /**
         * 软键盘展示
         * @param height
         */
        void keyboardShow(int height);

        /**
         * 软键盘收起
         * @param height
         */
        void keyboardHide(int height);
    }
}
