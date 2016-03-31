package com.zhangqing.taji.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonsListAdapter extends BaseAdapter {
    private static final int USER_ID = 1;
    private static final int USER_NAME = 2;
    private static final int AVATAR = 3;
    private static final int SIGN = 4;


    List<Map<Integer, String>> mapList = new ArrayList<Map<Integer, String>>();

    private Context mContext;

    private ListView mListView;

    private boolean isScrolling = false;

    private int start_index, end_index;


    /**
     * @param context  必须Activity context
     * @param listView 会自动setAdapter
     */
    public PersonsListAdapter(Context context, final ListView listView) {
        this.mContext = context;
        mListView = listView;
        if (listView.getAdapter() == null)
            listView.setAdapter(this);
        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                isScrolling = true;
                Log.e("onScrollStateChanged", scrollState + "|");
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 滑动停止
                        isScrolling = false;
                        notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
// 设置当前屏幕显示的起始index和结束index
                start_index = firstVisibleItem;
                end_index = firstVisibleItem + visibleItemCount;
            }
        }));
    }

//
//    public void setScrollState(boolean isScrolling) {
//        this.isScrolling = isScrolling;
//    }

    public void initData(JSONObject jsonObject) {
        JSONArray jsonArray;

        mapList.clear();

        try {
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            Log.e("ListViewinitDataFail", "|");
            return;
        }

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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mapList.add(map);
        }
        //notifyDataSetChanged();
        //局部更新,更新可视区域,
        notifyDataSetInvalidated();
        // 整体更新,更新所有item对象,如果滑动过,更新后回到初始状态
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
            ImageLoader.getInstance().displayImage(url, new ImageViewAware(viewHolder.imgViewIcon), MyApplication.getDisplayImageOptions(),
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