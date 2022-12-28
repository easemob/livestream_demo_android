package com.easemob.livedemo.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CustomConstraintLayout extends ConstraintLayout implements View.OnTouchListener {

    private boolean mScrolling;
    private float touchDownX;

    public OnGestureChangeListener listener;

    public CustomConstraintLayout(@NonNull Context context) {
        this(context, null);
    }

    public CustomConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setOnTouchListener(this);
        this.setLongClickable(true);

    }

    public void setOnGestureChangeListener(OnGestureChangeListener listener) {
        this.listener = listener;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                float dx = touchDownX - event.getX();

                if (dx < -ViewConfiguration.get(
                        getContext()).getScaledPagingTouchSlop()) {
                    // Fling enough to move left
                    if (null != listener) {
                        listener.scrollRight();
                    }
                    return true;
                } else if (dx > ViewConfiguration.get(
                        getContext()).getScaledPagingTouchSlop()) {
                    // Fling enough to move right

                    if (null != listener) {
                        listener.scrollLeft();
                    }
                    return true;
                } else {
                    return super.onTouchEvent(event);
                }
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                mScrolling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(touchDownX - event.getX()) >= ViewConfiguration.get(
                        getContext()).getScaledTouchSlop()) {
                    mScrolling = true;
                } else {
                    mScrolling = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                mScrolling = false;
                break;
        }
        return mScrolling;
    }

    public interface OnGestureChangeListener {
        void scrollLeft();

        void scrollRight();
    }

}
