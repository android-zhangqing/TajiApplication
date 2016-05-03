package com.zhangqing.taji.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.BaiShiInviteAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/5/3.
 * 收到的拜师邀请的界面
 */
public class BaiShiInviteListActivity extends BaseActivity {

    private RecyclerViewPullable mRecyclerView;
    private BaiShiInviteAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baishi_invite_list);

        mRecyclerView = (RecyclerViewPullable) findViewById(R.id.baishi_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecyclerViewAdapter = new BaiShiInviteAdapter(this));
        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int loadingPage) {
                UserClass.getInstance().shiTuGetBaiShiList(loadingPage, new VolleyInterface(getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        if (loadingPage == 1) {
                            mRecyclerViewAdapter.clearData();
                            mRecyclerView.setRefreshing(false);
                        }

                        if (mRecyclerViewAdapter.addData(jsonObject) != UserClass.Page_Per_Count) {
                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_End);
                        } else {
                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_Normal);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        if (loadingPage == 1) {
                            mRecyclerView.setRefreshing(false);
                        }
                        mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_ERROR);
                    }
                });
            }
        });
        mRecyclerView.setRefreshing(true);


    }

    public void finishThis(View v) {
        finish();
    }
}
