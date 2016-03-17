package com.zhangqing.taji.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/2/23.
 */
public interface OnImageDownload {
    void onDownloadSucc(Bitmap result, String url, ImageView mImageView);
}
