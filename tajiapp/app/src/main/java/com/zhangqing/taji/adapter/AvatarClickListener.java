package com.zhangqing.taji.adapter;

import android.content.Context;
import android.view.View;

import com.zhangqing.taji.activities.PersonDetailActivity;

/**
 * Created by zhangqing on 2016/4/9.
 */
public class AvatarClickListener implements View.OnClickListener {
    Context context;
    String id;
    String name;

    public AvatarClickListener(Context context, String id,String name) {
        this.id = id;
        this.name=name;
        this.context=context;
    }

    @Override
    public void onClick(View v) {
        PersonDetailActivity.start(context,id,name);
    }
}
