package com.zhangqing.taji.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhangqing.taji.R;
import com.zhangqing.taji.dongtai.DongTaiClass;
import com.zhangqing.taji.view.ComplicatedMediaView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/4/8.
 */
public class DongTaiAdapter extends RecyclerView.Adapter<DongTaiAdapter.MyViewHolder> {

    private Context mContext;

    private List<DongTaiClass> mDongTaiClassList = new ArrayList<DongTaiClass>();

    public DongTaiAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.view_home_hot_then_listview_item, parent, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DongTaiClass dongTaiClass = mDongTaiClassList.get(position);
        holder.tv_name.setText(dongTaiClass.mUserName);
        holder.tv_content.setText(dongTaiClass.mContent);
        holder.tv_label_parent.setText(dongTaiClass.mTag);
        holder.tv_count_forward.setText(dongTaiClass.mCountForward);
        holder.tv_count_comment.setText(dongTaiClass.mCountComment);
        holder.tv_count_like.setText(dongTaiClass.mCountLike);

        holder.cmv_media.picSingleImageView.setImageResource(0);
        ImageLoader.getInstance().displayImage(dongTaiClass.mAvatarUrl, holder.iv_avatar);
        ImageLoader.getInstance().displayImage(dongTaiClass.mCoverUrl, holder.cmv_media.picSingleImageView);
    }

    @Override
    public int getItemCount() {
        return mDongTaiClassList.size();
    }

    /**
     * 清空所有原数据
     */
    public void clearData() {
        mDongTaiClassList.clear();
    }

    /**
     * 增加新数据
     *
     * @param jsonArray
     * @return 增加条数
     */
    public int addData(JSONArray jsonArray) {
        int count = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                mDongTaiClassList.add(new DongTaiClass(jsonArray.getJSONObject(i)));
                count++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
        return count;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        ImageView iv_avatar;
        ComplicatedMediaView cmv_media;
        TextView tv_content;

        TextView tv_label_parent;
        LinearLayout ll_label_container;

        TextView tv_count_forward;
        TextView tv_count_comment;
        TextView tv_count_like;


        public MyViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.home_hot_then_name);
            iv_avatar = (ImageView) itemView.findViewById(R.id.home_hot_then_avatar);
            cmv_media = (ComplicatedMediaView) itemView.findViewById(R.id.home_hot_then_media);
            tv_content = (TextView) itemView.findViewById(R.id.home_hot_then_content);

            tv_label_parent = (TextView) itemView.findViewById(R.id.home_hot_then_label_parent);
            ll_label_container = (LinearLayout) itemView.findViewById(R.id.home_hot_then_label_container);

            tv_count_forward = (TextView) itemView.findViewById(R.id.home_hot_then_count_forward);
            tv_count_comment = (TextView) itemView.findViewById(R.id.home_hot_then_count_comment);
            tv_count_like = (TextView) itemView.findViewById(R.id.home_hot_then_count_like);

        }
    }
}
