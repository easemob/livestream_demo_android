package com.easemob.livedemo.ui.other.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.easemob.livedemo.ui.live.adapter.FragmentAdapter;
import com.easemob.livedemo.ui.live.fragment.LiveListFragment;
import com.easemob.livedemo.ui.other.SearchActivity;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.easemob.livedemo.R;

/**
 * Created by wei on 2016/5/27.
 * 直播广场
 */
public class LiveSquareFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViewPager();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tab_indicator));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(){
        FragmentAdapter adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new LiveListFragment(), "热门");
        adapter.addFragment(new LiveListFragment(), "女神");
        adapter.addFragment(new LiveListFragment(), "男神");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
