package com.easemob.livedemo.ui.live.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.ui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.widget.EaseImageView;

import androidx.annotation.NonNull;

public class LiveGiftStatisticsAdapter extends EaseBaseRecyclerViewAdapter<GiftBean> {

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.em_layout_live_gift_statistics_item, parent, false);
        return new GiftViewHolder(view);
    }

    private class GiftViewHolder extends ViewHolder<GiftBean> {
        private EaseImageView imgAvatar;
        private TextView txtUsernick;
        private TextView tvGiftInfo;

        public GiftViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            imgAvatar = findViewById(R.id.img_avatar);
            txtUsernick = findViewById(R.id.txt_usernick);
            tvGiftInfo = findViewById(R.id.tv_gift_info);
        }

        @Override
        public void setData(GiftBean item, int position) {
            txtUsernick.setText(item.getUser().getNickname());
            SpannableString span = new SpannableString(mContext.getString(R.string.em_live_gift_sender_info, item.getNum()));
            ImageSpan imageSpan = new ImageSpan(mContext, R.drawable.live_button_present_normal, DynamicDrawableSpan.ALIGN_BOTTOM);
            span.setSpan(imageSpan, 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvGiftInfo.setText(span);
        }
    }
}
