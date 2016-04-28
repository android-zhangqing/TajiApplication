package com.zhangqing.taji.adapter;

import android.support.v7.widget.RecyclerView;

import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/27.
 */
public abstract class MyRecyclerViewAdapter<M extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<M> {


    public abstract int addData(JSONObject jsonObject);

    public abstract void clearData();
}
