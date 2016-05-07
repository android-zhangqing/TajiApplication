package com.zhangqing.taji.util;

import android.app.Activity;
import android.os.Environment;

/**
 * Created by Administrator on 2016/2/23.
 */

public class PathUtil {
    private static PathUtil util;
    public static int flag = 0;


    /**
     * 判断是否有sdcard
     *
     * @return
     */
    public static boolean hasSDCard() {
        boolean b = false;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            b = true;
        }
        return b;
    }

    /**
     * 得到sdcard路径
     *
     * @return
     */
    public static String getExtPath() {
        String path = "";
        if (hasSDCard()) {
            path = Environment.getExternalStorageDirectory().getPath();
        }
        return path;
    }

    /**
     * 得到/data/data/yanbin.imagedownload目录
     *
     * @param mActivity
     * @return
     */
    public static String getPackagePath(Activity mActivity) {
        return mActivity.getFilesDir().toString();
    }

    /**
     * 根据url得到图片名
     *
     * @param url
     * @return
     */
    public static String getImageName(String url) {
        String imageName = "";
        if (url != null) {
            imageName = url.substring(url.lastIndexOf("/") + 1);
        }
        return imageName;
    }
}


