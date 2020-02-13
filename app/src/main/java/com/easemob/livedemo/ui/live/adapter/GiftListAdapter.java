package com.easemob.livedemo.ui.live.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easemob.livedemo.R;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.ui.adapter.EaseBaseRecyclerViewAdapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class GiftListAdapter extends EaseBaseRecyclerViewAdapter<GiftBean> {
    private int selectedPosition = -1;

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new GiftViewHolder(LayoutInflater.from(mContext).inflate(R.layout.em_item_gift_list, parent, false));
    }

    private class GiftViewHolder extends ViewHolder<GiftBean> {
        private ImageView ivGift;
        private TextView tvGiftName;

        public GiftViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            ivGift = findViewById(R.id.iv_gift);
            tvGiftName = findViewById(R.id.tv_gift_name);
        }

        @Override
        public void setData(GiftBean item, int position) {
            ivGift.setImageResource(item.getResource());
            tvGiftName.setText(item.getName());
            if(selectedPosition == position) {
                item.setChecked(true);
                itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.em_gift_selected_shape));
            }else {
                item.setChecked(false);
                itemView.setBackground(null);
            }
        }
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

}
