package com.zhangqing.taji.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.view.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/20.
 */
public class SearchActivity extends BaseActivity {
    ViewPager pager;
    ViewPagerIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FragmentPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager()).init();

        ViewPager pager = (ViewPager) findViewById(R.id.search_viewpager);
        pager.setAdapter(adapter);
        indicator= (ViewPagerIndicator) findViewById(R.id.search_indicator);
        indicator.setViewPager(pager,0);


    }

    public void finishThis(View v) {
        finish();
    }

    private class MyViewPagerAdapter extends FragmentPagerAdapter {

        private List<SelectItem> mList = new ArrayList<SelectItem>();


        public MyViewPagerAdapter init() {

            SelectItem item1 = new SelectItem();
            item1.fragment = FragmentSearch.getInstance(FragmentSearch.Pager_Person);
            item1.desc = "找人";
            SelectItem item2 = new SelectItem();
            item2.fragment = FragmentSearch.getInstance(FragmentSearch.Pager_Circle);
            item2.desc = "找圈子";
            SelectItem item3 = new SelectItem();
            item3.fragment = FragmentSearch.getInstance(FragmentSearch.Pager_Lable);
            item3.desc = "找标签";

            mList.add(item1);
            mList.add(item2);
            mList.add(item3);

            return this;


        }

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position).fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.e("getPageTitle", position + "|");
            return mList.get(position).desc;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

    }

    class SelectItem {
        Fragment fragment;
        String desc;


    }


}
