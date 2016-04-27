package com.zhangqing.taji.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseFragment;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.PersonListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.util.ImmUtil;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/2/23.
 * 右上角搜索图标点进去来这里
 */
public class FragmentSearch extends BaseFragment implements TextView.OnEditorActionListener {
    public static final int Pager_Person = 0;
    public static final int Pager_Circle = 1;
    public static final int Pager_Lable = 2;

    private static FragmentSearch mFragmentPerson;
    private static FragmentSearch mFragmentCircle;
    private static FragmentSearch mFragmentLable;

    private int mType;

    private EditText mEditText;
    private TextView mTempTextView;

    private RecyclerViewPullable mRecyclerView;
    private PersonListAdapter mRecyclerViewAdapter;

    public static FragmentSearch getInstance(int Type) {
        switch (Type) {
            case Pager_Person:
                if (mFragmentPerson == null) {
                    mFragmentPerson = new FragmentSearch();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", FragmentSearch.Pager_Person);
                    mFragmentPerson.setArguments(bundle);
                }
                return mFragmentPerson;
            case Pager_Circle:
                if (mFragmentCircle == null) {
                    mFragmentCircle = new FragmentSearch();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", FragmentSearch.Pager_Circle);
                    mFragmentCircle.setArguments(bundle);
                }
                return mFragmentCircle;
            case Pager_Lable:
                if (mFragmentLable == null) {
                    mFragmentLable = new FragmentSearch();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", FragmentSearch.Pager_Lable);
                    mFragmentLable.setArguments(bundle);
                }
                return mFragmentLable;
        }
        return null;

    }

    public FragmentSearch() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v2 = super.onCreateView(inflater, container, savedInstanceState);
//        Log.e("super.onCreateView", v2 + "|");
        mType = getArguments().getInt("type");
        View v = inflater.inflate(R.layout.view_search, container, false);

        mEditText = (EditText) v.findViewById(R.id.search_edittext);
        mTempTextView = (TextView) v.findViewById(R.id.search_temptxt);
        mRecyclerView = (RecyclerViewPullable) v.findViewById(R.id.search_recycler_view);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.getRecyclerView().setBackgroundColor(Color.WHITE);
        mRecyclerView.setAdapter(mRecyclerViewAdapter = new PersonListAdapter(getActivity()));
        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int loadingPage) {
                UserClass.getInstance().searchForPerson(mEditText.getText().toString(), loadingPage, new VolleyInterface(getActivity().getApplicationContext()) {
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


        switch (mType) {
            case Pager_Person:
                initEdit("Ta技用户昵称/手机号码", "找人", this);
                break;
            case Pager_Circle:
                initEdit("圈子名称/圈子号", "找圈子", this);
                break;
            case Pager_Lable:
                initEdit("标签名称", "找标签", this);
                break;

        }

        return v;
    }

    private void initEdit(String hint, String lable, TextView.OnEditorActionListener listener) {
        mEditText.setHint(hint);
        mEditText.setImeActionLabel(lable, EditorInfo.IME_ACTION_SEARCH);
        mEditText.setOnEditorActionListener(listener);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId <= 6 && actionId >= 2) {
            mRecyclerView.getSwipeRefreshLayout().setEnabled(true);

            mTempTextView.requestFocus();
            ImmUtil.closeIMM(getActivity());

            mRecyclerView.getSwipeRefreshLayout().setRefreshing(true);

            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log(mType + "|");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        log(mType + "|");
    }
}
