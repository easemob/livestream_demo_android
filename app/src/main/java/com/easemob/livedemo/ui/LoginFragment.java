package com.easemob.livedemo.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.callback.OnResourceParseCallback;
import com.easemob.livedemo.common.utils.CustomCountDownTimer;
import com.easemob.livedemo.common.utils.PhoneNumberUtils;
import com.easemob.livedemo.databinding.FragmentLoginBinding;
import com.easemob.livedemo.ui.base.BaseLiveFragment;
import com.easemob.livedemo.ui.other.viewmodels.LoginViewModel;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseEditTextUtils;
import com.hyphenate.util.EMLog;

public class LoginFragment extends BaseLiveFragment implements TextWatcher, View.OnClickListener, TextView.OnEditorActionListener {

    private FragmentLoginBinding binding;
    private LoginViewModel mViewModel;
    private Drawable clear;
    private String mUserPhone;
    private String mCode;
    private CustomCountDownTimer countDownTimer;

    @Override
    protected View getLayoutView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentLoginBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void initListener() {
        super.initListener();
        binding.etLoginPhone.addTextChangedListener(this);
        binding.etLoginCode.addTextChangedListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.tvGetCode.setOnClickListener(this);
        binding.etLoginCode.setOnEditorActionListener(this);
        EaseEditTextUtils.clearEditTextListener(binding.etLoginPhone);
    }

    @Override
    protected void initData() {
        super.initData();
        if(!TextUtils.isEmpty(EMClient.getInstance().getCurrentUser())) {
            binding.etLoginPhone.setText(EMClient.getInstance().getCurrentUser());
        }

        binding.tvAgreement.setText(getSpannable());
        binding.tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());

        clear = ResourcesCompat.getDrawable(getResources(), R.drawable.d_clear, null);
        EaseEditTextUtils.showRightDrawable(binding.etLoginPhone, clear);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        mViewModel.getLoginFromAppServeObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    EMClient.getInstance().loginWithToken(mUserPhone, data, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            //跳转到主页
                            startActivity(new Intent(mContext, MainActivity.class));
                            mContext.finish();
                        }

                        @Override
                        public void onError(int code, String error) {
                            EMLog.e("login", "error: "+error);
                        }
                    });
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    if(code == EMError.USER_AUTHENTICATION_FAILED) {
                        showToast(mContext.getString(R.string.login_error_user_authentication_failed));
                    }else {
                        showToast(message);
                    }
                }
            });
        });

        mViewModel.getVerificationCodeObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    if(!mContext.isFinishing() && countDownTimer != null) {
                        countDownTimer.start();
                        showToast(mContext.getString(R.string.login_post_code));
                    }
                }

            });
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mUserPhone = binding.etLoginPhone.getText().toString().trim();
        mCode = binding.etLoginCode.getText().toString().trim();
        EaseEditTextUtils.showRightDrawable(binding.etLoginPhone, clear);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login :
                hideKeyboard();
                loginToServer();
                break;
            case R.id.tv_get_code :
                getVerificationCode();
                break;
        }
    }

    private SpannableString getSpannable() {
        SpannableString spanStr = new SpannableString(getString(R.string.login_agreement));
        int start1 = 5;
        int end1 = 13;
        int start2 = 14;
        int end2 = spanStr.length();

        spanStr.setSpan(new MyClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                jumpToAgreement();
            }
        }, start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new ForegroundColorSpan(Color.WHITE), start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //spanStr.setSpan(new UnderlineSpan(), 10, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new MyClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                jumpToProtocol();
            }
        }, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new ForegroundColorSpan(Color.WHITE), start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    private void loginToServer() {
        if(TextUtils.isEmpty(mUserPhone)) {
            showToast(mContext.getString(R.string.login_phone_empty));
            return;
        }
        if(!PhoneNumberUtils.isPhoneNumber(mUserPhone)) {
            showToast(mContext.getString(R.string.login_phone_illegal));
            return;
        }
        if(TextUtils.isEmpty(mCode)) {
            showToast(mContext.getString(R.string.login_code_empty));
            return;
        }
        if(!PhoneNumberUtils.isNumber(mCode)) {
            showToast(mContext.getString(R.string.login_illegal_code));
            return;
        }
        if(!binding.cbSelect.isChecked()) {
            showToast(mContext.getString(R.string.login_not_select_agreement));
            return;
        }
        mViewModel.loginFromAppServe(mUserPhone, mCode);
    }

    private void getVerificationCode() {
        if (TextUtils.isEmpty(mUserPhone)){
            showToast(mContext.getString(R.string.login_phone_empty));
            return;
        }
        if(!PhoneNumberUtils.isPhoneNumber(mUserPhone)) {
            showToast(mContext.getString(R.string.login_phone_illegal));
            return;
        }
        if(countDownTimer == null) {
            countDownTimer = new CustomCountDownTimer(binding.tvGetCode, 60000, 1000);
        }
        mViewModel.postVerificationCode(mUserPhone);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE) {
            if(!TextUtils.isEmpty(mUserPhone) && !TextUtils.isEmpty(mCode)) {
                hideKeyboard();
                loginToServer();
                return true;
            }
        }
        return false;
    }

    private void jumpToAgreement() {
        Uri uri = Uri.parse("http://www.easemob.com/agreement");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    private void jumpToProtocol() {
        Uri uri = Uri.parse("http://www.easemob.com/protocol");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    private abstract class MyClickableSpan extends ClickableSpan {

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.bgColor = Color.TRANSPARENT;
        }
    }
}
