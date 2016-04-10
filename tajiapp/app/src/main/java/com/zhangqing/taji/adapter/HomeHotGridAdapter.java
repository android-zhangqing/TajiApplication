package com.zhangqing.taji.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.dongtai.DongTaiClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/4/3.
 */
public class HomeHotGridAdapter extends PullableBaseAdapter {

    private Context mContext;
    List<DongTaiClass> mItemsList = new ArrayList<DongTaiClass>();

    public HomeHotGridAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int onAddData(JSONObject jsonObject) {
        int count = 0;
        JSONArray jsonArray;
        try {
            jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    DongTaiClass d = new DongTaiClass(jsonArray.getJSONObject(i));
                    Log.e("onAddData", d.toString());
                    mItemsList.add(d);
                    count++;
                } catch (JSONException e) {
                    Log.e("onAddDataError", jsonArray.toString());
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
        return count;
    }

    @Override
    public void onClearData() {
        mItemsList.clear();
    }

    @Override
    public ImageView getImageViewFromItemView(View viewGroup) {
        return null;
    }

    @Override
    public int getCount() {
        return mItemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.valueOf(mItemsList.get(position).mId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.e("GridAdaterGetView", position + "|" + convertView);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_home_hot_first_gridview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.home_hot_page_gridview_title);
            viewHolder.tv_like = (TextView) convertView.findViewById(R.id.home_hot_page_gridview_favor);
            viewHolder.iv_cover = (ImageView) convertView.findViewById(R.id.home_hot_page_first_gridview_imgview);
            viewHolder.iv_avatar = (ImageView) convertView.findViewById(R.id.home_hot_page_gridview_circle_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_title.setText(mItemsList.get(position).mContent);
        viewHolder.tv_like.setText("❤" + mItemsList.get(position).mCountLike);

        viewHolder.iv_avatar.setImageBitmap(null);
        viewHolder.iv_avatar.setOnClickListener(new AvatarClickListener(mContext,
                mItemsList.get(position).mUserId, mItemsList.get(position).mPersonInfo.username));
        viewHolder.iv_cover.setImageBitmap(null);

        ImageLoader.getInstance().displayImage(mItemsList.get(position).mAvatarUrl,
                new ImageViewAware(viewHolder.iv_avatar),
                MyApplication.getCircleDisplayImageOptions(),
                new ImageSize(100, 100), null, null);
        ImageLoader.getInstance().displayImage(mItemsList.get(position).mCoverUrl,
                new ImageViewAware(viewHolder.iv_cover),
                MyApplication.getNormalDisplayImageOptions()
        );

        //mItemsList.get(position).get(MAP_COVER_URL)
        //mItemsList.get(position).get(MAP_AVATAR_URL)
        return convertView;
    }


    private static class ViewHolder {
        TextView tv_title;
        TextView tv_like;
        ImageView iv_avatar;
        ImageView iv_cover;
    }


}
