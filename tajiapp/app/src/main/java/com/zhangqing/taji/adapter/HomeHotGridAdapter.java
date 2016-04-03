package com.zhangqing.taji.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhangqing.taji.R;
import com.zhangqing.taji.view.CircleImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangqing on 2016/4/3.
 */
public class HomeHotGridAdapter extends PullableBaseAdapter {
    private static final int MAP_ID=0;
    private static final int MAP_TITLE = 1;
    private static final int MAP_COVER_URL = 2;
    private static final int MAP_AVATAR_URL = 3;
    private static final int MAP_FAVOR = 4;
    private Context mContext;
    List<Map<Integer, String>> mItemsList = new ArrayList<Map<Integer, String>>();

    public HomeHotGridAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int onAddData(JSONObject jsonObject) {
        return 0;
    }

    @Override
    public void onClearData() {

    }

    @Override
    public ImageView getImageViewFromItemView(View viewGroup) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mItemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItemsList.get(position).get();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;


        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_home_hot_first_gridview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.home_hot_page_gridview_title);
            viewHolder.tv_favor = (TextView) convertView.findViewById(R.id.home_hot_page_gridview_favor);
            viewHolder.iv_cover = (ImageView) convertView.findViewById(R.id.home_hot_page_first_gridview_imgview);
            viewHolder.iv_avatar = (ImageView) convertView.findViewById(R.id.home_hot_page_gridview_circle_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_title.setText(mItemsList.get(position).get(MAP_TITLE));
        viewHolder.tv_favor.setText("❤" + mItemsList.get(position).get(MAP_FAVOR));

        //mItemsList.get(position).get(MAP_COVER_URL)
        //mItemsList.get(position).get(MAP_AVATAR_URL)
        return convertView;
    }


    static class ViewHolder {
        TextView tv_title;
        TextView tv_favor;
        ImageView iv_avatar;
        ImageView iv_cover;
    }


}
