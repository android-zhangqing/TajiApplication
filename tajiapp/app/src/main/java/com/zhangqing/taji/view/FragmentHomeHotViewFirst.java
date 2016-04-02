package com.zhangqing.taji.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhangqing.taji.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页-热门-广场
 */
public class FragmentHomeHotViewFirst extends LinearLayout {
    private final int MESSAGE_SCALE_ANIMATION = 131;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private View containerView;
    private ChildViewPagerAdapter pagerAdapterInside;
    private ViewPager viewPagerInside;
    private GridView gridView;
    private ObservableScrollView scrollView;
    private RelativeLayout viewPagerInsideContainer;

    TextView testview;

    int newHeight = 0;
    ;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg);
            if (msg.what == MESSAGE_SCALE_ANIMATION) {
                // if(newHeight==0) newHeight=swipeRefreshLayout.getHeight();
                // RelativeLayout.LayoutParams lastParams =
                // (RelativeLayout.LayoutParams)swipeRefreshLayout.getLayoutParams();
                // //testview.setText(" "+swipeRefreshLayout.getWidth());
//				 newHeight=newHeight+10;
//				 RelativeLayout.LayoutParams newParams =new
//				 RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
//				 newHeight) ;
//				 newParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//				 swipeRefreshLayout.setLayoutParams(newParams); //使layout更新
//				 swipeRefreshLayout.setTop(swipeRefreshLayout.getTop()-10);
//				 swipeRefreshLayout.setLayoutParams(newParams);
//
            }

        }

    };

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            for (int i = 0; i < 200; i++) {
                handler.sendEmptyMessage(MESSAGE_SCALE_ANIMATION);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    };

    public FragmentHomeHotViewFirst(final Context context) {
        super(context);
        this.context = context;
        // TODO Auto-generated constructor stub
        LayoutInflater flater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        containerView = flater.inflate(R.layout.view_home_hot_first, null);
        testview = (TextView) containerView.findViewById(R.id.home_hot_first_temptest);
        scrollView = (ObservableScrollView) containerView.findViewById(R.id.home_hot_first_page_scrollview);
        viewPagerInside = (ViewPager) containerView
                .findViewById(R.id.home_hot_first_viewpagerinside);
        viewPagerInsideContainer = (RelativeLayout) containerView
                .findViewById(R.id.home_hot_first_viewpagercontainer);
        gridView = (GridView) containerView
                .findViewById(R.id.home_hot_first_gridview);


//        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
//        for (int j = 0; j < 4; j++) {
//            Map<String, Object> listitem = new HashMap<String, Object>();
//            listitem.put("pic", R.drawable.icon_tab_home_hot_loading);
//            listItems.add(listitem);
//        }
        gridView.setAdapter(new MyGridViewAdapter());
//		


//		gridView.setOnTouchListener(new OnTouchListener() {
//
//			private float lastY;
//			private boolean hasScale = false;
//			private boolean scaling = false;
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//
//				if (scaling == true)
//					return false;
//
//				testview.setText(event.getX() + " " + event.getY() + " "
//						+ event.getAction());
//
//				if (event.getAction() == 2) {
//					if (event.getY() < lastY && hasScale == false) {
//						Toast.makeText(context, "start", 2000).show();
//						hasScale = true;
//						scaling = true;
//						TranslateAnimation mHiddenAction = new TranslateAnimation(
//								Animation.RELATIVE_TO_SELF, 0.0f,
//								Animation.RELATIVE_TO_SELF, 0.0f,
//								Animation.RELATIVE_TO_SELF, 0.0f,
//								Animation.RELATIVE_TO_SELF, -1.0f);
//						mHiddenAction.setDuration(500);
//						viewPagerInsideContainer.startAnimation(mHiddenAction);
//						viewPagerInsideContainer.postDelayed(new Runnable() {
//
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//								viewPagerInsideContainer
//										.setVisibility(View.GONE);
//								scaling = false;
//							}
//						}, 500);
//						// new Thread(runnable).start();
//
//					}
//					if (event.getY() > lastY && hasScale == true) {
//
//						hasScale = false;
//						scaling = true;
//
//						TranslateAnimation mShowAction = new TranslateAnimation(
//								Animation.RELATIVE_TO_SELF, 0.0f,
//								Animation.RELATIVE_TO_SELF, 0.0f,
//								Animation.RELATIVE_TO_SELF, -1.0f,
//								Animation.RELATIVE_TO_SELF, 0.0f);
//						mShowAction.setDuration(500);
//						viewPagerInsideContainer.startAnimation(mShowAction);
//						viewPagerInsideContainer.setVisibility(View.VISIBLE);
//						viewPagerInsideContainer.postDelayed(new Runnable() {
//
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//
//								scaling = false;
//							}
//						}, 500);
//					}
//
//				}
//
//				lastY = event.getY();
//
//				return false;
//
//			}
//		});

        scrollView.setOnScrollListener(new OnScrollViewListener() {

            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y,
                                        int oldx, int oldy) {
                // TODO Auto-generated method stub
                testview.setText("scrollViewnScroll" + x + " " + y + " " + oldx + " " + oldy + " " + Math.random());
            }
        });
        gridView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (scrollState == 0) {
                    final View v = (containerView
                            .findViewById(R.id.home_hot_first_viewpagercontainer));
//                    Toast.makeText(
//                            context,
//                            "gridViewscroll " + gridView.getLastVisiblePosition() + " "
//                                    + gridView.getFirstVisiblePosition(),
//                            Toast.LENGTH_SHORT).show();

                    if (v.getVisibility() == View.GONE)
                        return;

                    //

                    // ScaleAnimation mHiddenActionScale=new
                    // ScaleAnimation(1f,1f,1f, 1.4f,Animation.RELATIVE_TO_SELF,
                    // 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    // mHiddenActionScale.setDuration(3000);
                    // swipeRefreshLayout.startAnimation(mHiddenActionScale);
                    //

                    // v.postDelayed(new Runnable() {
                    //
                    // @Override
                    // public void run() {
                    // // TODO Auto-generated method stub
                    // v.setVisibility(View.GONE);
                    // }
                    // },3000);
                    // v.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) containerView
                .findViewById(R.id.home_hot_first_swipe_ly);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_red_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light));

        swipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                    @Override
                    public void onRefresh() {
                        // tv.setText("正在刷新");
                        // // TODO Auto-generated method stub
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }, 3000);
                    }
                });


        pagerAdapterInside = new ChildViewPagerAdapter(context, containerView);
        viewPagerInside.setAdapter(pagerAdapterInside);
        viewPagerInside.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                testview.setText("PageSelected " + event.getAction() + " " + Math.random());
                if (event.getAction() == 0) {
                    //swipeRefreshLayout.setEnabled(false);
                }
                if (event.getAction() == 1 || event.getAction() == 3) {
                    //swipeRefreshLayout.setEnabled(true);
                }
                return false;
            }
        });
        viewPagerInside.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                pagerAdapterInside.updatePointContainer(arg0);
                testview.setText("PageSelected " + arg0 + " " + Math.random());
                Log.e("viewPagerInside", "onPageSelected " + arg0);
//                Toast.makeText(context, Md5Util.getMd5("12311d"), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                testview.setText("pagerscroll " + arg0 + " " + arg1 + " " + arg2 + " " + Math.random());

                //swipeRefreshLayout.setEnabled(false);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                Log.e("viewPagerInside", "onPageScrollStateChanged " + arg0);
                testview.setText("PageScrollStateChanged " + arg0 + " " + Math.random());

                if (arg0 == 1) {
                    swipeRefreshLayout.setEnabled(false);
                } else if (arg0 == 0) {
                    swipeRefreshLayout.setEnabled(true);
                }

            }
        });

        addView(containerView);
        perfromOnPageSelected();
    }

    public void perfromOnPageSelected() {
        //scrollView.smoothScrollTo(0, 0);
    }

    class MyGridViewAdapter extends BaseAdapter {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

        public MyGridViewAdapter() {
            for (int j = 0; j < 8; j++) {
                Map<String, Object> listitem = new HashMap<String, Object>();
                listitem.put("pic", getResources().getDrawable(R.drawable.pic_loading_bg));
                listitem.put("icon", getResources().getDrawable(R.drawable.icon_tab_home_hot_loading_avatar));
                listitem.put("title", "加载中...");
                listitem.put("favor", 3101 + j * 11);
                listItems.add(listitem);
            }
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;


            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.view_home_hot_first_gridview_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.home_hot_page_gridview_title);
                viewHolder.textViewFavor = (TextView) convertView.findViewById(R.id.home_hot_page_gridview_favor);
                viewHolder.imgViewPic = (ImageView) convertView.findViewById(R.id.home_hot_page_first_gridview_imgview);
                viewHolder.imgViewIcon = (CircleImageView) convertView.findViewById(R.id.home_hot_page_gridview_circle_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textViewTitle.setText((String) listItems.get(position).get("title"));
            viewHolder.imgViewPic.setImageDrawable((Drawable) listItems.get(position).get("pic"));
            viewHolder.textViewFavor.setText("❤" + listItems.get(position).get("favor"));
            viewHolder.imgViewIcon.setImageDrawable((Drawable) listItems.get(position).get("icon"));
            return convertView;
        }
    }

    static class ViewHolder {
        TextView textViewTitle;
        TextView textViewFavor;
        CircleImageView imgViewIcon;
        ImageView imgViewPic;
    }
}


class ChildViewPagerAdapter extends PagerAdapter {
    private List<View> viewsInside = new ArrayList<View>();
    private Context context;
    private View containerView;
    private LinearLayout pointContainer;
    private int currentPosition;

    public ChildViewPagerAdapter(Context context, View containerView) {
        super();
        this.context = context;
        this.containerView = containerView;
        ImageView imgLoading1 = new ImageView(context);
        imgLoading1.setImageResource(R.drawable.pic_loading_bg);
        imgLoading1.setBackgroundColor(Color.parseColor("#F1F1F1"));
        ImageView imgLoading2 = new ImageView(context);
        imgLoading2.setImageResource(R.drawable.pic_loading_bg);
        imgLoading2.setBackgroundColor(Color.parseColor("#F1F1F1"));
        ImageView imgLoading3 = new ImageView(context);
        imgLoading3.setImageResource(R.drawable.pic_loading_bg);
        imgLoading3.setBackgroundColor(Color.parseColor("#F1F1F1"));
        imgLoading1.setScaleType(ScaleType.CENTER);
        imgLoading2.setScaleType(ScaleType.CENTER);
        imgLoading3.setScaleType(ScaleType.CENTER);
        viewsInside.add(imgLoading1);
        viewsInside.add(imgLoading2);
        viewsInside.add(imgLoading3);
        pointContainer = (LinearLayout) containerView
                .findViewById(R.id.home_hot_first_pager_point_container);
        updatePointContainer(0);
    }

    public void updatePointContainer(int positionSelect) {
        currentPosition = positionSelect;
        pointContainer.removeAllViews();
        for (int i = 0; i < viewsInside.size(); i++) {
            ImageView img = new ImageView(context);
            if (positionSelect == i) {
                img.setImageResource(R.drawable.icon_viewpager_point_selected);
            } else {
                img.setImageResource(R.drawable.icon_viewpager_point_unselected);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20, 20);
            lp.setMargins(10, 10, 10, 10);
            pointContainer.addView(img, lp);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        //Log.e("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "1" + viewsInside.size());
        return viewsInside.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewsInside.get(position));
        return viewsInside.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        container.removeView(viewsInside.get(position));
    }

}
