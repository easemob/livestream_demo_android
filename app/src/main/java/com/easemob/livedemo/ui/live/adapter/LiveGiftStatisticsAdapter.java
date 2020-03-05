package com.easemob.livedemo.ui.live.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.db.dao.ReceiveGiftDao;
import com.easemob.livedemo.common.db.entity.ReceiveGiftEntity;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.ui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.widget.EaseImageView;

import androidx.annotation.NonNull;

public class LiveGiftStatisticsAdapter extends EaseBaseRecyclerViewAdapter<ReceiveGiftEntity> {

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.em_layout_live_gift_statistics_item, parent, false);
        return new GiftViewHolder(view);
    }

    private class GiftViewHolder extends ViewHolder<ReceiveGiftEntity> {
        private EaseImageView imgAvatar;
        private TextView txtUsernick;
        private TextView tvGiftInfo;
        private ImageView ivGiftIcon;

        public GiftViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            imgAvatar = findViewById(R.id.img_avatar);
            txtUsernick = findViewById(R.id.txt_usernick);
            tvGiftInfo = findViewById(R.id.tv_gift_info);
            ivGiftIcon = findViewById(R.id.iv_gift_icon);
        }

        @Override
        public void setData(ReceiveGiftEntity item, int position) {
            String giftId = item.getGift_id();
            GiftBean giftBean = DemoHelper.getGiftById(giftId);
            ivGiftIcon.setImageResource(giftBean.getResource());
            tvGiftInfo.setText(mContext.getString(R.string.em_live_gift_gift_info, item.getGift_num()));

            txtUsernick.setText(mContext.getString(R.string.em_live_gift_sender_info, DemoHelper.getNickName(item.getFrom())));
            imgAvatar.setImageResource(DemoHelper.getAvatarResource(item.getFrom()));

        }
    }
}
