package com.easemob.livedemo.ui.live;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.base.BaseActivity;
import com.easemob.livedemo.ui.live.viewmodels.StreamViewModel;

/**
 * Created by wei on 2016/6/12.
 */
public abstract class LiveBaseActivity extends BaseActivity {
    protected static final String TAG = "LiveActivity";
    ImageView coverImage;
    protected LiveRoom liveRoom;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        liveRoom = (LiveRoom) getIntent().getSerializableExtra("liveroom");
        if(liveRoom == null) {
            finish();
            return;
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        onActivityCreated(savedInstanceState);
        initView();
        initListener();
        initData();
    }

    protected abstract void onActivityCreated(@Nullable Bundle savedInstanceState);

    protected void initView() {
        coverImage = findViewById(R.id.cover_image);
        Glide.with(mContext)
                .load(liveRoom.getCover())
                .error(R.drawable.em_live_default_bg)
                .into(coverImage);
    }

    protected void initListener() {}

    protected void initData() {
    }
}
