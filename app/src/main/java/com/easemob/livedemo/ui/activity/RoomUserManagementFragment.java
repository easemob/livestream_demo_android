package com.easemob.livedemo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easemob.livedemo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomUserManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomUserManagementFragment extends Fragment {
    private ManagementType type;


    public RoomUserManagementFragment() {
        // Required empty public constructor
    }

    public static RoomUserManagementFragment newInstance(ManagementType type) {
        RoomUserManagementFragment fragment = new RoomUserManagementFragment();
        Bundle args = new Bundle();
        args.putSerializable("ManagementType", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = (ManagementType) getArguments().getSerializable("ManagementType");
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room_user_management, container, false);
    }

    public enum ManagementType {
        REMOVE_ADMIN,
        NO_TALK,
        REMOVE_BLACKLIST
    }
}
