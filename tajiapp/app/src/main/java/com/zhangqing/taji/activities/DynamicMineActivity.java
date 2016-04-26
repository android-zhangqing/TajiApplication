package com.zhangqing.taji.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.alibaba.sdk.android.session.model.User;
import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.DongTaiListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/26.
 * 我的技能秀界面
 */
public class DynamicMineActivity extends BaseActivity {
    private RecyclerViewPullable mRecyclerView;
    private DongTaiListAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);

        mRecyclerView = (RecyclerViewPullable) findViewById(R.id.dynamic_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecyclerViewAdapter = new DongTaiListAdapter(this));
        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int loadingPage) {
                UserClass.getInstance().getDynamicMine(loadingPage, new VolleyInterface(getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {

                        if (loadingPage == 1) {
                            mRecyclerView.setRefreshing(false);
                            mRecyclerViewAdapter.clearData();
                        }
                        try {
                            if (mRecyclerViewAdapter.addData(jsonObject.getJSONArray("data")) !=
                                    UserClass.Page_Per_Count) {
                                mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_End);
                            } else {
                                mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_Normal);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_ERROR);
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

    public void finishThis(View v) {
        finish();
    }
}
