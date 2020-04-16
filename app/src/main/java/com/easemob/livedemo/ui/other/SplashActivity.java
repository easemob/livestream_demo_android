package com.easemob.livedemo.ui.other;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.common.PreferenceManager;
import com.easemob.livedemo.data.UserRepository;
import com.easemob.livedemo.data.model.User;
import com.easemob.livedemo.ui.MainActivity;
import com.easemob.livedemo.ui.base.BaseLiveActivity;
import com.easemob.livedemo.ui.other.viewmodels.LoginViewModel;
import com.hyphenate.chat.EMClient;

public class SplashActivity extends BaseLiveActivity {
    private ImageView ivIcon;
    private TextView tvWelcome;
    private LoginViewModel viewModel;

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
        //初始化demo user数据
        UserRepository.getInstance().init(mContext);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

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
            DemoHelper.saveUserId();
            DemoHelper.initDb();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            if(DemoHelper.isCanRegister()) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }else {
                //创建临时账号
                createRandomUser();
            }

        }
    }

    private void createRandomUser() {
        ProgressDialog pd = new ProgressDialog(mContext);
        pd.setMessage("请稍等...");
        pd.setCanceledOnTouchOutside(false);

        viewModel.getLoginObservable().observe(mContext, response -> {
            parseResource(response, new OnResourceParseCallback<User>() {
                @Override
                public void onSuccess(User data) {
                    skipToTarget();
                }

                @Override
                public void onLoading() {
                    super.onLoading();
                    pd.show();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    pd.dismiss();
                }
            });
        });

        viewModel.login();
    }
}
