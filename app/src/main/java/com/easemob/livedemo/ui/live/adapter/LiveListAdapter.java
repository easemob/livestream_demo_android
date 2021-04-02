package com.easemob.livedemo.ui.live.adapter;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easemob.livedemo.DemoConstants;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.base.EaseBaseRecyclerViewAdapter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;

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
            author.setVisibility(View.VISIBLE);
            audienceNum.setVisibility(View.VISIBLE);
            if(isOngoing) {
                tvOngoingStatus.setVisibility(View.VISIBLE);
                tvStatus.setVisibility(View.GONE);
            }else {
                tvOngoingStatus.setVisibility(View.GONE);
                tvStatus.setVisibility(View.VISIBLE);
                String status = liveRoom.getStatus();
                if(DemoHelper.isLiving(status)) {
//                    if(!TextUtils.equals(liveRoom.getOwner(), EMClient.getInstance().getCurrentUser())) {
                        groupLived.setVisibility(View.VISIBLE);
                        tvStatus.setVisibility(View.GONE);
//                    }else {
//                        groupLived.setVisibility(View.GONE);
//                        tvStatus.setVisibility(View.VISIBLE);
//                        tvStatus.setText(R.string.em_live_list_item_continue);
//                        tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.em_color_warning));
//                    }

                }else {
                    groupLived.setVisibility(View.GONE);
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText(R.string.em_live_list_item_open);
                    tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    author.setVisibility(View.GONE);
                    audienceNum.setVisibility(View.GONE);
                }
            }
            author.setText(liveRoom.getName());
            String content = liveRoom.getAudienceNum() + "正在看";
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            builder.setSpan(new AbsoluteSizeSpan((int) EaseCommonUtils.dip2px(mContext, 18)), 0, content.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            audienceNum.setText(builder);

            Glide.with(mContext)
                    .load(liveRoom.getCover())
                    .placeholder(R.drawable.em_live_default_bg)
                    .into(photo);
        }
    }

    private int getMemberNum(int num) {
        return num > 0 ? num - 1 : num;
    }
}
