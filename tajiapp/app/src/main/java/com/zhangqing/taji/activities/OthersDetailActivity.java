package com.zhangqing.taji.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.session.model.User;
import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.DongTaiListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.bean.PersonInfoBean;
import com.zhangqing.taji.view.PersonInfoView;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;

/**
 * Created by Administrator on 2016/3/19.
 * 查看其他人的个人信息
 */
public class OthersDetailActivity extends BaseActivity {

    public static void start(Context context, String id, String name) {
        Intent intent = new Intent(context, OthersDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("name", name);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private String mId = "";
    private String mName = "";

    private PersonInfoBean mPersonInfo;
    private PersonInfoView mPersonInfoView;

    private RecyclerViewPullable mRecyclerView;
    private DongTaiListAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        mRecyclerView = (RecyclerViewPullable) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecyclerViewAdapter = new DongTaiListAdapter(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Bundle bundle = getIntent().getExtras();
        mId = bundle.getString("id");
        mName = bundle.getString("name");

        ((TextView) findViewById(R.id.title)).setText(mName);

        UserClass.getInstance().getOthersInfo(mId, new VolleyInterface(getApplicationContext()) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {

                try {
                    mPersonInfo = new PersonInfoBean(mId, jsonObject);
                    mPersonInfoView = new PersonInfoView(OthersDetailActivity.this, mPersonInfo);

                    mRecyclerView.setHeaderView(mPersonInfoView);

                    mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {

                        @Override
                        public void onLoadMore(final int loadingPage) {
                            UserClass.getInstance().getOthersDongTai(mId, loadingPage, new VolleyInterface(getApplicationContext()) {
                                @Override
                                public void onMySuccess(JSONObject jsonObject) {

                                    if (loadingPage == 1) {
                                        mRecyclerViewAdapter.clearData();
                                        mRecyclerView.setRefreshing(false);
                                    }
                                    try {
                                        if (mRecyclerViewAdapter.addData(jsonObject.getJSONArray("data")) != UserClass.Page_Per_Count) {
                                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_End);
                                        } else {
                                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_Normal);
                                        }
                                        mRecyclerView.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(OthersDetailActivity.this, "解析异常", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onMyError(VolleyError error) {
                                    mRecyclerView.setRefreshing(false);
                                    mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_ERROR);
                                }
                            });
                        }
                    });
                    initNotHeaderView();
                    mRecyclerView.setRefreshing(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "getOthersInfo");
                }
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setDisplayShowCustomEnabled(true);
//        RelativeLayout relativeLayout=new RelativeLayout(this);
    }

    public void onClickBtnFinish(View v) {
        finish();
    }

    public void onClickBtnBottom(View v) {
        switch (v.getId()) {
            case R.id.person_detail_chat_btn: {
                RongIM.getInstance().startPrivateChat(this, mId, mName);
                break;
            }
            case R.id.person_detail_baishi_btn: {


                break;
            }
        }
    }

    /**
     * mPersonInfo获取成功后初始化非headerView
     */
    private void initNotHeaderView() {


        //如果是自己，则不显示底部两按钮
        if (mPersonInfo.userid.equals(UserClass.getInstance().userId)) {
            findViewById(R.id.person_detail_bottom_container).setVisibility(View.INVISIBLE);
        } else {
            //查看的是他人个人主页则初始化底部按钮特效
            initQuickReturnChatBtn();
            //如果是师傅，则不显示拜师按钮
            if (mPersonInfo.is_master)
                findViewById(R.id.person_detail_baishi_btn).setVisibility(View.GONE);
        }
    }


    /**
     * quickReturn聊天按钮动画显示与隐藏
     */
    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;
    private int mMinRawY;
    private int mState = STATE_ONSCREEN;
    private int mQuickReturnHeight;
    private LinearLayout mQuickReturnView;

    private int y_total = 0;

    private void initQuickReturnChatBtn() {
        mQuickReturnView = (LinearLayout) findViewById(R.id.person_detail_bottom_container);
        mQuickReturnHeight = mQuickReturnView != null ? mQuickReturnView.getMeasuredHeight() : 0;

        Log.e("initQuickReturnChatBtn", "mQuickReturnHeight=" + mQuickReturnHeight);
        mRecyclerView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                y_total += dy;

                int translationY = 0;

                int rawY = y_total;
                Log.e("onScrolled", y_total + "|");

                switch (mState) {
                    case STATE_OFFSCREEN:
                        if (rawY >= mMinRawY) {
                            mMinRawY = rawY;
                        } else {
                            mState = STATE_RETURNING;
                        }
                        translationY = rawY;
                        break;

                    case STATE_ONSCREEN:
                        if (rawY > mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                        translationY = rawY;
                        break;

                    case STATE_RETURNING:

                        translationY = (rawY - mMinRawY) + mQuickReturnHeight;

                        System.out.println(translationY);
                        if (translationY < 0) {
                            translationY = 0;
                            mMinRawY = rawY + mQuickReturnHeight;
                        }

                        if (rawY == 0) {
                            mState = STATE_ONSCREEN;
                            translationY = 0;
                        }

                        if (translationY > mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                        break;
                }
                mQuickReturnView.setTranslationY(translationY);
            }
        });
    }


}
