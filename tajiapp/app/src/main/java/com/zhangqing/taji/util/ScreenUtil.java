package com.zhangqing.taji.util;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Created by zhangqing on 2016/4/2.
 * 屏幕 相关工具
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

    public static int getRealViewTop(ViewParent viewParent1) {
        ViewParent viewParent = viewParent1;
        int height = 0;
        while (viewParent != null && viewParent instanceof ViewGroup) {
            height += ((ViewGroup) viewParent).getTop();
            viewParent = viewParent.getParent();
        }
        return height;
    }


}
