package com.zhangqing.taji.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhangqing.taji.BaseFragment;

/**
 * Created by zhangqing on 2016/4/30.
 * 首页上方点击【活动】以后的这个Fragment
 */
public class FragmentHuoDongActivity extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView tv = new TextView(getActivity());
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tv.setText("暂无活动，敬请期待！");
        tv.setPadding(0, 20, 0, 0);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        return tv;

    }
}
