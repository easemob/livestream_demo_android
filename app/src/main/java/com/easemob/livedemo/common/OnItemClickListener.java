package com.easemob.livedemo.common;

import android.view.View;

/**
 * 条目点击事件
 */
public interface OnItemClickListener {
    /**
     * 条目点击
     * @param view
     * @param position
     */
    void onItemClick(View view, int position);
}
