package com.zhangqing.taji.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.ZanListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/5/6.
 * 赞我的、评论我的、at我的 列表界面
 */
public class ZanListActivity extends BaseActivity {

    public static void startZanActivity(Context context, String type) {
        Intent intent = new Intent(context, ZanListActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public static final String LIST_ZAN = "myLike";
    public static final String LIST_COMMENT = "myComment";
    public static final String LIST_AT = "myAt";

    private String mListType;

    private RecyclerViewPullable mRecyclerView;
    private ZanListAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zan_list);

        mListType = getIntent().getStringExtra("type");
        if (mListType == null) mListType = LIST_ZAN;

        mRecyclerView = (RecyclerViewPullable) findViewById(R.id.zan_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecyclerViewAdapter = new ZanListAdapter(this, mListType));
        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int loadingPage) {
                UserClass.getInstance().getDynamicList(mListType, loadingPage, new VolleyInterface(getApplicationContext()) {
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
