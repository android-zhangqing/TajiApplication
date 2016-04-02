package com.zhangqing.taji.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.PullableBaseAdapter;

import java.util.List;

/**
 * 封装的一个可上拉加载更多同时解决滚动时不加载的ListView
 * 使用步骤：
 * 1.setAdapter(PullableBaseAdapter)
 * 2.setOnAddDataListener()
 */
public class PullableListView extends ListView {

    private PullableBaseAdapter mPullableBaseAdapter;


    //universal image loader 的滑动暂停加载代理监听器
    private PauseOnScrollListener mPauseOnScrollListener;
    //子滚动监听器
    private OnScrollListener mSubOnScrollListener;
    //子子滚动监听器
    private OnScrollListener mSubSubOnScrollListener;

    private OnAddDataListener mOnAddDataListener;

    private TextView mFootView;

    public interface OnAddDataListener {
        void addData(PullableBaseAdapter pullableBaseAdapter);
    }

    public void setOnAddDataListener(OnAddDataListener l) {
        this.mOnAddDataListener = l;
    }

    public PullableListView(Context context) {
        super(context);
    }

    public PullableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFootView = new TextView(context);
        mFootView.setText("");
        mFootView.setGravity(Gravity.CENTER);
        mFootView.setPadding(0, 100, 0, 100);
        mFootView.setTextColor(Color.BLACK);
        mFootView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        super.setFooterDividersEnabled(false);
        super.addFooterView(mFootView);
    }

    public PullableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextView getFootView() {
        return mFootView;
    }

    private void initPullableAdapter() {
        super.setDivider(getResources().getDrawable(R.drawable.persons_list_divider));
        super.setDividerHeight(2);


        //ListView滑动时暂停加载图片 特殊情况：由下滑变为下拉刷新然后松手
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 2) return false;
                Log.e("onTouch", event.getAction() + "|");
                switch (event.getAction()) {
                    case 1:
                    case 3: {
                        onStopScroll();
                        break;
                    }
                }
                return false;
            }
        });

        mSubOnScrollListener = new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mSubSubOnScrollListener != null) {
                    mSubSubOnScrollListener.onScrollStateChanged(view, scrollState);
                }

                mPullableBaseAdapter.isScrolling = true;
                Log.e("onScrollStateChanged", scrollState + "##|" + view.getLastVisiblePosition() + "|" + view.getCount());
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 滑动停止
                        mPullableBaseAdapter.isScrolling = false;
                        onStopScroll();//停止滚动时加载数据

                        if (view.getLastVisiblePosition() == view.getCount() - 1 && mOnAddDataListener != null) {
                            mOnAddDataListener.addData(mPullableBaseAdapter);
                        }
                        //notifyDataSetChanged();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        mPullableBaseAdapter.isScrolling = true;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mSubSubOnScrollListener != null) {
                    mSubSubOnScrollListener.onScroll(
                            view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

            }
        };
        mPauseOnScrollListener = new PauseOnScrollListener(
                ImageLoader.getInstance(), true, true, mSubOnScrollListener);
        super.setOnScrollListener(mPauseOnScrollListener);

    }


    /**
     * 停止滚动ListView时触发 用于加载当前可视区域的网络图片
     */
    private void onStopScroll() {
        if (mPullableBaseAdapter == null) return;
        //重点！！！该方法返回可视区域的item的View，不要使用ChildAt(position)方法得到View
        List<View> viewList = getTouchables();
        for (int i = 0; i < viewList.size(); i++) {
            if (viewList.get(i) == null) continue;

            final ImageView iv = mPullableBaseAdapter.getImageViewFromItemView(viewList.get(i));
            if (iv != null && iv.getTag() != null) {
                ImageLoader.getInstance().displayImage((String) iv.getTag(), new ImageViewAware(iv), MyApplication.getDisplayImageOptions(),
                        new ImageSize(100, 100), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                iv.setTag(null);
                            }
                        }, null);
            }
        }
    }


    /**
     * 设置适配器
     *
     * @param adapter 需要实现上拉加载更多，请传入PullableBaseAdapter
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof PullableBaseAdapter) {
            mPullableBaseAdapter = (PullableBaseAdapter) adapter;
            initPullableAdapter();
            Log.e("initPullableAdapter", "aa");
        }

    }


    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mSubSubOnScrollListener = l;
    }
}
