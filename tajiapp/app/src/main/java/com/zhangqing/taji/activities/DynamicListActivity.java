package com.zhangqing.taji.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.DongTaiListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/26.
 * 我的技能秀界面+我的师徒圈界面
 */
public class DynamicListActivity extends BaseActivity {
    //我的技能秀界面
    public static final String Dynamic_Mine = "myDynamic";
    //我的师徒圈动态界面
    public static final String Dynamic_Shitu = "myCircle";
    //我的动态草稿箱
    public static final String Dynamic_Draft = "myDraft";

    public static void startDynamicActivity(Context context, String type, String title) {
        Intent intent = new Intent(context, DynamicListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }


    private String mDynamicType;
    private String mTitle;

    private RecyclerViewPullable mRecyclerView;
    private DongTaiListAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);

        mDynamicType = getIntent().getStringExtra("type");
        mTitle = getIntent().getStringExtra("title");

        ((TextView) findViewById(R.id.dynamic_title)).setText(mTitle);

        mRecyclerView = (RecyclerViewPullable) findViewById(R.id.dynamic_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecyclerViewAdapter = new DongTaiListAdapter(this));
        mRecyclerViewAdapter.setToEchoTime(true);
        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int loadingPage) {
                UserClass.getInstance().getDynamicList(mDynamicType, loadingPage, new VolleyInterface(getApplicationContext()) {
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
