package com.zhangqing.taji.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.DongTaiAdapter;
import com.zhangqing.taji.adapter.LoadMoreRecyclerView;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 首页-热门-广场旁边的一系列标签对应界面
 */
public class FragmentHomeHotViewThen extends LinearLayout implements LoadMoreRecyclerView.LoadMoreListener {

    private static final int STATUS_NORMAL = 1;//分页加载空闲状态
    private static final int STATUS_LOADING = 2;//分页加载中
    private static final int STATUS_END = 3;//分页加载已尾页

    private int current_page = 0;
    private int current_loading_status = STATUS_NORMAL;

    private String mCategoryName;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context context;
    private View containerView;

    private LoadMoreRecyclerView mRecyclerView;
    private DongTaiAdapter mRecyclerViewAdapter;

    public FragmentHomeHotViewThen(Context context, String name) {
        super(context);
        this.context = context;
        mCategoryName = name;
        // TODO Auto-generated constructor stub
        LayoutInflater flater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        containerView = flater.inflate(R.layout.view_home_hot_then, null);

        mRecyclerView = (LoadMoreRecyclerView) containerView
                .findViewById(R.id.home_hot_then_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRecyclerViewAdapter = new DongTaiAdapter(getContext()));


        mRecyclerView.setLoadMoreListener(this);
        mRecyclerView.setLoadingMore(true);


        mSwipeRefreshLayout = (SwipeRefreshLayout) containerView
                .findViewById(R.id.home_hot_then_swipe_ly_then);
        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                    @Override
                    public void onRefresh() {
                        current_page = 0;
                        onLoadMore();
                    }
                });


        addView(containerView);
        //mSwipeRefreshLayout.setRefreshing(true);

    }

    public void perfromOnPageSelected() {
        if (current_page == 0)
            mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onLoadMore() {
        /**
         * current_page初始化时为0  为1时表示获取第一页
         */
        UserClass.getInstance().doGetDongTai(mCategoryName, ++current_page, new VolleyInterface(getContext().getApplicationContext()) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {
                if (current_page == 1) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mRecyclerViewAdapter.clearData();
                }

                try {
                    if (mRecyclerViewAdapter.addData(jsonObject.getJSONArray("data")) != 20) {
                        //mFootTextView.setText("没有了呢~~");
                        current_loading_status = STATUS_END;
                        mRecyclerView.notifyMoreFinish(false);
                    } else {
                        //mFootTextView.setText("正在加载...");
                        current_loading_status = STATUS_NORMAL;
                        mRecyclerView.notifyMoreFinish(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("未捕获", "|");
                }

            }

            @Override
            public void onMyError(VolleyError error) {
                if (current_page == 1)
                    mSwipeRefreshLayout.setRefreshing(false);
                current_loading_status = STATUS_NORMAL;
                //mFootTextView.setText("网络错误");
            }
        });
    }
}


