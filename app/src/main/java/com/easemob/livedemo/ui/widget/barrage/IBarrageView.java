package com.easemob.livedemo.ui.widget.barrage;

import android.view.View;


public interface IBarrageView {
    void addBarrageItem(View view);

    View getCacheView(int type);

    long getInterval();

    int getRepeat();
}
