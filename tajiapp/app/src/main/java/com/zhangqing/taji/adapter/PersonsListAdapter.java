package com.zhangqing.taji.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonsListAdapter extends PullableBaseAdapter {
    private static final int USER_ID = 1;
    private static final int USER_NAME = 2;
    private static final int AVATAR = 3;
    private static final int SIGN = 4;


    List<Map<Integer, String>> mapList = new ArrayList<Map<Integer, String>>();

    private Context mContext;

    //private ListView mListView;

    //private boolean isScrolling = false;

    private int start_index, end_index;
    private int last_start_index, last_end_index;

//    /**
//     * 分页加载时滚动到列表尾的监听器
//     */
//    public interface OnAddDataListener {
//        void addData(PersonsListAdapter personsListAdapter);
//    }
//
//    private OnAddDataListener onAddDataListener;
//
//    public void setOnAddDataListener(OnAddDataListener onAddDataListener) {
//        this.onAddDataListener = onAddDataListener;
//    }


    /**
     * @param context 必须Activity context
     */
    public PersonsListAdapter(Context context) {
        this.mContext = context;
    }
//        /**
//         * 回收情况的监听
//         */
//        listView.setRecyclerListener(new AbsListView.RecyclerListener() {
//            @Override
//            public void onMovedToScrapHeap(View view) {
//                Log.e("onMovedToScrapHeap", "start");
//                if (view != null && view instanceof LinearLayout && view.getTag() != null && view.getTag() instanceof ViewHolder) {
//                    Log.e("onMovedToScrapHeap", "isLinearLayout Tag=ViewHolder");
//                    MyApplication.viewGroupWeakReference = new WeakReference<ViewGroup>((ViewGroup) view);
////                    ViewHolder holder = (ViewHolder) view.getTag();
////                    if (holder.imgViewIcon != null) {
////                        Log.e("onMovedToScrapHeap", holder.imgViewIcon.getDrawable().getBounds().toString() + "|imgViewIcon");
////                        Drawable drawable = (holder.imgViewIcon).getDrawable();
////                        if (drawable instanceof BitmapDrawable) {
////                            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
////                            bitmapDrawable.getBitmap().recycle();
////                        }
////                    }
//                }
//            }
//        });

//        //ListView滑动时暂停加载图片 特殊情况：由下滑变为下拉刷新然后松手
//        listView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == 2) return false;
//                Log.e("onTouch", event.getAction() + "|");
//                switch (event.getAction()) {
//                    case 1:
//                    case 3: {
//                        onStopScroll(mListView);
//                        break;
//                    }
//                }
//                return false;
//            }
//        });
//        //ListView滑动时暂停加载图片
//        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                isScrolling = true;
//                Log.e("onScrollStateChanged", scrollState + "##|" + view.getLastVisiblePosition() + "|" + view.getCount());
//                switch (scrollState) {
//                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 滑动停止
//                        isScrolling = false;
//                        onStopScroll(view);//停止滚动时加载数据
//
//                        if (view.getLastVisiblePosition() == view.getCount() - 1 && onAddDataListener != null) {
//                            onAddDataListener.addData(PersonsListAdapter.this);
//                        }
//                        //notifyDataSetChanged();
//                        break;
//                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
//                        isScrolling = true;
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                // 设置当前屏幕显示的起始index和结束index
//                // Log.e("onScroll", firstVisibleItem + "|" + visibleItemCount + "|" + totalItemCount);
//                if (!isScrolling) {
//                    last_start_index = firstVisibleItem;
//                    last_end_index = firstVisibleItem + visibleItemCount;
//                }
//                start_index = firstVisibleItem;
//                end_index = firstVisibleItem + visibleItemCount;
//            }
//        }));
//    }

//
//    /**
//     * 停止滚动ListView时触发 用于加载当前可视区域的网络图片
//     *
//     * @param listView 传入待操作的ListView
//     */
//    private void onStopScroll(AbsListView listView) {
//        //重点！！！该方法返回可视区域的item的View，不要使用ChildAt(position)方法得到View
//        List<View> viewList = listView.getTouchables();
//        for (int i = 0; i < viewList.size(); i++) {
//            View v = viewList.get(i);
//
//            //只加载之前未加载的图片
//            if (v.getTag() == null) continue;
//
//            ViewHolder holder = (ViewHolder) v.getTag();
//
//            final ImageView iv = holder.imgViewIcon;
//            if (iv.getTag() != null) {
//                ImageLoader.getInstance().displayImage((String) iv.getTag(), new ImageViewAware(iv), MyApplication.getCircleDisplayImageOptions(),
//                        new ImageSize(100, 100), new SimpleImageLoadingListener() {
//                            @Override
//                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                                iv.setTag(null);
//                            }
//                        }, null);
//            }
//        }
//    }

//
//    public void setScrollState(boolean isScrolling) {
//        this.isScrolling = isScrolling;
//    }

    /**
     * 清除所有数据
     */

    public void onClearData() {
        mapList.clear();
    }


    @Override
    public ImageView getImageViewFromItemView(View viewGroup) {
        //只加载之前未加载的图片
        if (viewGroup instanceof ViewGroup && viewGroup != null && viewGroup.getTag() != null) {
            Object object = viewGroup.getTag();
            if (object != null && object instanceof ViewHolder) {
                return ((ViewHolder) object).imgViewIcon;
            }
        }
        return null;
    }


    /**
     * 增加新数据
     *
     * @param jsonObject 网络获取得到的新数据
     * @return 返回新增数据条数
     */
    @Override
    public synchronized int onAddData(JSONObject jsonObject) {
        JSONArray jsonArray;

        try {
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            Log.e("ListViewinitDataFail", "|");
            return 0;
        }

        int count = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            Map<Integer, String> map = new HashMap<Integer, String>();
            JSONObject tempJsonObject;
            try {
                tempJsonObject = jsonArray.getJSONObject(i);
                if (tempJsonObject.has("userid"))
                    map.put(USER_ID, tempJsonObject.getString("userid"));
                if (tempJsonObject.has("username"))
                    map.put(USER_NAME, tempJsonObject.getString("username"));
                if (tempJsonObject.has("avatar"))
                    map.put(AVATAR, tempJsonObject.getString("avatar"));
                if (tempJsonObject.has("signature"))
                    map.put(SIGN, tempJsonObject.getString("signature"));
                count++;
                mapList.add(map);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        notifyDataSetChanged();
        //局部更新,更新可视区域,
        //notifyDataSetInvalidated();
        // 整体更新,更新所有item对象,如果滑动过,更新后回到初始状态

        return count;
    }

    @Override
    public int getCount() {
        return mapList.size();
    }

    @Override
    public Object getItem(int position) {
        return mapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(mapList.get(position).get(USER_ID));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("getView", position + "|" + mapList.size() + "|" + mapList.get(position).toString());
        ViewHolder viewHolder;
        ImageDownloader a;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_persons_listview_item, null, false);
            viewHolder = new ViewHolder();
            viewHolder.imgViewIcon = (ImageView) convertView.findViewById(R.id.persons_item_avatar);
            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.persons_item_name);
            viewHolder.textViewSign = (TextView) convertView.findViewById(R.id.persons_item_sign);
            convertView.setTag(viewHolder);
            Log.e("getView", "viewHolder");
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String name = mapList.get(position).get(USER_NAME);
        if (name.equals("null") || name.equals(""))
            name = "游客" + mapList.get(position).get(USER_ID);
        viewHolder.textViewName.setText(name);
        viewHolder.textViewSign.setText(mapList.get(position).get(SIGN));

        String url = mapList.get(position).get(AVATAR);

        viewHolder.imgViewIcon.setTag(url);

        //滚动ListView时不加载图片
        if (isScrolling) {
            viewHolder.imgViewIcon.setImageBitmap(null);
        } else {
            //convertView.setTag(position);
            ImageLoader.getInstance().displayImage(url, new ImageViewAware(viewHolder.imgViewIcon), MyApplication.getCircleDisplayImageOptions(),
                    new ImageSize(100, 100), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            view.setTag(null);
                        }
                    }, null);
        }
        return convertView;
    }


    static class ViewHolder {
        ImageView imgViewIcon;
        TextView textViewName;
        TextView textViewSign;
    }
}