package com.easemob.livedemo.common.inf;

import android.app.Activity;

import java.util.List;

public interface ActivityState {
    /**
     * get current Activity
     *
     * @return
     */
    Activity current();

    /**
     * get the list of activity
     *
     * @return
     */
    List<Activity> getActivityList();

    /**
     * The total number of Activities in the task stack
     *
     * @return
     */
    int count();

    /**
     * Determine if the app is in the foreground, that is, if it is visible
     *
     * @return
     */
    boolean isFront();
}
