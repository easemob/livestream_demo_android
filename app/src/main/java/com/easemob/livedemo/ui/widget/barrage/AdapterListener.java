package com.easemob.livedemo.ui.widget.barrage;


public interface AdapterListener<T> {
    void onItemClick(BarrageAdapter.BarrageViewHolder<T> holder, T item);
}
