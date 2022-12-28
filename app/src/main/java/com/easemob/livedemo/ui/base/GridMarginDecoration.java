package com.easemob.livedemo.ui.base;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeui.utils.EaseCommonUtils;


public class GridMarginDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public GridMarginDecoration(Context context, int space) {
        this.space = (int) (EaseCommonUtils.dip2px(context, space) / 2);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.right = space;
        outRect.bottom = space;
        outRect.left = space;
        outRect.top = space;

        int pos = parent.getChildAdapterPosition(view);
        int spanCount = 0;
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
            int childCount = parent.getAdapter().getItemCount();

            if (pos % spanCount == 0) {
                outRect.left = 0;
            }

            if (pos % spanCount == spanCount - 1) {
                outRect.right = 0;
            }

            if (pos < spanCount) {
                outRect.top = 0;
            }

            int rows = childCount % spanCount == 0 ? childCount / spanCount : childCount / spanCount + 1;
            int rowNum = pos % spanCount == 0 ? pos / spanCount + 1 : (int) Math.floor(pos / spanCount) + 1;
            if (rowNum == rows) {
                outRect.bottom = 0;
            } else {
                int lastRowChildCount = (childCount - 1) % spanCount;
                if (rowNum == rows - 1 && pos % spanCount > lastRowChildCount) {
                    outRect.bottom = 0;
                }
            }
        }

    }
}
