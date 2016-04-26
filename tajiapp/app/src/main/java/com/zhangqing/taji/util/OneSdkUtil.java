package com.zhangqing.taji.util;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.zhangqing.taji.MyApplication;

import java.io.File;

/**
 * Created by zhangqing on 2016/4/24.
 */
public class OneSdkUtil {

    public static void upLoadFile(String path, UploadListener listener) {
        MyApplication.mediaService.upload(new File(path), "taji", listener);

    }
}
