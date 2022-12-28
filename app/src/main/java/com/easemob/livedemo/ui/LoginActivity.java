package com.easemob.livedemo.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.easemob.livedemo.R;
import com.easemob.livedemo.databinding.ActivityLoginBinding;
import com.easemob.livedemo.ui.base.BaseLiveActivity;


public class LoginActivity extends BaseLiveActivity {

    private ActivityLoginBinding binding;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected View getContentView() {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initData() {
        super.initData();
        getSupportFragmentManager().beginTransaction().replace(R.id.fcv_fragment, new LoginFragment()).commit();
    }
}
