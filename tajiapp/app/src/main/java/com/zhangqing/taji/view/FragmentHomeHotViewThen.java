package com.zhangqing.taji.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zhangqing.taji.R;

import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FragmentHomeHotViewThen extends LinearLayout {

	private SwipeRefreshLayout swipeRefreshLayout;
	private Context context;
	private View containerView;

	public FragmentHomeHotViewThen(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
		LayoutInflater flater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		containerView = flater.inflate(R.layout.view_home_hot_then, null);
		
		ListView listView = (ListView) containerView
				.findViewById(R.id.home_hot_then_listview);

		//

		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int j = 0; j < 4; j++) {
			Map<String, Object> listitem = new HashMap<String, Object>();
			listitem.put("pic", R.drawable.pic_loading_bg);
			listItems.add(listitem);
		}
		listView.setAdapter(new SimpleAdapter(context, listItems,
				R.layout.view_home_hot_then_listview_item, new String[] { "pic" },
				new int[] { R.id.home_hot_then_icon_iv}));

		swipeRefreshLayout = (SwipeRefreshLayout) containerView
				.findViewById(R.id.home_hot_then_swipe_ly_then);
		swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_light),
				getResources().getColor(android.R.color.holo_red_light),
				getResources().getColor(android.R.color.holo_orange_light),
						getResources().getColor(android.R.color.holo_green_light));
		swipeRefreshLayout
				.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

					@Override
					public void onRefresh() {
						// tv.setText("正在刷新");
						// // TODO Auto-generated method stub
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								swipeRefreshLayout.setRefreshing(false);
							}
						}, 3000);
					}
				});

		

		addView(containerView);

	}

}


