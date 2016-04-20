package com.zhangqing.taji.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.CommentAdapter;
import com.zhangqing.taji.adapter.DongTaiListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.bean.DongTaiBean;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONObject;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by zhangqing on 2016/4/19.
 * 动态的详情页面
 */
public class DongTaiDetailActivity extends BaseActivity {
    private String mTid;
    private DongTaiBean mDongTai;

    private RecyclerViewPullable mRecyclerView;
    private CommentAdapter mRecyclerViewAdapter;

    private LinearLayout mHeaderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dongtai_detail);

        mRecyclerView = (RecyclerViewPullable) findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        mRecyclerView.getRecyclerView().setItemAnimator(new SlideInLeftAnimator());
//
//        mRecyclerView.getRecyclerView().getItemAnimator().setAddDuration(2000);
//        mRecyclerView.getRecyclerView().getItemAnimator().setRemoveDuration(2000);
//        mRecyclerView.getRecyclerView().getItemAnimator().setMoveDuration(2000);
//        mRecyclerView.getRecyclerView().getItemAnimator().setChangeDuration(2000);

        mRecyclerView.setAdapter(mRecyclerViewAdapter = new CommentAdapter(this));

        mTid = getIntent().getExtras().getString("dongtai");
        mDongTai = DongTaiBean.getInstance(mTid);

        addHeaderView();


        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int loadingPage) {
                UserClass.getInstance().getDongTaiComment(mTid, loadingPage, new VolleyInterface(getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        if (loadingPage == 1) {
                            mRecyclerView.setRefreshing(false);
                            mRecyclerViewAdapter.clearData();
                        }
                        if (mRecyclerViewAdapter.addData(jsonObject, mRecyclerView) != UserClass.Page_Per_Count) {
                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_End);
                        } else {
                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_Normal);
                        }

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        if (loadingPage == 1) mRecyclerView.setRefreshing(false);
                        mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_ERROR);
                    }
                });
            }
        });
        mRecyclerView.setRefreshing(true);

    }

    /**
     * 构造动态详情HeaderView
     */
    private void addHeaderView() {
        mHeaderView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_home_hot_then_listview_item, null);
        Log.e("mHead", mHeaderView instanceof LinearLayout ? "null" : "not");

        if (mDongTai == null) return;

        DongTaiListAdapter.MyViewHolder myViewHolder = new DongTaiListAdapter.MyViewHolder(mHeaderView);

        DongTaiListAdapter.updateViewHolder(this, myViewHolder, mDongTai, null);

        mRecyclerView.setHeaderView(mHeaderView);

    }

    public void finishThis(View v) {
        finish();
    }
}
