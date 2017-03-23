package com.easemob.livedemo.ui.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.easemob.livedemo.R;

/**
 * Created by wei on 2017/3/3.
 */

public class RoomUserManagementDialog extends DialogFragment {

    private String chatroomId;
    TabLayout tabLayout;
    ViewPager viewPager;

    public RoomUserManagementDialog(){}

    public RoomUserManagementDialog(String chatroomId){
        this.chatroomId = chatroomId;
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

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tabLayout = (TabLayout) getView().findViewById(R.id.tabs);
        viewPager = (ViewPager) getView().findViewById(R.id.viewpager);


        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager() {
        FragmentAdapter adapter = new FragmentAdapter(getChildFragmentManager());
        adapter.addFragment(RoomUserManagementFragment.newInstance(chatroomId,
                RoomUserManagementFragment.ManagementType.ADMIN), "房管");
        adapter.addFragment(RoomUserManagementFragment.newInstance(chatroomId,
                RoomUserManagementFragment.ManagementType.MUTE), "禁言");
        adapter.addFragment(RoomUserManagementFragment.newInstance(chatroomId,
                RoomUserManagementFragment.ManagementType.BLACKLIST), "黑名单");
        viewPager.setAdapter(adapter);
    }
}
