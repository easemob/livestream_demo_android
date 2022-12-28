package com.easemob.livedemo.ui.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseLiveFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        View view;
        if(layoutId == 0) {
            view = getLayoutView(inflater, container);
        }else {
            view = inflater.inflate(layoutId, container, false);
        }
        initArgument();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initViewModel();
        initListener();
        initData();
    }

    protected View getLayoutView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return null;
    }

    protected int getLayoutId() {
        return 0;
    }

    protected void initArgument() {
    }

    protected void initView(Bundle savedInstanceState) {
        Log.e("TAG", "fragment = " + this.getClass().getSimpleName());
    }

    protected void initViewModel() {
    }

    protected void initListener() {
    }

    protected void initData() {
    }

    protected <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }
}
