package com.easemob.livedemo.ui.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.easemob.livedemo.common.callback.OnResourceParseCallback;
import com.easemob.livedemo.common.repository.Resource;

public abstract class BaseDialogFragment extends DialogFragment {
    public BaseActivity mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArgument();
        View view = inflater.inflate(getLayoutId(), container, false);
        setChildView(view);
        setDialogAttrs();
        setAnimation();
        return view;
    }

    protected void setAnimation() {

    }

    public void setChildView(View view) {
    }

    public abstract int getLayoutId();

    private void setDialogAttrs() {
        try {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initData();
        initViewModel();
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void initArgument() {
    }

    public void initView(Bundle savedInstanceState) {
    }

    public void initViewModel() {
    }

    public void initListener() {
    }

    public void initData() {
    }

    protected <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if (mContext != null) {
            mContext.parseResource(response, callback);
        }
    }

    public void setDialogParams() {
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.dimAmount = 0.6f;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            setDialogParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDialogParams(WindowManager.LayoutParams layoutParams) {
        try {
            Window dialogWindow = getDialog().getWindow();
            dialogWindow.setAttributes(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
