package com.easemob.livedemo.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.inf.OnConfirmClickListener;
import com.easemob.livedemo.databinding.ActivityLoginBinding;
import com.easemob.livedemo.ui.base.BaseLiveActivity;
import com.easemob.livedemo.ui.base.DemoDialogFragment;
import com.easemob.livedemo.ui.other.fragment.SimpleDialogFragment;
import com.hyphenate.EMError;


public class LoginActivity extends BaseLiveActivity {

    private ActivityLoginBinding binding;
    private int errorCode;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        errorCode = intent.getIntExtra("errorCode", 0);
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
        if(errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE || errorCode == EMError.USER_ALREADY_LOGIN_ANOTHER
        || errorCode == EMError.USER_KICKED_BY_OTHER_DEVICE || errorCode == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
            new SimpleDialogFragment.Builder(mContext)
                .dismissCancel(true)
                .setTitle(R.string.login_again_hint)
                .show();
        }
    }
}
