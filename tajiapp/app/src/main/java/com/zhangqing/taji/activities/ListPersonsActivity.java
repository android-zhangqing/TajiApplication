package com.zhangqing.taji.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.PersonsListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.util.ImageDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/20.
 */

public class ListPersonsActivity extends Activity {
    private int mWhichType;
    private String mTitleString;

    private TextView mTitleTextView;
    private EditText mSearchEditText;
    private ListView mListView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PersonsListAdapter mAdapterListView;

    private int start_index,end_index;


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
        TextView t=new TextView(this);
        t.setText("正在加载...");
        mListView.setFooterDividersEnabled(true);
        mListView.addFooterView(t);

        //滑动ListView时暂停加载图片,只加载可视区域内图片
//        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
////                isInit = true;
////                switch (scrollState) {
////                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 滑动停止
////                        for (; start_index < end_index; start_index++) {
////                            ImageView img = (ImageView) mListView.findViewWithTag(start_index);
////                            img.setImageResource(R.drawable.update_log);
////                        }
////                        break;
////
////                    default:
////                        break;
////                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        }));

        mAdapterListView = new PersonsListAdapter(ListPersonsActivity.this, mListView);


        final SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UserClass.getInstance().getPersonsList(mWhichType, new VolleyInterface(ListPersonsActivity.this.getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapterListView.initData(jsonObject);
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(swipeListener);
        mSwipeRefreshLayout.setRefreshing(true);
       // mSwipeRefreshLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                swipeListener.onRefresh();
//                mSwipeRefreshLayout.setRefreshing(true);
//            }
//        }, 100);
    }

    public void finishThis(View v) {
        finish();
    }


}

