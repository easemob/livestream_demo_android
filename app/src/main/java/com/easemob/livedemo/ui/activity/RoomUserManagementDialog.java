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

    TabLayout tabLayout;
    ViewPager viewPager;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_room_user_management, container, false);
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
        adapter.addFragment(RoomUserManagementFragment.newInstance(
                RoomUserManagementFragment.ManagementType.REMOVE_ADMIN), "房管");
        adapter.addFragment(RoomUserManagementFragment.newInstance(
                RoomUserManagementFragment.ManagementType.NO_TALK), "禁言");
        adapter.addFragment(RoomUserManagementFragment.newInstance(
                RoomUserManagementFragment.ManagementType.REMOVE_BLACKLIST), "黑名单");
        viewPager.setAdapter(adapter);
    }
    View view;
    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        //view = LayoutInflater.from(getActivity()).inflate(dialog_room_user_management, null);

        //Dialog dialog = super.onCreateDialog(savedInstanceState);
        Dialog dialog = new Dialog(getActivity(), R.style.room_user_details_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // must be called before set content
        dialog.setContentView(R.layout.dialog_room_user_management);

        //不设置不会占满全屏
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        return dialog;
    }
}
