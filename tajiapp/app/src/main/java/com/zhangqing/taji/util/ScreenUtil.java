package com.zhangqing.taji.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;

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

    public static int getRealViewTop(ViewParent viewParent1) {
        ViewParent viewParent = viewParent1;
        int height = 0;
        while (viewParent != null && viewParent instanceof ViewGroup) {
            height += ((ViewGroup) viewParent).getTop();
            viewParent = viewParent.getParent();
        }
        return height;
    }

    /**
     * 打开输入法
     *
     * @param context
     * @param v       接受软键盘输入的编辑文本或其它视图
     */
    public static void openIMM(Context context, View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 接受软键盘输入的编辑文本或其它视图
        inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }


    /**
     * 关闭输入法
     *
     * @param context
     */
    public static void closeIMM(Activity context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken()
                , InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 切换输入法
     *
     * @param context
     */
    public static void toggleIMM(Context context) {
        InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 取输入法状态
     *
     * @param context
     * @return
     */
    public static boolean isActiveIMM(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

}
