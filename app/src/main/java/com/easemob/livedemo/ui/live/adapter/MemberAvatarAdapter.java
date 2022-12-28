package com.easemob.livedemo.ui.live.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseImageView;

import com.easemob.livedemo.R;

public class MemberAvatarAdapter extends EaseBaseRecyclerViewAdapter<String> {
    public MemberAvatarAdapter() {
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.avatar_list_item, parent, false);
        return new AvatarViewHolder(view);
    }

    private class AvatarViewHolder extends ViewHolder<String> {
        private EaseImageView avatar;

        public AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            avatar = findViewById(R.id.avatar);
        }

        @Override
        public void setData(String item, int position) {
            try {
                EaseUserUtils.setUserAvatar(mContext, item, avatar);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
