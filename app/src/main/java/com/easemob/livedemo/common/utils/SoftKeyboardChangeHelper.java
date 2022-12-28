package com.easemob.livedemo.common.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;


public class SoftKeyboardChangeHelper {

    private static final int CHANG_VALUE = 200;
    private View rootView;
    private int rootViewVisibleHeight;
    private OnSoftKeyboardChangeListener listener;

    public SoftKeyboardChangeHelper(Activity activity) {
        addRootViewListener(activity);
    }

    private void addRootViewListener(Activity activity) {
        rootView = activity.getWindow().getDecorView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int visibleHeight = r.height();
                if (rootViewVisibleHeight == 0) {
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }


                if (rootViewVisibleHeight == visibleHeight) {
                    return;
                }

                if (rootViewVisibleHeight - visibleHeight > CHANG_VALUE) {
                    if (listener != null) {
                        listener.keyboardShow(rootViewVisibleHeight - visibleHeight);
                    }
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }

                if (visibleHeight - rootViewVisibleHeight > CHANG_VALUE) {
                    if (listener != null) {
                        listener.keyboardHide(visibleHeight - rootViewVisibleHeight);
                    }
                    rootViewVisibleHeight = visibleHeight;
                }
            }
        });
    }


    public static void setOnSoftKeyboardChangeListener(Activity activity, OnSoftKeyboardChangeListener listener) {
        SoftKeyboardChangeHelper helper = new SoftKeyboardChangeHelper(activity);
        helper.setOnSoftKeyboardChangeListener(listener);
    }


    public void setOnSoftKeyboardChangeListener(OnSoftKeyboardChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSoftKeyboardChangeListener {

        void keyboardShow(int height);


        void keyboardHide(int height);
    }
}
