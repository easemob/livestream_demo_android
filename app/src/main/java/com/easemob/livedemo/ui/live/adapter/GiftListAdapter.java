package com.easemob.livedemo.ui.live.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;

import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.utils.Utils;

public class GiftListAdapter extends EaseBaseRecyclerViewAdapter<GiftBean> {
    private int selectedPosition = -1;

    @Override
    public GiftViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new GiftViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_gift_list, parent, false));
    }

    private class GiftViewHolder extends ViewHolder<GiftBean> {
        private ImageView ivGift;
        private TextView tvGiftName;
        private TextView tvGiftValue;
        private TextView tvLeftTime;
        private TextView tvLeftSTime;
        private View leftTimeBgView;

        public GiftViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            ivGift = findViewById(R.id.iv_gift);
            tvGiftName = findViewById(R.id.tv_gift_name);
            tvGiftValue = findViewById(R.id.iv_gift_gold_value);

            tvLeftTime = findViewById(R.id.left_time_tv);
            tvLeftSTime = findViewById(R.id.left_time_s_tv);
            tvLeftTime.setTypeface(Utils.getRobotoBlackItalicTypeface(mContext));
            tvLeftSTime.setTypeface(Utils.getRobotoBlackItalicTypeface(mContext));
            leftTimeBgView = findViewById(R.id.left_time_bg_view);
        }

        @Override
        public void setData(GiftBean item, int position) {
            ivGift.setImageResource(item.getResource());
            tvGiftName.setText(item.getName());
            tvGiftValue.setText(String.valueOf(item.getValue()));
            if (selectedPosition == position) {
                item.setChecked(true);
                itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gift_stroke));
            } else {
                item.setChecked(false);
                itemView.setBackground(null);
            }
            if (item.getLeftTime() > 0) {
                tvLeftTime.setVisibility(View.VISIBLE);
                tvLeftSTime.setVisibility(View.VISIBLE);
                tvLeftTime.setText(String.valueOf(item.getLeftTime()));
                leftTimeBgView.setVisibility(View.VISIBLE);
            } else {
                leftTimeBgView.setVisibility(View.GONE);
                tvLeftSTime.setVisibility(View.GONE);
                tvLeftTime.setVisibility(View.GONE);
            }
        }
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

}
