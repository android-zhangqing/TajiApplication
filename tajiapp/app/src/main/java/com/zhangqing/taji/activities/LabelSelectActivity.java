package com.zhangqing.taji.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.LabelAdapter;
import com.zhangqing.taji.adapter.MyRecyclerViewAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

/**
 * Created by zhangqing on 2016/4/30.
 * 发布技能秀时选择标签的界面
 */
public class LabelSelectActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mParentLabelContainer;
    private String mParentLabel;

    private RecyclerViewPullable mRecyclerView;
    private MyRecyclerViewAdapter mRecyclerViewAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_select);
        initView();

        //初始化父标签
        initLabelParentView();

        //初始化子标签
        initChildLabelView();
    }

    private void initView() {
        mParentLabelContainer = (LinearLayout) findViewById(R.id.label_parent_container);
        mRecyclerView = (RecyclerViewPullable) findViewById(R.id.label_recycler_view);
    }

    /**
     * 初始化界面上半部分的父标签视图
     */
    private void initLabelParentView() {

        LinearLayout insideContainer = null;
        LinearLayout.LayoutParams lp_inside_container = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp_container = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams lp_tv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp_tv.setMargins(0, 10, 0, 10);

        for (int i = 0; i < UserClass.LABEL_PARENT_ARRAY.length; i++) {
            String label = UserClass.LABEL_PARENT_ARRAY[i];

            if (i % 4 == 0) {
                insideContainer = new LinearLayout(this);
                mParentLabelContainer.addView(insideContainer, lp_inside_container);
            }
            TextView tv = new TextView(this);
            tv.setText(label);
            tv.setTextColor(Color.parseColor("#16FBCC"));
            tv.setPadding(30, 8, 30, 8);
            tv.setClickable(true);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.home_hot_btn_concern_bg);
            tv.setOnClickListener(this);

            //放tv的容器
            LinearLayout tv_container = new LinearLayout(this);
            tv_container.setGravity(Gravity.CENTER);
            tv_container.addView(tv, lp_tv);

            insideContainer.addView(tv_container, lp_container);
        }
    }

    /**
     * 初始化界面下半部分的子标签界面
     */
    private void initChildLabelView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setAdapter(mRecyclerViewAdapter = new LabelAdapter(this));
        /**
         * ！！注意！！此处一页需要加载相当多个，所以此处不使用UserClass.Page_Per_Count
         */
        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(int loadingPage) {

            }
        });

    }

    public void finishThis(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            mParentLabel = ((TextView) v).getText().toString();
            mRecyclerView.setRefreshing(true);
        }
    }
}
