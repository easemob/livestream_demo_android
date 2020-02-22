package com.easemob.livedemo.ui.activity;

import android.content.Intent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.PreferenceManager;
import com.hyphenate.chat.EMClient;

public class SplashActivity extends BaseLiveActivity {
    private ImageView ivIcon;
    private TextView tvWelcome;

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_splash;
    }

    @Override
    protected void initView() {
        super.initView();
        ivIcon = findViewById(R.id.iv_icon);
        tvWelcome = findViewById(R.id.tv_welcome);
    }

    @Override
    protected void initData() {
        super.initData();
        AlphaAnimation animation = new AlphaAnimation(0, 1f);
        animation.setDuration(500);
        ivIcon.startAnimation(animation);
        tvWelcome.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                skipToTarget();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void skipToTarget() {
        //登录过
        if(EMClient.getInstance().isLoggedInBefore()){
            PreferenceManager.init(mContext, EMClient.getInstance().getCurrentUser());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            //创建临时账号
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
