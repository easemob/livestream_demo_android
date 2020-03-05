package com.easemob.livedemo.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.easemob.livedemo.common.OnResourceParseCallback;
import com.easemob.livedemo.common.reponsitories.Resource;
import com.easemob.livedemo.ui.viewmodels.UserManageViewModel;
import com.google.android.material.tabs.TabLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.easemob.livedemo.R;

import java.util.List;

/**
 * Created by wei on 2017/3/3.
 */

public class RoomUserManagementDialog extends DialogFragment {
    private BaseActivity mContext;
    private String chatroomId;
    TabLayout tabLayout;
    ViewPager viewPager;
    private FragmentAdapter adapter;
    private UserManageViewModel viewModel;

    public RoomUserManagementDialog(){}

    public RoomUserManagementDialog(String chatroomId){
        this.chatroomId = chatroomId;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (BaseActivity) context;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dialog_room_user_management, container, false);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = (TabLayout) getView().findViewById(R.id.tabs);
        viewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        initViewModel();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(mContext).get(UserManageViewModel.class);
        viewModel.getObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    String title = getString(R.string.em_live_user_manage_users, data.size());
                    adapter.getTitles().remove(0);
                    adapter.getTitles().add(0, title);
                    adapter.notifyDataSetChanged();
                }
            });
        });
        viewModel.getWhitesObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    String title = getString(R.string.em_live_user_manage_white, data.size());
                    adapter.getTitles().remove(1);
                    adapter.getTitles().add(1, title);
                    adapter.notifyDataSetChanged();
                }
            });
        });
        viewModel.getMuteObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    String title = getString(R.string.em_live_user_manage_mute, data.size());
                    adapter.getTitles().remove(2);
                    adapter.getTitles().add(2, title);
                    adapter.notifyDataSetChanged();
                }
            });
        });
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);

        viewModel.getMembers(chatroomId);
        viewModel.getWhiteList(chatroomId);
        viewModel.getMuteList(chatroomId);
    }

    private void setupViewPager() {
        adapter = new FragmentAdapter(getChildFragmentManager());
        adapter.addFragment(RoomUserManagementFragment.newInstance(chatroomId,
                RoomUserManagementFragment.ManagementType.MEMBER), "观众");
        adapter.addFragment(RoomUserManagementFragment.newInstance(chatroomId,
                RoomUserManagementFragment.ManagementType.BLACKLIST), "白名单");
        adapter.addFragment(RoomUserManagementFragment.newInstance(chatroomId,
                RoomUserManagementFragment.ManagementType.MUTE), "用户禁言");
        viewPager.setAdapter(adapter);
    }

    /**
     * 解析Resource<T>
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if(mContext != null) {
            mContext.parseResource(response, callback);
        }
    }
}
