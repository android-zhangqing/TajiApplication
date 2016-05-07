package com.zhangqing.taji.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.MediaController;
import android.widget.VideoView;

import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.util.CameraUtil;

/**
 * Created by zhangqing on 2016/5/7.
 * 视频播放界面
 */
public class VideoPlayerActivity extends BaseActivity {

    public static void startVideoPlayer(Context context, Uri uri) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra("video", uri);
        context.startActivity(intent);
    }

    Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        //Set the screen to landscape.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        uri = getIntent().getParcelableExtra("video");

        VideoView videoView = (VideoView) this.findViewById(R.id.video_player_view);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.requestFocus();
    }
}
