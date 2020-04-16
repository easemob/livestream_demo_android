package com.easemob.livedemo.ui.widget.barrage;

/**
 * ViewHolder click Listener
 *
 * Created by wangjie on 2019/3/12.
 * 项目地址：https://github.com/mCyp/Muti-Barrage
 */

public interface AdapterListener<T> {
    // 点击事件
    void onItemClick(BarrageAdapter.BarrageViewHolder<T> holder, T item);
}
