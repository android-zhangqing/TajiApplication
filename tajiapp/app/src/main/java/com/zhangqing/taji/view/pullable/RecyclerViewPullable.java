package com.zhangqing.taji.view.pullable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhangqing.taji.R;

/**
 * Created by zhangqing on 2016/4/15.
 * 咱们一般用这个组件就行
 * 带 分页加载、下拉刷新 功能的RecyclerView
 */
public class RecyclerViewPullable extends LinearLayout {
    public static final int LoadingMoreStatus_Normal = 1;
    public static final int LoadingMoreStatus_Loading = 2;
    public static final int LoadingMoreStatus_End = 3;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerViewWithHeaderAndFooter mRecyclerView;

    private OnLoadListener mOnLoadListener;

    private TextView mFootView = null;
    private int mFootColor = Color.DKGRAY;

    public interface OnLoadListener {

        /**
         * 1.务必记得在onLoadMore里面Volley结束后setLoadingMoreStatus(status);
         * <P/>2.第一步先要判断loadingPage为第一页，则清空数据
         *
         * @param loadingPage 当前加载页
         */
        void onLoadMore(int loadingPage);

    }

    /**
     * 当前在第几页
     */
    private int current_page = 0;
    /**
     * 标记是否正在加载更多，防止再次调用加载更多接口.
     * 务必记得在onLoadMore里面Volley结束后setLoadingMoreStatus(status);
     */
    private int mLoadingMoreStatus;

    /**
     * 传进来的Adapter
     */
    private RecyclerView.Adapter mInsideAdapter;

    public RecyclerViewPullable(Context context) {
        super(context);
        initView();
    }

    public RecyclerViewPullable(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 取xml属性-Footer字体颜色
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.recycler);
        mFootColor = typedArray.getColor(R.styleable.recycler_footer_text_color, Color.WHITE);

        initView();
    }

    public RecyclerViewPullable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_recycler_pullable, this, false);
        addView(v);

        mRecyclerView = (RecyclerViewWithHeaderAndFooter) v.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                current_page = 1;
                if (mOnLoadListener != null)
                    mOnLoadListener.onLoadMore(current_page);
            }
        });

//        /**
//         * 加入FootView用于分页加载提示
//         */

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mFootView = new TextView(getContext());
                mFootView.setPadding(0, 40, 0, 40);
                mFootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mFootView.setGravity(Gravity.CENTER);
                mFootView.setTextColor(mFootColor);
                mRecyclerView.setFooterView(mFootView);
            }
        });


        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (null != mOnLoadListener && mLoadingMoreStatus == LoadingMoreStatus_Normal && dy > 0) {
                    int lastVisiblePosition = mRecyclerView.getLastVisiblePosition();
                    if (lastVisiblePosition + 1 == mRecyclerView.getAdapter().getItemCount()) {
                        setLoadingMoreStatus(LoadingMoreStatus_Loading);
                        current_page++;
                        Log.e("onScrolledTo", "加载第" + current_page + "页");
                        mOnLoadListener.onLoadMore(current_page);
                    }
                }
            }

        });


    }


    public void setLoadingMoreStatus(int loadingMoreStatus) {
        mLoadingMoreStatus = loadingMoreStatus;
        switch (loadingMoreStatus) {
            case LoadingMoreStatus_Normal: {
                Log.e("加载结果：", "还有下一页");
                if (mFootView != null)
                    mFootView.setText("");
                break;
            }
            case LoadingMoreStatus_Loading: {
                Log.e("加载结果：", "正在加载");
                if (mFootView != null)
                    mFootView.setText("正在加载");
                break;
            }
            case LoadingMoreStatus_End: {

                if (mFootView != null) {
                    Log.e("加载结果：", "没有了呢");
                    mFootView.setText("没有了呢~");
                }

            }

        }
        //mRecyclerView.getAdapter().notifyItemChanged(mRecyclerView.getAdapter().getItemCount()-1);
    }

    public void setOnLoadListener(OnLoadListener l) {
        mOnLoadListener = l;
    }

    //-----------------RecyclerView方法暴露开始------------------------------------------
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setAdapter(RecyclerView.Adapter a) {
        mInsideAdapter = a;
        mRecyclerView.setAdapter(a);
    }

    public void setLayoutManager(RecyclerView.LayoutManager l) {
        mRecyclerView.setLayoutManager(l);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator i) {
        mRecyclerView.setItemAnimator(i);
    }

    public void notifyDataSetChanged() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setFooterView(View v) {
        mRecyclerView.setFooterView(v);
    }

    public void setHeaderView(View v) {
        mRecyclerView.setHeaderView(v);
    }

    public int getCurrentPage() {
        return current_page;
    }

    //------------------RecyclerView方法暴露结束-----------------------------------------

    //------------------SwipeRefreshLayout方法暴露开始-----------------------------------
    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }
    //------------------SwipeRefreshLayout方法暴露结束-----------------------------------


}