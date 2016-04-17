package com.zhangqing.taji.activities;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseFragment;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.CircleAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.FlexiListView;
import com.zhangqing.taji.view.FragmentCircleAll;
import com.zhangqing.taji.view.ViewPagerIndicator;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

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
        ListView listView3;
        ListView listView2;

        View[] views = new View[2];

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views[position]);
            return views[position];
        }

        public CirclePagerAdapter(final Context context) {
            Log.e("CirclePagerAdapter", "###构造");



            views[0] = new FragmentCircleAll(context);


            View v = LayoutInflater.from(context).inflate(R.layout.view_circle_second, null, false);

            listView2 = (ListView) v.findViewById(R.id.circle_second_listview1);
            listView3 = (ListView) v.findViewById(R.id.circle_second_listview2);

            listView2.setAdapter(new CircleListViewAdapter(context, 4));
            listView3.setAdapter(new CircleListViewAdapter(context, 3));
            views[1] = v;


            // listView1.setAdapter();
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

        //RongIM.getInstance().getRongIMClient().getConversationList(Conversation.ConversationType.CHATROOM);
        return v;
    }


    static class CircleListViewAdapter extends BaseAdapter {
        List<Map<String, Object>> itemMapList;
        Context context;

        public CircleListViewAdapter(Context context, int initCount) {
            this.context = context;
            Log.e("CircleListViewAdapter", "###构造");
            itemMapList = new ArrayList<Map<String, Object>>();
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.pic_loading_bg);
            for (int i = 0; i < initCount; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("icon", bm);
                map.put("title", "标题" + i);
                map.put("count_online", 10 + 12 * i);
                map.put("count_all", 123 + 23 * i);
                itemMapList.add(map);
            }


        }

        @Override
        public int getCount() {

            // Log.e("CircleListViewAdapter", "###getCount");
            return itemMapList.size();
        }

        @Override
        public Object getItem(int position) {
            Log.e("CircleListViewAdapter", "###getItem");

            return itemMapList.get(position);
        }

        @Override
        public long getItemId(int position) {
            Log.e("CircleListViewAdapter", "###getItemId");
            return 0;
        }

        class ViewHolder {
            ImageView imgViewIcon;
            TextView textViewTitle;
            TextView textViewCountOnline;
            TextView textViewCountAll;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Log.e("CircleListViewAdapter", "###getView" + position + " " + convertView);
            ViewHolder viewHolder;

            if (convertView == null) {
                //  Log.e("CircleListViewAdapter", "**nullconvertView " + position + " " + parent.toString());
                convertView = LayoutInflater.from(context).inflate(R.layout.view_circle_first_listview_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.circle_first_title);
                viewHolder.textViewCountOnline = (TextView) convertView.findViewById(R.id.circle_first_count_online_tv);
                viewHolder.textViewCountAll = (TextView) convertView.findViewById(R.id.circle_first_count_all_tv);
                viewHolder.imgViewIcon = (ImageView) convertView.findViewById(R.id.circle_first_icon_iv);

                convertView.setTag(viewHolder);

            } else {

                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.imgViewIcon.setImageBitmap((Bitmap) itemMapList.get(position).get("icon"));

            viewHolder.textViewTitle.setText((String) itemMapList.get(position).get("title"));
            viewHolder.textViewCountOnline.setText("" + itemMapList.get(position).get("count_online"));
            viewHolder.textViewCountAll.setText("在线 /" + itemMapList.get(position).get("count_all") + "人");
            return convertView;

        }
    }
}
