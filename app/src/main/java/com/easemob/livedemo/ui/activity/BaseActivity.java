package com.easemob.livedemo.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.utils.Utils;

/**
 * Created by wei on 2016/5/30.
 */
public class BaseActivity extends AppCompatActivity{
    private Toolbar mActionBarToolbar;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }
    /**
     * getInstance the actionbar(toolbar) which view id is R.id.toolbar_actionbar
     */
    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                mActionBarToolbar.setTitleTextColor(getResources().getColor(R.color.colorTextPrimary));
                if(allowBack){
                    mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }
        }
        return mActionBarToolbar;
    }

    protected <T> void executeTask(ThreadPoolManager.Task<T> task){
        ThreadPoolManager.getInstance().executeTask(task);
    }

    protected void executeRunnable(Runnable runnable){
        ThreadPoolManager.getInstance().executeRunnable(runnable);
    }

    protected void showToast(final String toastContent){
        Utils.showToast(this, toastContent);
    }

    protected void showLongToast(final String toastContent){
        Utils.showLongToast(this, toastContent);
    }

    protected boolean allowBack = true;

    private ProgressDialog progressDialog;
    protected void showProgressDialog(String message){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
    protected void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
