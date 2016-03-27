package com.zhangqing.taji.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;

/**
 * 优化的swipeRefreshLayout，刷新有最小时长，优化setRefreshing方法
 */
public class MySwipeRefreshLayout extends SwipeRefreshLayout {
    private static final int DELAY_MILLIS = 500;

    private OnRefreshListener mListener;
    private long mStartTimeMillis;

    public MySwipeRefreshLayout(Context context) {
        super(context);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setColorSchemeColors(Color.parseColor("#915A99")
                , Color.parseColor("#27BDA7")
                , Color.parseColor("#37A4C5")
                , Color.parseColor("#D46F68"));
        //setProgressBackgroundColorSchemeColor(Color.parseColor("#FAFAFA"));
    }


    @Override
    public void setOnRefreshListener(OnRefreshListener listener) {
        super.setOnRefreshListener(listener);
        this.mListener = listener;
    }

    @Override
    public void setRefreshing(final boolean refreshing) {
        Log.e("MySwipeRefresh", "setRefreshing");

        //进入刷新状态
        if (refreshing) {
            mStartTimeMillis = System.currentTimeMillis();
            if (mListener != null) {
                mListener.onRefresh();
            }
            post(new Runnable() {
                @Override
                public void run() {
                    MySwipeRefreshLayout.super.setRefreshing(refreshing);
                }
            });
            return;
        }


        //停止刷新，保存刷新状态在DELAY_MILLIS毫秒以上。
        long currentTime = System.currentTimeMillis();
        if (currentTime - mStartTimeMillis >= DELAY_MILLIS) {
            post(new Runnable() {
                @Override
                public void run() {
                    MySwipeRefreshLayout.super.setRefreshing(refreshing);
                }
            });
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    MySwipeRefreshLayout.super.setRefreshing(refreshing);
                }
            }, DELAY_MILLIS - (currentTime - mStartTimeMillis));
        }


    }


}
