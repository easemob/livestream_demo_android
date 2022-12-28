package com.easemob.livedemo.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.easemob.livedemo.common.callback.OnResourceParseCallback;
import com.easemob.livedemo.common.enums.Status;
import com.easemob.livedemo.common.repository.Resource;
import com.easemob.livedemo.common.utils.DemoHelper;
import com.easemob.livedemo.common.utils.ThreadManager;
import com.easemob.livedemo.utils.StatusBarCompat;
import com.easemob.livedemo.utils.ThreadPoolManager;
import com.easemob.livedemo.utils.Utils;
import com.hyphenate.easeui.EaseIM;

import com.easemob.livedemo.R;

public class BaseActivity extends AppCompatActivity {
    private Toolbar mActionBarToolbar;
    public BaseActivity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        DemoHelper.setDefaultLanguage(mContext,"en");
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        if (null == EaseIM.getInstance().getUserProvider()) {
            DemoHelper.init();
        }
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    /**
     * getInstance the actionbar(toolbar) which view id is R.id.toolbar_actionbar
     */
    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                mActionBarToolbar.setTitleTextColor(getResources().getColor(R.color.colorTextPrimary));
                if (allowBack) {
                    mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }
        }
        return mActionBarToolbar;
    }

    protected <T> void executeTask(ThreadPoolManager.Task<T> task) {
        ThreadPoolManager.getInstance().executeTask(task);
    }

    protected void executeRunnable(Runnable runnable) {
        ThreadPoolManager.getInstance().executeRunnable(runnable);
    }

    public void showToast(final String toastContent) {
        Utils.showToast(this, toastContent);
    }

    public void showLongToast(final String toastContent) {
        Utils.showLongToast(this, toastContent);
    }

    protected boolean allowBack = true;

    private ProgressDialog progressDialog;

    protected void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void setFitSystemForTheme(boolean fitSystemForTheme) {
        setFitSystemForTheme(fitSystemForTheme, R.color.black);
        setStatusBarTextColor(true);
    }

    public void setFitSystemForTheme(boolean fitSystemForTheme, @ColorRes int colorId) {
        StatusBarCompat.setFitSystemForTheme(this, fitSystemForTheme, ContextCompat.getColor(mContext, colorId));
    }

    /**
     * modify status bar color
     *
     * @param isLight whether is light
     */
    public void setStatusBarTextColor(boolean isLight) {
        StatusBarCompat.setLightStatusBar(mContext, !isLight);
    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                super.onBackPressed();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * parse resource<T>
     *
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if (response == null) {
            return;
        }
        if (response.status == Status.SUCCESS) {
            callback.hideLoading();
            callback.onSuccess(response.data);
        } else if (response.status == Status.ERROR) {
            ThreadManager.getInstance().runOnMainThread(() -> {
                callback.hideLoading();
                if (!callback.hideErrorMsg) {
                    showToast(response.getMessage());
                }
                callback.onError(response.errorCode, response.getMessage());
            });

        } else if (response.status == Status.LOADING) {
            callback.onLoading();
        }
    }
}
