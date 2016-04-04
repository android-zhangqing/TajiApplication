package com.zhangqing.taji.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.HomeHotGridAdapter;
import com.zhangqing.taji.adapter.PullableBaseAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 首页-热门-广场
 */
public class FragmentHomeHotViewFirst extends LinearLayout {
    private final int MESSAGE_SCALE_ANIMATION = 131;

    private static final int STATUS_NORMAL = 1;//分页加载空闲状态
    private static final int STATUS_LOADING = 2;//分页加载中
    private static final int STATUS_END = 3;//分页加载已尾页

    private GridViewWithHeaderAndFooter mGridView;
    private PullableBaseAdapter mGridViewAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context context;
    private View containerView;

    private ChildViewPagerAdapter mPagerInsideAdapter;
    private ViewPager mPagerInside;
    private LinearLayout mPagerInsideContainer;

    private int current_page = 0;
    private int current_loading_status = STATUS_NORMAL;

    private TextView mFootTextView;

    /**
     * 更新HeaderView——ViewPager
     */
    private void addHeaderView() {
        View ll = LayoutInflater.from(
                getContext()).inflate(R.layout.view_child_viewpager, null);
        mPagerInside = (ViewPager) ll.findViewById(R.id.home_hot_first_viewpagerinside);
        mPagerInsideContainer = (LinearLayout) ll.findViewById(R.id.home_hot_first_pager_point_container);

        mPagerInsideAdapter = new ChildViewPagerAdapter(context, mPagerInsideContainer, 6);
        mPagerInside.setAdapter(mPagerInsideAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UserClass.getInstance().doGetDongTaiBanner(new VolleyInterface(getContext().getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        try {
                            mPagerInsideAdapter.updateUrl(jsonObject.getJSONArray("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });
            }
        }, 2000);

        mPagerInside.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mPagerInsideAdapter.updatePointContainer(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                Log.e("mPagerInside", "onPageScrollStateChanged " + arg0);

                if (arg0 == 1) {
                    mSwipeRefreshLayout.setEnabled(false);
                } else if (arg0 == 0) {
                    mSwipeRefreshLayout.setEnabled(true);
                }

            }
        });

        mGridView.addHeaderView(ll);


        /**
         * addFooterView
         */
        mFootTextView = new TextView(getContext());
        mFootTextView.setGravity(Gravity.CENTER);
        mFootTextView.setPadding(0, 20, 0, 20);
        mFootTextView.setLayoutParams(
                new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT,
                        AbsListView.LayoutParams.WRAP_CONTENT));
        mGridView.addFooterView(mFootTextView);

    }

    /**
     * 上拉加载数据
     */
    private void addDataToAdapter() {
        /**
         * 判断当前加载状态，normal空闲时才加载。正在加载下一页或已加载到尾页时不再请求
         */
        if (current_loading_status != STATUS_NORMAL) return;
        current_loading_status = STATUS_LOADING;
        /**
         * current_page初始化时为0  为1时表示获取第一页
         */
        UserClass.getInstance().doGetDongTai(++current_page, new VolleyInterface(getContext().getApplicationContext()) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {
                if (current_page == 1) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mGridViewAdapter.onClearData();
                }

                if (mGridViewAdapter.onAddData(jsonObject) != 20) {
                    mFootTextView.setText("没有了呢~~");
                    current_loading_status = STATUS_END;
                } else {
                    mFootTextView.setText("正在加载...");
                    current_loading_status = STATUS_NORMAL;
                }
            }

            @Override
            public void onMyError(VolleyError error) {
                if (current_page == 1)
                    mSwipeRefreshLayout.setRefreshing(false);
                current_loading_status = STATUS_NORMAL;
                mFootTextView.setText("网络错误");
            }
        });

    }


    public FragmentHomeHotViewFirst(Context context) {
        super(context);
        this.context = context;
        // TODO Auto-generated constructor stub
        LayoutInflater flater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        containerView = flater.inflate(R.layout.view_home_hot_first, null);

        mGridView = (GridViewWithHeaderAndFooter) containerView.findViewById(R.id.home_hot_first_gridview);
        addHeaderView();
        mGridView.setAdapter(mGridViewAdapter = new HomeHotGridAdapter(getContext()));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("mGridView.ItemClick", position + "|" + id);
            }
        });
        mGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, new AbsListView.OnScrollListener() {

            int firstVisibleItem, visibleItemCount, totalItemCount;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("onScrollStateChanged", scrollState + "|" + firstVisibleItem + "|"
                        + visibleItemCount + "|" + totalItemCount + "|" + mGridView.getLastVisiblePosition());

                switch (scrollState) {
                    case SCROLL_STATE_IDLE: {//停止滑动
                        if (mGridView.getLastVisiblePosition() == totalItemCount - 1) {
                            addDataToAdapter();
                        }
                        break;
                    }
                    case SCROLL_STATE_TOUCH_SCROLL: {
                        break;
                    }
                    case SCROLL_STATE_FLING: {
                        break;
                    }

//                    public static int SCROLL_STATE_IDLE = 0;
//                    public static int SCROLL_STATE_TOUCH_SCROLL = 1;
//                    public static int SCROLL_STATE_FLING = 2;

                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.firstVisibleItem = firstVisibleItem;
                this.visibleItemCount = visibleItemCount;
                this.totalItemCount = totalItemCount;
            }
        }));
//        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            int firstVisibleItem, visibleItemCount, totalItemCount;
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                // TODO Auto-generated method stub
//                Log.e("gridViewScrollChanged", firstVisibleItem + "|" + visibleItemCount + "|" + totalItemCount);
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//                // TODO Auto-generated method stub
//                this.firstVisibleItem = firstVisibleItem;
//                this.visibleItemCount = visibleItemCount;
//                this.totalItemCount = totalItemCount;
//            }
//        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) containerView
                .findViewById(R.id.home_hot_first_swipe_ly);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_red_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light));

        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                    @Override
                    public void onRefresh() {
                        current_page = 0;
                        current_loading_status = STATUS_NORMAL;
                        mFootTextView.setText("");
                        addDataToAdapter();
                    }
                });

        addView(containerView);
        perfromOnPageSelected();

        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void perfromOnPageSelected() {
        //scrollView.smoothScrollTo(0, 0);


    }

    /**
     * 2016-04-03 优化 及时释放ImageView
     */
    private static class ChildViewPagerAdapter extends PagerAdapter {
        private String[] urls;
        private Context context;
        private LinearLayout mPointContainer;
        private int currentPosition;

        public ChildViewPagerAdapter(Context context, LinearLayout pointContainer, int initCount) {
            this.context = context;
            this.mPointContainer = pointContainer;

            urls = new String[initCount];
            updatePointContainer(0);
        }

        /**
         * 更新展示图片Url
         *
         * @param urls
         */
        public void updateUrl(String[] urls) {
            this.urls = urls;
            notifyDataSetChanged();
            updatePointContainer(0);
            Log.e("updateUrl", urls[0] + "||" + urls.length);
        }

        public void updateUrl(JSONArray jsonArray) {
            List<String> stringList = new ArrayList<String>();
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String temp = jsonObject.optString("pic", "");
                    if (temp.contains("http")) {
                        stringList.add(temp);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (stringList.size() == 0) return;

            String[] urls = new String[stringList.size()];
            for (int i = 0; i < stringList.size(); i++) {
                urls[i] = stringList.get(i);
            }
            Log.e("urls", urls.length + "|" + urls[0]);
            updateUrl(urls);
        }

        /**
         * 在ListView的PageSelectListener中进行回调，更新圆点指示器
         *
         * @param positionSelect 当前选中位置
         */
        public void updatePointContainer(int positionSelect) {
            currentPosition = positionSelect;
            mPointContainer.removeAllViews();
            for (int i = 0; i < urls.length; i++) {
                ImageView img = new ImageView(context);
                if (positionSelect == i) {
                    img.setImageResource(R.drawable.icon_viewpager_point_selected);
                } else {
                    img.setImageResource(R.drawable.icon_viewpager_point_unselected);
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(10, 10);
                lp.setMargins(15, 15, 15, 15);
                mPointContainer.addView(img, lp);
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return urls.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        /**
         * 用于notifyDataSetChange
         * 这不是PagerAdapter中的Bug，通常情况下，调用 notifyDataSetChanged方法会让ViewPager
         * 通过Adapter的getItemPosition方法查询一遍所有child view，这种情况下，所有child view
         * 位置均为POSITION_NONE，表示所有的child view都不存在，ViewPager会调用destroyItem方法销毁
         * ，并且重新生成，加大系统开销，并在一些复杂情况下导致逻辑问题。特别是对于 只是希望更新
         * child view内容的时候，造成了完全不必要的开销。
         *
         * @param object
         * @return
         */
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.e("PagerAdapterTest", "instantiateItem|" + position + "|" + container);

            ImageView iv = new ImageView(context);
            iv.setScaleType(ScaleType.FIT_XY);
            container.addView(iv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            if (urls[position] != null && urls[position].contains("http")) {
                ImageLoader.getInstance().displayImage(urls[position], new ImageViewAware(iv), MyApplication.getCornerDisplayImageOptions());
            } else {
                iv.setImageResource(R.drawable.pic_loading_bg);
                iv.setBackgroundColor(Color.parseColor("#F1F1F1"));
                iv.setScaleType(ScaleType.CENTER);
            }
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            Log.e("PagerAdapterTest", "destroyItem|" + position + "|" + container);
            container.removeView((View) object);
        }

    }


}

