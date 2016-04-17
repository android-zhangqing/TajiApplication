package com.zhangqing.taji.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.SkillMatchingAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.pullable.DividerItemDecoration;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/13.
 * 技能匹配界面Activity
 */
public class SkillMatchingActivity extends BaseActivity {

    private RecyclerViewPullable mPullRecyclerView;
    private SkillMatchingAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_skill_matching);

        mPullRecyclerView = (RecyclerViewPullable) findViewById(R.id.skill_matching_recycler);

        mPullRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPullRecyclerView.setAdapter(mAdapter = new SkillMatchingAdapter(this));
        mPullRecyclerView.getRecyclerView().setBackgroundColor(Color.parseColor("#FFFFFF"));
        mPullRecyclerView.getRecyclerView().addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        mPullRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {

            @Override
            public void onLoadMore(final int loadingPage) {
                UserClass.getInstance().getSkillMatching(loadingPage, new VolleyInterface(getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        if (loadingPage == 1) {
                            mAdapter.clearData();
                            mPullRecyclerView.setRefreshing(false);

                        }
                        try {
                            if (mAdapter.addData(jsonObject.getJSONArray("data")) != UserClass.Page_Per_Count) {
                                mPullRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_End);
                            } else {
                                mPullRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_Normal);
                            }
                            mPullRecyclerView.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        mPullRecyclerView.setRefreshing(false);
                        mPullRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_ERROR);
                    }
                });
            }
        });
        mPullRecyclerView.setRefreshing(true);

    }

    public void finishThis(View v) {
        finish();
    }
}
