package com.easemob.livedemo.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.easemob.livedemo.BuildConfig;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.callback.OnResourceParseCallback;
import com.easemob.livedemo.common.utils.CustomCountDownTimer;
import com.easemob.livedemo.common.utils.PhoneNumberUtils;
import com.easemob.livedemo.data.model.LoginResult;
import com.easemob.livedemo.databinding.FragmentLoginBinding;
import com.easemob.livedemo.ui.base.BaseLiveFragment;
import com.easemob.livedemo.ui.other.viewmodels.LoginViewModel;
import com.easemob.livedemo.utils.AESEncryptor;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseEditTextUtils;
import com.hyphenate.util.EMLog;

import org.json.JSONObject;

import java.util.Locale;

import javax.crypto.spec.SecretKeySpec;

public class LoginFragment extends BaseLiveFragment implements TextWatcher, View.OnClickListener, TextView.OnEditorActionListener {

    private FragmentLoginBinding binding;
    private LoginViewModel mViewModel;
    private Drawable clear;
    private String mUserPhone;
    private String mCode;
    private CustomCountDownTimer countDownTimer;
    private String TAG = getClass().getSimpleName();

    private final String VERIFY_CODE_URL = "https://downloadsdk.easemob.com/downloads/IMDemo/sms/index.html";

    @Override
    protected View getLayoutView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentLoginBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setupWebView();
    }

    @Override
    protected void initListener() {
        super.initListener();
        binding.etLoginPhone.addTextChangedListener(this);
        binding.etLoginCode.addTextChangedListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.tvGetCode.setOnClickListener(this);
        binding.etLoginCode.setOnEditorActionListener(this);
        binding.cslRoot.setOnClickListener(this);
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
            parseResource(response, new OnResourceParseCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult data) {
                    EMClient.getInstance().loginWithToken(data.getUsername(), data.getToken(), new EMCallBack() {
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
            case R.id.csl_root :
                hideKeyboard();
                makeVerifyCodeWebViewVisible(false);
                break;
        }
    }

    private SpannableString getSpannable() {
        String language = Locale.getDefault().getLanguage();
        boolean isZh = language.startsWith("zh");
        SpannableString spanStr = new SpannableString(getString(R.string.login_agreement));
        int start1 = 29;
        int end1 = 45;
        int start2 = 50;
        int end2 = spanStr.length();
        if(isZh) {
            start1 = 5;
            end1 = 13;
            start2 = 14;
            end2 = spanStr.length();
        }
        //设置下划线
        //spanStr.setSpan(new UnderlineSpan(), 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

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
        hideKeyboard();
        showVerifyCodeWebView(mUserPhone);
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

    @Override
    public void onDestroyView() {
        binding.webView.stopLoading();
        binding.webView.destroy();
        super.onDestroyView();
    }

    private void setupWebView() {
        if (binding != null && binding.webView != null && binding.webView.getSettings() != null) {
            WebSettings settings = binding.webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }

        if (binding != null && binding.webView != null) {
            binding.webView.addJavascriptInterface(new CaptchaJsInterface(), "android");
            binding.webView.setVerticalScrollBarEnabled(false);
            binding.webView.setHorizontalScrollBarEnabled(false);
            binding.webView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));

            binding.webView.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    float radius = dpToPx(4f);
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                }
            });
            binding.webView.setClipToOutline(true);
        }

        if (binding != null && binding.webView != null) {
            binding.webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (binding != null && binding.progressBar != null && binding.progressBar.getVisibility() == View.VISIBLE) {
                        binding.progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (binding != null && binding.progressBar != null) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    handleError("WEBVIEW_ERROR: " + error.getDescription());
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url != null) {
                        view.loadUrl(url);
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });
        }
    }

    private void showVerifyCodeWebView(String mUserPhone) {
        makeVerifyCodeWebViewVisible(true);
        if (binding != null && binding.webView != null) {
            binding.webView.loadUrl(VERIFY_CODE_URL + "?telephone=" + mUserPhone);
        }
    }

    private void makeVerifyCodeWebViewVisible(boolean visible) {
        if (binding == null) return;

        if (visible) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.webView.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.webView.setVisibility(View.GONE);
            // 隐藏时加载空白页面，清除视觉内容
            binding.webView.post(new Runnable() {
                @Override
                public void run() {
                    binding.webView.loadUrl("about:blank");
                }
            });
        }
    }

    class CaptchaJsInterface {

        @JavascriptInterface
        public void encryptData(String param) {
            // 1. 准备密钥
            byte[] keyBytes = Base64.decode(BuildConfig.SECRET_KEY, Base64.DEFAULT);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            String encryptedData = AESEncryptor.encrypt(param, secretKey);

            // 2. 切换到主线程回调JS
            if (binding != null && binding.webView != null) {
                binding.webView.post(new Runnable() {
                    @Override
                    public void run() {
                        String jsCallback = "window.encryptCallback('" + encryptedData + "')";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            binding.webView.evaluateJavascript(jsCallback, null);
                        }
                        Log.d(TAG, "encryptData: " + jsCallback);
                    }
                });
            }
        }

        @JavascriptInterface
        public void getVerifyResult(String verifyResult) {
            if (binding != null && binding.progressBar != null) {
                binding.progressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        makeVerifyCodeWebViewVisible(false);
                    }
                });
            }
            EMLog.d(TAG, "verifyResult = " + verifyResult);
            int code = -1;
            try {
                JSONObject json = new JSONObject(verifyResult);
                code = json.getInt("code");
                if (code == 200) {
                    handleSuccess();
                } else {
                    String errorInfo = json.getString("errorInfo");
                    handleError(code, errorInfo);
                }
            } catch (Exception e) {
                handleError(code, "PARSE_ERROR: " + e.getMessage());
            }
        }
    }

    private void handleSuccess() {
        if (binding != null && binding.webView != null) {
            binding.webView.post(new Runnable() {
                @Override
                public void run() {
                    if (countDownTimer != null) {
                        countDownTimer.start();
                    }
                    showToast(getString(R.string.login_post_code));
                }
            });
        }
    }

    private void handleError(int code, String errorInfo) {
        EMLog.e(TAG, "code = " + code + ",errorInfo = " + errorInfo);
        if (binding != null && binding.webView != null) {
            binding.webView.post(new Runnable() {
                @Override
                public void run() {
                    showToast(errorInfo);
                }
            });
        }
    }

    private void handleError(String errorInfo) {
        handleError(-1, errorInfo);
    }

    // Helper method to convert dp to pixels
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
