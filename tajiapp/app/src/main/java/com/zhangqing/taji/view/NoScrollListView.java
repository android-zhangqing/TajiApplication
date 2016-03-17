package com.zhangqing.taji.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

public class NoScrollListView extends ListView {

	public NoScrollListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDividerHeight(0);
		// TODO Auto-generated constructor stub
	}

	public NoScrollListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int aa=Integer.MAX_VALUE >> 2;


		int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		Log.e("NoScrollListView","onMeasure "+aa+" "+mExpandSpec);
		super.onMeasure(widthMeasureSpec, mExpandSpec);
	}
}
