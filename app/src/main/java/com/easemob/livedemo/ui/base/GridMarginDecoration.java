/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.easemob.livedemo.ui.base;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

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
        int spanCount = ((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
        int childCount = parent.getAdapter().getItemCount();
        //最左边没有padding
        if(pos % spanCount == 0){
            outRect.left = 0;
        }
        //最右边也没有padding
        if(pos % spanCount == spanCount - 1) {
            outRect.right = 0;
        }
        //最上边也没有padding
        if(pos < spanCount) {
            outRect.top = 0;
        }
        //在最下边也没有padding，此处需要分情况
        int rows = childCount % spanCount == 0 ? childCount/spanCount : childCount/spanCount + 1;
        int rowNum = pos % spanCount == 0 ? pos/spanCount + 1: (int) Math.floor(pos / spanCount) + 1;
        if(rowNum == rows) {
            outRect.bottom = 0;
        }else {
            int lastRowChildCount = (childCount - 1) % spanCount;
            if(rowNum == rows - 1 && pos % spanCount > lastRowChildCount) {
                outRect.bottom = 0;
            }
        }

    }
}
