package com.zhangqing.taji.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.PersonListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONObject;

/**
 * 列出用户列表Activity，包括关注列表界面、被关注列表界面、师傅界面、徒弟界面
 */

public class ListPersonsActivity extends BaseActivity {

    //该成员变量指示该Activity为哪个界面：关注、被关注、师傅、徒弟
    private int mWhichType;
    private String mTargetId = null;

    private String mTitleString;

    private TextView mTitleTextView;
    private EditText mSearchEditText;

    private RecyclerViewPullable mRecyclerView;
    private PersonListAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_persons);

        mTitleTextView = (TextView) findViewById(R.id.persons_title_tv);
        mSearchEditText = (EditText) findViewById(R.id.persons_search_edit);

        mRecyclerView = (RecyclerViewPullable) findViewById(R.id.list_persons_recycler_view);

        Bundle bundleDatas = getIntent().getExtras();
        mWhichType = bundleDatas.getInt("which");
        mTitleString = bundleDatas.getString("title");
        mTargetId = bundleDatas.getString("id");

        mTitleTextView.setText(mTitleString);
        mSearchEditText.setHint("搜索" + mTitleString);


        mRecyclerView.setAdapter(mRecyclerViewAdapter = new PersonListAdapter(this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.getRecyclerView().setBackgroundColor(Color.WHITE);

        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int loadingPage) {

                UserClass.getInstance().getPersonsList(mWhichType, mTargetId, loadingPage, new VolleyInterface(ListPersonsActivity.this.getApplicationContext()) {
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

