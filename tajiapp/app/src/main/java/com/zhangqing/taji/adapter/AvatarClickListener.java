package com.zhangqing.taji.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.zhangqing.taji.activities.OthersDetailActivity;
import com.zhangqing.taji.activities.TajiappActivity;
import com.zhangqing.taji.base.UserClass;

/**
 * Created by zhangqing on 2016/4/9.
 */
public class AvatarClickListener implements View.OnClickListener {
    Context context;
    String id;
    String name;

    public AvatarClickListener(Context context, String id, String name) {
        this.id = id;
        this.name = name;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

        OthersDetailActivity.start(context, id, name);
    }
}
