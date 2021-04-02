package com.easemob.livedemo.ui;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.easemob.livedemo.R;
import com.easemob.livedemo.ui.base.BaseFragment;
import com.easemob.livedemo.ui.fast.FastLivingListActivity;
import com.easemob.livedemo.ui.live.LivingListActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;

public class VideoTypeFragment extends BaseFragment implements View.OnClickListener {
    private ConstraintLayout clLive;
    private ConstraintLayout clFastLive;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_type, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
    }

    private void initView() {
        clLive = findViewById(R.id.cl_live);
        clFastLive = findViewById(R.id.cl_fast_live);

        setImageDimen();
    }

    private void setImageDimen() {
        float[] screenInfo = EaseCommonUtils.getScreenInfo(mContext);
        float width = screenInfo[0];
        if(width <= 0) {
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeResource(getResources(), R.drawable.live_normal_bg, options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        if(imageWidth <= 0 || imageHeight <= 0) {
            return;
        }
        //获取条目控件在当前屏幕上的宽和高
        float itemRealWidth = width - getResources().getDimension(R.dimen.home_item_width_margin) * 2;
        float itemRealHeight = itemRealWidth / imageWidth * imageHeight;


        ViewGroup.LayoutParams params = clLive.getLayoutParams();
        params.height = (int) itemRealHeight;
        params.width = (int) itemRealWidth;

        ViewGroup.LayoutParams FastParams = clFastLive.getLayoutParams();
        FastParams.height = (int) itemRealHeight;
        FastParams.width = (int) itemRealWidth;
    }

    private void initListener() {
        clLive.setOnClickListener(this);
        clFastLive.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_live :
                LivingListActivity.actionStart(mContext);
                break;
            case R.id.cl_fast_live :
                FastLivingListActivity.actionStart(mContext);
                break;
        }
    }
}

