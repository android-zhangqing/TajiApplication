package com.zhangqing.taji.adapter.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zhangqing.taji.activities.DongTaiDetailActivity;

/**
 * Created by zhangqing on 2016/4/19.
 */
public class DongTaiClickListener implements View.OnClickListener {
    Context mContext;
    String tid;

    public DongTaiClickListener(Context context, String tid) {
        this.mContext = context;
        this.tid = tid;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, DongTaiDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("dongtai", tid);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
}
