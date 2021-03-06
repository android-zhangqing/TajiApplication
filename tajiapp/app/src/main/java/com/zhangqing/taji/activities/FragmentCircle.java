package com.zhangqing.taji.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhangqing.taji.BaseFragment;
import com.zhangqing.taji.R;
import com.zhangqing.taji.view.FragmentCircleAll;
import com.zhangqing.taji.view.FragmentCircleMine;
import com.zhangqing.taji.view.ViewPagerIndicator;

/**
 * Created by zhangqing on 2016/2/7.
 * 圈子(聊天室)
 */
public class FragmentCircle extends BaseFragment {
    private ViewPager mViewPager;
    private ViewPagerIndicator mIndicator;


    public FragmentCircle() {
        super();
        Log.e("FragmentCircle", "###构造");
    }

    static class CirclePagerAdapter extends PagerAdapter {

        //热门圈子和我收藏的圈子

        View[] views = new View[2];

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views[position]);
            return views[position];
        }

        public CirclePagerAdapter(final Context context) {
            Log.e("CirclePagerAdapter", "###构造");


            views[0] = new FragmentCircleAll(context);

            views[1] = new FragmentCircleMine(context);

        }

        @Override
        public int getCount() {
            return views.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views[position]);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("FragmentCircle", "###onActivityCreated");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("FragmentCircle", "###onCreateView");
        View v = inflater.inflate(R.layout.fragment_circle, container, false);
        mViewPager = (ViewPager) v.findViewById(R.id.circle_viewpager);
        mViewPager.setAdapter(new CirclePagerAdapter(getActivity()));

        mIndicator = (ViewPagerIndicator) v.findViewById(R.id.circle_indicator);
        mIndicator.setViewPager(mViewPager, 0);

        return v;
    }
}
