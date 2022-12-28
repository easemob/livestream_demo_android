package com.easemob.livedemo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.easemob.livedemo.R;


public class SearchEditText extends AppCompatEditText {
    private Context mContext;
    private float mLeftHeight;
    private float mLeftWidth;
    private float mRightHeight;
    private float mRightWidth;
    private float DEFAULT_SIZE = dip2px(18);
    private int DEFAULT_DRAWABLE_PADDING = (int) dip2px(6);
    private Drawable left, top;
    private Drawable right, bottom;

    public SearchEditText(Context context) {
        this(context, null);
    }

    public SearchEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SearchEditText);
            mLeftHeight = ta.getDimension(R.styleable.SearchEditText_search_edit_drawable_left_height, DEFAULT_SIZE);
            mLeftWidth = ta.getDimension(R.styleable.SearchEditText_search_edit_drawable_left_width, DEFAULT_SIZE);
            mRightHeight = ta.getDimension(R.styleable.SearchEditText_search_edit_drawable_right_height, 0);
            mRightWidth = ta.getDimension(R.styleable.SearchEditText_search_edit_drawable_right_width, 0);
            ta.recycle();
        }
        setGravity(Gravity.CENTER_VERTICAL);
        CharSequence hint = getHint();
        if (TextUtils.isEmpty(hint)) {
            setHint(getResources().getString(R.string.search_text_hint));
            setHintTextColor(this.getResources().getColor(R.color.color_search_hint));
        }
        setDrawable();
        setTextColor(this.getResources().getColor(R.color.color_search_text));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (left != null && (mLeftWidth != 0 && mLeftHeight != 0)) {
            left.setBounds(0, 0, (int) mLeftWidth, (int) mLeftHeight);
        }
        if (right != null && (mRightWidth != 0 && mRightHeight != 0)) {
            right.setBounds(0, 0, (int) mRightWidth, (int) mRightHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setCompoundDrawables(
                left,
                top,
                right,
                bottom
        );
    }

    private void setDrawable() {
        // If have non-compat relative drawables, then ignore leftCompat/rightCompat
        if (Build.VERSION.SDK_INT >= 17) {
            final Drawable[] existingRel = getCompoundDrawablesRelative();
            if (existingRel[0] != null || existingRel[2] != null) {
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                        existingRel[0],
                        existingRel[1],
                        existingRel[2],
                        existingRel[3]
                );
                return;
            }
        }
        // No relative drawables, so just set any compat drawables
        Drawable[] existingAbs = getCompoundDrawables();
        left = existingAbs[0];
        top = existingAbs[1];
        right = existingAbs[2];
        bottom = existingAbs[3];
        if (left == null) {
            left = ContextCompat.getDrawable(mContext, R.drawable.search);
        }
        if (right == null) {
            right = ContextCompat.getDrawable(mContext, R.drawable.search_delete);
        }
        if (left != null && (mLeftWidth != 0 && mLeftHeight != 0)) {
            left.setBounds(0, 0, (int) mLeftWidth, (int) mLeftHeight);
        }
        if (right != null && (mRightWidth != 0 && mRightHeight != 0)) {
            right.setBounds(0, 0, (int) mRightWidth, (int) mRightHeight);
        }
        setCompoundDrawables(
                left != null ? left : existingAbs[0],
                existingAbs[1],
                right != null ? right : existingAbs[2],
                existingAbs[3]
        );

        Drawable background = getBackground();
        if (background == null) {
            background = ContextCompat.getDrawable(mContext, R.drawable.common_search_bg);
            setBackground(background);
        }

        int drawablePadding = getCompoundDrawablePadding();
        if (drawablePadding == 0) {
            drawablePadding = DEFAULT_DRAWABLE_PADDING;
            setCompoundDrawablePadding(drawablePadding);
        }

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        if (paddingLeft == 0 || paddingRight == 0) {
            setPadding((int) dip2px(10), paddingTop, (int) dip2px(20), paddingBottom);
        }

    }

    private float dip2px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getContext().getResources().getDisplayMetrics());
    }
}
