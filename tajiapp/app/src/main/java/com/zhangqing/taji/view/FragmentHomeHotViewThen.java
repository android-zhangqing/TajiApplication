package com.zhangqing.taji.view;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.DongTaiListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 首页-热门-广场旁边的一系列标签对应界面
 */
public class FragmentHomeHotViewThen extends LinearLayout {

    private String mCategoryName;

    private Context context;
    private View containerView;

    private RecyclerViewPullable mRecyclerView;
    private DongTaiListAdapter mRecyclerViewAdapter;

    public FragmentHomeHotViewThen(Context context, String name) {
        super(context);
        this.context = context;
        mCategoryName = name;
        // TODO Auto-generated constructor stub
        LayoutInflater flater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        containerView = flater.inflate(R.layout.view_home_hot_then, null);

        mRecyclerView = (RecyclerViewPullable) containerView
                .findViewById(R.id.home_hot_then_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRecyclerViewAdapter = new DongTaiListAdapter(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int current_page) {
                UserClass.getInstance().dongTaiGetList(mCategoryName, current_page, new VolleyInterface(getContext().getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        if (current_page == 1) {
                            mRecyclerView.setRefreshing(false);
                            mRecyclerViewAdapter.clearData();
                            //mRecyclerViewAdapter.notifyDataSetChanged();
                        }

                        try {
                            if (mRecyclerViewAdapter.addData(jsonObject.getJSONArray("data"), mRecyclerView) != UserClass.Page_Per_Count) {
                                //mFootTextView.setText("没有了呢~~");
                                mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_End);
                                //mRecyclerView.notifyMoreFinish(false);
                            } else {
                                //mFootTextView.setText("正在加载...");
                                mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_Normal);
                            }
                            //mRecyclerView.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("未捕获", "|");
                        }

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        if (current_page == 1)
                            mRecyclerView.setRefreshing(false);
                        mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_ERROR);

                        //mFootTextView.setText("网络错误");
                    }
                });
            }
        });
        //mRecyclerView.setRefreshing(true);
        addView(containerView);

    }

    public void perfromOnPageSelected() {
        if (mRecyclerView.getCurrentPage() == 0)
            mRecyclerView.setRefreshing(true);
        else
            mRecyclerView.notifyDataSetChanged();
    }
}


