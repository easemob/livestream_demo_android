package com.easemob.livedemo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.easemob.livedemo.R;


public class ImageGroupView extends ConstraintLayout {

    private ImageView ivIcon;
    private View bgView;
    private int imageSrcId;

    public ImageGroupView(@NonNull Context context) {
        this(context, null);
    }

    public ImageGroupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageGroupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttrs(context, attrs);
    }

    private void initView(Context context) {
        bgView = LayoutInflater.from(context).inflate(R.layout.layout_image_inlude_bg, this, false);
        ivIcon = findViewById(R.id.iv_icon);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageGroupView);
        if (ta != null) {
            imageSrcId = ta.getResourceId(R.styleable.ImageGroupView_image_group_src, -1);
            if (imageSrcId != -1) {
                ivIcon.setImageResource(imageSrcId);
            }
        }
    }
}
