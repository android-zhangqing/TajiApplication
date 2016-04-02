package com.zhangqing.taji.util;

import android.content.Context;

/**
 * Created by zhangqing on 2016/4/2.
 */
public class ScreenUtil {

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
