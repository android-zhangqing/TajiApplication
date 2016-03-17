package com.zhangqing.taji.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ListView;

import com.zhangqing.taji.R;

/**
 * 弹性ListView。
 *
 * @author E
 */
public class FlexiListView extends ListView {
    //初始可拉动Y轴方向距离
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 200;
    //上下文环境
    private Context mContext;
    //实际可上下拉动Y轴上的距离
    private int mMaxYOverscrollDistance;

    public FlexiListView(Context context) {
        super(context);
        mContext = context;
        initBounceListView();

        setOverscrollHeader(getResources().getDrawable(R.drawable.ic_launcher));
    }

    public FlexiListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initBounceListView();

        setOverscrollHeader(getResources().getDrawable(R.drawable.ic_launcher));
    }

    public FlexiListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initBounceListView();

    }

    private void initBounceListView() {
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;
        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                   int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        //实现的本质就是在这里动态改变了maxOverScrollY的值
        // mContext.test2.setText("aaaa"+deltaX);
        /*
         deltaY	Change in Y in pixels
         scrollY	Current Y scroll value in pixels before applying deltaY
         scrollRangeY	Maximum content scroll range along the Y axis
         maxOverScrollY	Number of pixels to overscroll by in either direction along the Y axis.
         isTouchEvent	true if this scroll operation is the result of a touch event.
         */

        Log.e("overScrollBy",
                deltaY + "|" + scrollY + "|" + scrollRangeY + "|" + maxOverScrollY + "|" + isTouchEvent + "|" + mMaxYOverscrollDistance);
        //if(scrollY<0)return false;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, 0-scrollY+10, isTouchEvent);
    }

}