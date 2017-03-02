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

package com.easemob.livedemo.ui;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridMarginDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public GridMarginDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.right = 0;
        outRect.bottom = 0;
        outRect.left = space;
        outRect.top = space;

        int pos = parent.getChildAdapterPosition(view) + 1;
        int spanCount = ((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
        int childCount = parent.getAdapter().getItemCount();
        if(pos == 0 || pos % spanCount == 1){
            outRect.left = 0;
        }
        int rows = childCount % spanCount == 0 ? childCount/spanCount : childCount/spanCount+1;
        int rowNum = pos % spanCount == 0 ? pos/spanCount : pos/spanCount+1;
        if(rowNum == rows) outRect.bottom = space;

    }
}
