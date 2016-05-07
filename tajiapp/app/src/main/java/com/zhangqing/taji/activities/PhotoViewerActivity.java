package com.zhangqing.taji.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;

import io.rong.imkit.tools.PhotoFragment;

/**
 * Created by zhangqing on 2016/5/7.
 * 查看大图的activity
 */
public class PhotoViewerActivity extends BaseActivity {

    public static void startPhotoView(Context context, Uri uri, Uri thumbnail) {
        Intent intent = new Intent(context, PhotoViewerActivity.class);
        intent.putExtra("uri", uri);
        intent.putExtra("thumbnail", (thumbnail == null ? uri : thumbnail));
        context.startActivity(intent);
    }

    private Uri uri;
    private Uri uri_thumbnail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        uri = getIntent().getParcelableExtra("uri");
        uri_thumbnail = getIntent().getParcelableExtra("thumbnail");

        PhotoFragment mPhotoFragment = (PhotoFragment) getSupportFragmentManager().findFragmentById(R.id.photo_fragment);
        mPhotoFragment.initPhoto(uri, uri_thumbnail, null);

    }
}
