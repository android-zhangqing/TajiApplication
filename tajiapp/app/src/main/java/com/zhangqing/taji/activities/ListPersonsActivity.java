package com.zhangqing.taji.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.PersonsListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;

import org.json.JSONObject;

/**
 * 列出用户列表Activity，包括关注列表界面、被关注列表界面、师傅界面、徒弟界面
 */

public class ListPersonsActivity extends Activity {

    //该成员变量指示该Activity为哪个界面：关注、被关注、师傅、徒弟
    private int mWhichType;

    private String mTitleString;

    private TextView mTitleTextView;
    private EditText mSearchEditText;
    private ListView mListView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PersonsListAdapter mAdapterListView;

    private int current_page;
    private TextView footTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_persons);

        mTitleTextView = (TextView) findViewById(R.id.persons_title_tv);
        mSearchEditText = (EditText) findViewById(R.id.persons_search_edit);
        mListView = (ListView) findViewById(R.id.persons_list_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.persons_swipe_refresh);

        Bundle bundleDatas = getIntent().getExtras();
        mWhichType = bundleDatas.getInt("which");
        mTitleString = bundleDatas.getString("title");
        mTitleTextView.setText(mTitleString);
        mSearchEditText.setHint("搜索" + mTitleString);

        mListView.setDivider(getResources().getDrawable(R.drawable.persons_list_divider));
        mListView.setDividerHeight(2);
        footTv = new TextView(this);
        footTv.setText("正在加载...");
        footTv.setPadding(40, 40, 40, 40);
        //mListView.setFooterDividersEnabled(true);
        mListView.addFooterView(footTv);
        mAdapterListView = new PersonsListAdapter(ListPersonsActivity.this, mListView);
        mAdapterListView.setOnAddDataListener(new PersonsListAdapter.OnAddDataListener() {
            @Override
            public void addData(PersonsListAdapter personsListAdapter) {
                addDataToList(current_page + 1);
            }
        });


        SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addDataToList(1);
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(swipeListener);
        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void finishThis(View v) {
        finish();
    }

    private void addDataToList(final int loading_page) {
        UserClass.getInstance().getPersonsList(mWhichType, loading_page, new VolleyInterface(ListPersonsActivity.this.getApplicationContext()) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {

                current_page = loading_page;
                if (loading_page == 1) {
                    mAdapterListView.clearData();
                }
                mSwipeRefreshLayout.setRefreshing(false);

                Log.e("getPersonsList", jsonObject.toString() + "@@" + loading_page);
                if (jsonObject.toString().contains("\"data\":[]")) {
                    footTv.setText("没有了呢~~");
                    Log.e("getPersonsList", "null data");
                } else {
                    footTv.setText("正在加载...");
                    mAdapterListView.addData(jsonObject);
                }


            }

            @Override
            public void onMyError(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                footTv.setText("网络错误");
            }
        });
    }


}

