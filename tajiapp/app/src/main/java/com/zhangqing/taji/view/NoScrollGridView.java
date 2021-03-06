package com.zhangqing.taji.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;

public class NoScrollGridView extends GridView {

    public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public NoScrollGridView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("NoScrollGrid", MeasureSpec.getSize(heightMeasureSpec) + "|"
                + MeasureSpec.getMode(heightMeasureSpec) + "|" +
                MeasureSpec.getSize(widthMeasureSpec) + "|" +
                MeasureSpec.getMode(widthMeasureSpec)
        );


        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
