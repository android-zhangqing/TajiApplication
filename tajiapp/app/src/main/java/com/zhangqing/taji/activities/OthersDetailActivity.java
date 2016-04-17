package com.zhangqing.taji.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.DongTaiListAdapter;
import com.zhangqing.taji.bean.PersonInfoBean;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
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
                    mRecyclerView.setRefreshing(true);
                    //mRecyclerViewAdapter.notifyItemRangeChanged(-1,3);
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

    public void onClickBtnChat(View v) {
        RongIM.getInstance().startPrivateChat(this, mId, mName);
    }


}
