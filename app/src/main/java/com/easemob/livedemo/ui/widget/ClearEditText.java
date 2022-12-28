package com.easemob.livedemo.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.Objects;

import com.easemob.livedemo.R;

public class ClearEditText extends AppCompatEditText implements View.OnFocusChangeListener,
        TextWatcher {

    private Drawable mClearDrawable;

    private boolean hasFoucs;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init() {
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(
                    R.drawable.search_delete);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
                mClearDrawable.getIntrinsicHeight());
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        if (paddingLeft == 0 || paddingRight == 0) {
            setPadding((int) dip2px(5), paddingTop, (int) dip2px(0), paddingBottom);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (getCompoundDrawables()[2] != null && Objects.requireNonNull(getText()).length() > 0) {
                    boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                            && (event.getX() < ((getWidth() - getPaddingRight())));
                    setIconChange(R.drawable.search_delete);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (getCompoundDrawables()[2] != null && Objects.requireNonNull(getText()).length() > 0) {
                    boolean touchable = (event.getX() > (getWidth() - getTotalPaddingRight()) && (event
                            .getX() < ((getWidth() - getPaddingRight()))))
                            && (event.getY() > 0 && event.getY() < getHeight() + 100);
                    setIconChange(R.drawable.search_delete);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (getCompoundDrawables()[2] != null && Objects.requireNonNull(getText()).length() > 0) {
                    setIconChange(R.drawable.search_delete);
                    boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                            && (event.getX() < ((getWidth() - getPaddingRight())))
                            && (event.getY() > 0 && event.getY() < getHeight());

                    if (touchable) {
                        this.setText("");
                    }
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearIconVisible(Objects.requireNonNull(getText()).length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setIconChange(int res) {
        mClearDrawable = getResources().getDrawable(res);

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
                mClearDrawable.getIntrinsicHeight());
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], mClearDrawable,
                getCompoundDrawables()[3]);

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private float dip2px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getContext().getResources().getDisplayMetrics());
    }

}
