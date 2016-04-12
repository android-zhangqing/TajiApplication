package com.zhangqing.taji.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.PersonsListAdapter;
import com.zhangqing.taji.adapter.PullableBaseAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.PullableListView;

import org.json.JSONObject;

/**
 * 列出用户列表Activity，包括关注列表界面、被关注列表界面、师傅界面、徒弟界面
 */

public class ListPersonsActivity extends BaseActivity {

    //该成员变量指示该Activity为哪个界面：关注、被关注、师傅、徒弟
    private int mWhichType;

    private String mTitleString;

    private TextView mTitleTextView;
    private EditText mSearchEditText;
    private PullableListView mListView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PersonsListAdapter mListAdapter;

    private int current_page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_persons);

        mTitleTextView = (TextView) findViewById(R.id.persons_title_tv);
        mSearchEditText = (EditText) findViewById(R.id.persons_search_edit);
        mListView = (PullableListView) findViewById(R.id.persons_list_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.persons_swipe_refresh);

        Bundle bundleDatas = getIntent().getExtras();
        mWhichType = bundleDatas.getInt("which");
        mTitleString = bundleDatas.getString("title");
        mTitleTextView.setText(mTitleString);
        mSearchEditText.setHint("搜索" + mTitleString);

        mListAdapter = new PersonsListAdapter(ListPersonsActivity.this);
        mListView.setAdapter(mListAdapter);
        mListView.setOnAddDataListener(new PullableListView.OnAddDataListener() {
            @Override
            public void addData(PullableBaseAdapter pullableBaseAdapter) {
                addDataToList(current_page + 1);
            }
        });

        SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListView.isNoMoreData = false;
                addDataToList(1);
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(swipeListener);
        mSwipeRefreshLayout.setRefreshing(true);

//
//        /**
//         * 测试GC回收ImageView的情况
//         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    ViewGroup v = null;
//                    if (MyApplication.viewGroupWeakReference != null) {
//                        v = MyApplication.viewGroupWeakReference.get();
//                    }
//                    Log.e("WeakReference", v == null ? "null" : v.toString());
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        break;
//                    }
//                }
//            }
//        }).start();
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
                    mListAdapter.onClearData();
                }
                mSwipeRefreshLayout.setRefreshing(false);

                Log.e("getPersonsList", jsonObject.toString() + "@@" + loading_page);
                if (jsonObject.toString().contains("\"data\":[]")) {
                    mListView.getFootView().setText("没有了呢~~");
                    mListView.isNoMoreData = true;
                    Log.e("getPersonsList", "null data");
                } else {
                    if (mListAdapter.onAddData(jsonObject) != 20) {
                        mListView.getFootView().setText("没有了呢~~");
                        mListView.isNoMoreData = true;
                    } else {
                        mListView.getFootView().setText("正在加载...");
                    }
                }


            }

            @Override
            public void onMyError(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                mListView.getFootView().setText("网络错误");
            }

        });
    }


}

