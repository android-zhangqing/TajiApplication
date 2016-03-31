package com.zhangqing.taji.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * 本想封装一个可上拉加载更多同时解决滚动时不加载的ListView，但是封装遇到了困难。
 */
public class PullableListView extends ListView {

    interface addDataListener{
        public void addData();


    }


    public PullableListView(Context context) {
        super(context);
    }

    public PullableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        setOnScrollListener(null);

    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);



    }

    @Override
    public void setOnScrollListener(final OnScrollListener l) {

        super.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (l != null) l.onScrollStateChanged(view, scrollState);

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (l != null) l.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }));
    }
}
