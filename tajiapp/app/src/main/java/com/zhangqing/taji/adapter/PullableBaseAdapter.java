package com.zhangqing.taji.adapter;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.json.JSONObject;

/**
 * 可上拉加载的BaseAdapter
 */
public abstract class PullableBaseAdapter extends BaseAdapter {
    public boolean isScrolling;

    public abstract int onAddData(JSONObject jsonObject);

    /**
     * 用于清空数据，在ListVeiw重新加载数据时调用
     */
    public abstract void onClearData();

    /**
     * 传入item，传出imageview，用于图片加载
     *
     * @param viewGroup item
     * @return 一般通过viewgroup的Tag_viewholder取出ImageView，设置URL为Tag
     */
    public abstract ImageView getImageViewFromItemView(View viewGroup);

}
