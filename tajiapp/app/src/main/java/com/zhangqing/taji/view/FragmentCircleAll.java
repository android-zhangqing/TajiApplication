package com.zhangqing.taji.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.zhangqing.taji.adapter.CircleAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/17.
 */
public class FragmentCircleAll extends LinearLayout {
    private RecyclerViewPullable mRecyclerView;
    private CircleAdapter mRecyclerViewAdapter;

    public FragmentCircleAll(Context context) {
        super(context);
        initView();
    }

    public FragmentCircleAll(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FragmentCircleAll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mRecyclerView = new RecyclerViewPullable(getContext());
        addView(mRecyclerView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mRecyclerView.getRecyclerView().setBackgroundColor(Color.parseColor("#E1DEE6"));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRecyclerViewAdapter = new CircleAdapter(getContext(), CircleAdapter.CIRCLE_ALL));
        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int loadingPage) {
                UserClass.getInstance().chatRoomGetRoomList(loadingPage, new VolleyInterface(getContext().getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        if (loadingPage == 1) {
                            mRecyclerView.setRefreshing(false);
                            mRecyclerViewAdapter.clearData();
                        }
                        if (mRecyclerViewAdapter.addData(jsonObject) != UserClass.Page_Per_Count) {
                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_End);
                        } else {
                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_Normal);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        if (loadingPage == 1)
                            mRecyclerView.setRefreshing(false);
                        mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_ERROR);
                    }
                });
            }
        });
        mRecyclerView.setRefreshing(true);
    }
}
