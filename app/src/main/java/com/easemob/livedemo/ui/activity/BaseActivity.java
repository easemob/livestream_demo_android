package com.easemob.livedemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;

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
     * get the actionbar(toolbar) which view id is R.id.toolbar_actionbar
     */
    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                mActionBarToolbar.setTitleTextColor(getResources().getColor(R.color.colorTextPrimary));
            }
        }
        return mActionBarToolbar;
    }

    protected <T> void executeTask(ThreadPoolManager.Task<T> task){
        ThreadPoolManager.getInstance().executeTask(task);
    }

    protected void showToast(final String toastContent){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(BaseActivity.this, toastContent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void showLongToast(final String toastContent){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(BaseActivity.this, toastContent, Toast.LENGTH_LONG).show();
            }
        });
    }
}
