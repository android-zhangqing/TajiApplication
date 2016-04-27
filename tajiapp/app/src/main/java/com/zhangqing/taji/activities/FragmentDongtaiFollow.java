package com.zhangqing.taji.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseFragment;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.DongTaiListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/27.
 * 订阅的人发布的动态的界面
 */
public class FragmentDongtaiFollow extends BaseFragment {

    private RecyclerViewPullable mRecyclerView;
    private DongTaiListAdapter mRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dongtai_myfollow, container, false);
        mRecyclerView = (RecyclerViewPullable) v.findViewById(R.id.myfollow_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRecyclerViewAdapter = new DongTaiListAdapter(getActivity()));
        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int loadingPage) {
                UserClass.getInstance().doGetDongTaiMyFollow(loadingPage, new VolleyInterface(getActivity().getApplicationContext()) {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_ERROR);
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


        return v;

    }
}
