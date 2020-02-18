package com.easemob.livedemo.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.adapter.EaseBaseRecyclerViewAdapter;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;

public class LiveListAdapter extends EaseBaseRecyclerViewAdapter<LiveRoom> {
    private String status;
    private boolean isOngoing;

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.layout_livelist_item, parent, false);
        return new PhotoViewHolder(view);
    }

    public void setStatus(String status) {
        this.status = status;
        isOngoing = !TextUtils.isEmpty(status) && TextUtils.equals(status, DemoConstants.LIVE_ONGOING);
    }

    private class PhotoViewHolder extends ViewHolder<LiveRoom> {
        private ImageView photo;
        private TextView author;
        private TextView audienceNum;
        private TextView tvOngoingStatus;
        private TextView tvStatus;
        private Group groupLived;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            photo = findViewById(R.id.photo);
            author = findViewById(R.id.author);
            audienceNum = findViewById(R.id.audience_num);
            tvOngoingStatus = findViewById(R.id.tv_ongoing_status);
            tvStatus = findViewById(R.id.tv_status_unactivite);
            groupLived = findViewById(R.id.group_lived);
        }

        @Override
        public void setData(LiveRoom liveRoom, int position) {
            if(isOngoing) {
                tvOngoingStatus.setVisibility(View.VISIBLE);
                tvStatus.setVisibility(View.GONE);
            }else {
                tvOngoingStatus.setVisibility(View.GONE);
                tvStatus.setVisibility(View.VISIBLE);
                String status = liveRoom.getStatus();
                if(!TextUtils.isEmpty(status) && TextUtils.equals(status, DemoConstants.LIVE_ONGOING)) {
                    groupLived.setVisibility(View.VISIBLE);
                    tvStatus.setVisibility(View.GONE);
                }else {
                    groupLived.setVisibility(View.GONE);
                    tvStatus.setVisibility(View.VISIBLE);
                }
            }
            author.setText(liveRoom.getName());
            audienceNum.setText(liveRoom.getAudienceNum() + "正在看");
            Glide.with(mContext)
                    .load(liveRoom.getCover())
                    .placeholder(R.drawable.em_live_default_bg)
                    .into(photo);
        }
    }
}
