package com.zhangqing.taji.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import java.io.File;

/**
 * Created by zhangqing on 2016/4/9.
 */
public class CameraUtil {

    public static class Picture {
        /**
         * 快速生成缩放后的图片的路径
         * /storage/emulated/0/pictures/1461561252499.jpg
         * /storage/emulated/0/pictures/1461561252499_scaled.jpg
         * <p/>
         * 注意：执行该操作将删除可能存在的_scaled文件
         *
         * @param path
         * @return
         */
        public static String getAppendPath(String path, String append) {
            String scale_path = path.substring(0, path.lastIndexOf("."))
                    + "_" + append +
                    path.substring(path.lastIndexOf("."), path.length());
            Log.e("getAppendPath", scale_path + "|");

            //删除原文件
            File file = new File(scale_path);
            if (file.exists() && file.isFile()) file.delete();

            return scale_path;

        }

        public static Uri createOutputUri(Context context) {
            Uri photoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
            return photoUri;
        }

        public static Intent capturePicture(Uri outputUri) {
            Intent intent = null;
            //执行拍照前，应该先判断SD卡是否存在
            String SDState = Environment.getExternalStorageState();
            if (SDState.equals(Environment.MEDIA_MOUNTED)) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                /***
                 * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
                 * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
                 * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
                 */
                Log.e("photoUri", outputUri.toString());
                if (outputUri != null)
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputUri);
            }
            return intent;
        }


        public static Intent choosePicture2() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            return intent;
        }

        public static Intent choosePicture() {
            Intent intent3 = new Intent();
            intent3.setType("image/*");
            intent3.setAction(Intent.ACTION_GET_CONTENT);
            return intent3;
        }

        public static Intent cropPicture(Uri mOutputUri) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(mOutputUri, "image/*");
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 250);
            intent.putExtra("outputY", 250);
            //intent.putExtra("return-data", true);
            return intent;
        }
    }

    public static class Video {
        public static Intent captureVideo() {
            Intent intent2 = new Intent();
            intent2.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
            intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intent2.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
            return intent2;
        }
    }

    /**
     * 把Uri转化成文件路径
     */
    public static String uri2filePath(Context context, Uri uri) {
        if (uri == null || context == null || uri.getPath() == null) return null;
        String media_path = uri.getPath().toLowerCase();
        if (isMediaFilePath(media_path)) {
            return media_path;
        }

        String real_path = null;
        String[] pojo = {MediaStore.Images.Media.DATA};
        Cursor cursor = new CursorLoader(context, uri, pojo, null, null, null).loadInBackground();
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            real_path = cursor.getString(columnIndex).toLowerCase();
            cursor.close();
        }
        return real_path;
    }

    public static boolean isMediaFilePath(String path) {
        if (path == null) return false;
        return (isPictureFilePath(path) || isVideoFilePath(path));
    }

    public static boolean isPictureFilePath(String media_path) {
        media_path = media_path.toLowerCase();
        return (media_path.endsWith(".jpg") || media_path.endsWith(".bmp") || media_path.endsWith(".png"));
    }

    public static boolean isVideoFilePath(String media_path) {
        media_path = media_path.toLowerCase();
        return (media_path.endsWith(".3gp"));
    }


}
