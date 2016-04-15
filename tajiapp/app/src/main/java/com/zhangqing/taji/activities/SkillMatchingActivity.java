package com.zhangqing.taji.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.DongTaiAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/13.
 * 技能匹配界面Activity
 */
public class SkillMatchingActivity extends BaseActivity {

    private RecyclerViewPullable mPullRecyclerView;
    private DongTaiAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_skill_matching);

        mPullRecyclerView = (RecyclerViewPullable) findViewById(R.id.skill_matching_recycler);

        mPullRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPullRecyclerView.setAdapter(mAdapter = new DongTaiAdapter(this));
        mPullRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int loadingPage) {
//                UserClass.getInstance().doGetDongTai("", loadingPage, new VolleyInterface(getApplicationContext()) {
//                    @Override
//                    public void onMySuccess(JSONObject jsonObject) {
//                        if (loadingPage == 1) {
//                            mPullRecyclerView.setRefreshing(false);
//                            mAdapter.clearData();
//                            //mRecyclerViewAdapter.notifyDataSetChanged();
//                        }
//
//                        try {
//                            if (mAdapter.addData(jsonObject.getJSONArray("data")) != 20) {
//                                //mFootTextView.setText("没有了呢~~");
//                                mPullRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_End);
//                                mPullRecyclerView.notifyDataSetChanged();
//                                //mRecyclerView.notifyMoreFinish(false);
//                            } else {
//                                //mFootTextView.setText("正在加载...");
//                                mPullRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_Normal);
//                                mPullRecyclerView.notifyDataSetChanged();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.e("未捕获", "|");
//                        }
//
//                    }
//
//                    @Override
//                    public void onMyError(VolleyError error) {
//                        if (loadingPage == 1)
//                            mPullRecyclerView.setRefreshing(false);
//                        mPullRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_Normal);
//
//                        //mFootTextView.setText("网络错误");
//                    }
//                });
            }
        });

    }

    public void finishThis(View v) {
        finish();
    }
}
