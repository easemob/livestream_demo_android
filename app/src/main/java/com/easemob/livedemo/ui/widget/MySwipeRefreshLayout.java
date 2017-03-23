package com.easemob.livedemo.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import com.easemob.livedemo.R;

/**
 * Created by wei on 2017/3/13.
 */

public class MySwipeRefreshLayout extends SwipeRefreshLayout{
    public MySwipeRefreshLayout(Context context) {
        super(context);
        setProgressBarColor();
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setProgressBarColor();
    }

    private void setProgressBarColor(){
        setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
    }
}
