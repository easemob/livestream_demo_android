package com.easemob.live;

import android.content.Context;
import android.content.SharedPreferences;


public class FastPrefManager {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(FastConstants.PREF_NAME, Context.MODE_PRIVATE);
    }
}
