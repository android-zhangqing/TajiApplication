package com.zhangqing.taji.adapter.listener;

import android.content.Context;
import android.view.View;

import com.zhangqing.taji.activities.OthersDetailActivity;

/**
 * Created by zhangqing on 2016/4/9.
 */
public class AvatarClickListener implements View.OnClickListener {
    Context context;
    String userid;
    String name;

    public AvatarClickListener(Context context, String id, String name) {
        this.userid = id;
        this.name = name;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        OthersDetailActivity.start(context, userid, name);
    }
}
