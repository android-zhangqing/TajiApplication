package com.zhangqing.taji.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.listener.AvatarClickListener;
import com.zhangqing.taji.adapter.listener.DongTaiClickListener;
import com.zhangqing.taji.bean.DongTaiBean;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/4/16.
 */
public class DongTaiGridAdapter extends RecyclerView.Adapter<DongTaiGridAdapter.MyHolder> {

    private Context mContext;
    List<DongTaiBean> mItemsList = new ArrayList<DongTaiBean>();

    public DongTaiGridAdapter(Context context) {
        mContext = context;
    }


    /**
     * insertItem的方式添加数据(推荐！！)
     *
     * @param jsonObject
     * @param recyclerViewPullable 传入是为了 调用真实的Adapter的notifyItemInsert
     * @return
     */
    public int addData(JSONObject jsonObject, RecyclerViewPullable recyclerViewPullable) {
        int count = 0;
        JSONArray jsonArray;
        int insert_position = mItemsList.size() - 1;
        try {
            jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    DongTaiBean d = DongTaiBean.getInstance(jsonObject1);
                    //Log.e("onAddData", d.toString());
                    mItemsList.add(d);
                    insert_position++;
                    if (recyclerViewPullable != null) {
                        notifyItemChanged(insert_position);
                    }

                    count++;
                } catch (JSONException e) {
                    Log.e("AddError", jsonArray.toString());
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 普通的notifyDataSetChange方式更新视图
     *
     * @param jsonObject
     * @return
     */
    public int addData(JSONObject jsonObject) {
        return addData(jsonObject, null);
    }

    public void clearData() {
        //   boolean willNotify = mItemsList.size() != 0;
        mItemsList.clear();
        //  if (willNotify)
        notifyDataSetChanged();
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).
                inflate(R.layout.view_home_hot_first_gridview_item, null));
    }

    @Override
    public void onBindViewHolder(MyHolder viewHolder, final int position) {
        DongTaiBean dongTaiBean = mItemsList.get(position);

        viewHolder.tv_title.setText(dongTaiBean.mContent);
        viewHolder.tv_like.setText("❤" + dongTaiBean.mCountLike);

        viewHolder.iv_avatar.setOnClickListener(new AvatarClickListener(mContext,
                dongTaiBean.mUserId, dongTaiBean.mPersonInfo.username));

        viewHolder.iv_cover_press.setOnClickListener(new DongTaiClickListener(mContext,
                dongTaiBean.mTid));

        viewHolder.iv_cover.setImageBitmap(null);

        if (!dongTaiBean.mVideoUrl.equals("")) {
            viewHolder.iv_is_video.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_is_video.setVisibility(View.INVISIBLE);
        }

        ImageLoader.getInstance().displayImage(dongTaiBean.mAvatarUrl,
                new ImageViewAware(viewHolder.iv_avatar),
                MyApplication.getCircleDisplayImageOptions());
        ImageLoader.getInstance().displayImage(dongTaiBean.mCoverUrl,
                new ImageViewAware(viewHolder.iv_cover),
                MyApplication.getNormalDisplayImageOptions());

    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_like;
        ImageView iv_avatar;
        ImageView iv_cover;
        View iv_cover_press;

        ImageView iv_is_video;

        public MyHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.home_hot_page_gridview_title);
            tv_like = (TextView) itemView.findViewById(R.id.home_hot_page_gridview_favor);
            iv_cover = (ImageView) itemView.findViewById(R.id.home_hot_page_first_gridview_imgview);
            iv_cover_press = itemView.findViewById(R.id.home_hot_page_first_gridview_imgview_press);
            iv_avatar = (ImageView) itemView.findViewById(R.id.home_hot_page_gridview_circle_img);

            iv_is_video = (ImageView) itemView.findViewById(R.id.home_hot_first_is_video);
        }
    }
}
