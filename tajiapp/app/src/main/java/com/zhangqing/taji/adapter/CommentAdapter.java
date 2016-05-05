package com.zhangqing.taji.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.listener.AvatarClickListener;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.bean.CommentBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/4/19.
 * 评论Adapter
 */
public class CommentAdapter extends MyRecyclerViewAdapter<CommentAdapter.MyHolder> {
    public CommentAdapter(Context context) {
        this.mContext = context;
    }

    private Context mContext;
    private List<CommentBean> mCommentBeanList = new ArrayList<CommentBean>();


    public int addData(JSONObject jsonObject) {
        int count = 0;
        JSONArray jsonArray;
        try {
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
            return count;
        }

        int insert_position = mCommentBeanList.size() - 1;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                CommentBean commentBean = new CommentBean(jsonArray.getJSONObject(i));
                mCommentBeanList.add(commentBean);
                count++;

                insert_position++;
                notifyItemInserted(insert_position + 1);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        return count;
    }

    public void clearData() {
        boolean willNotify = mCommentBeanList.size() != 0;
        mCommentBeanList.clear();
        if (willNotify)
            notifyDataSetChanged();
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(
                R.layout.activity_dongtai_detail_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        final CommentBean commentBean = mCommentBeanList.get(position);

        holder.tv_name.setText(commentBean.username);
        holder.tv_content.setText(commentBean.content);
        String real_time = commentBean.time.substring(commentBean.time.indexOf("-") + 1, commentBean.time.length());
        holder.tv_time.setText(real_time);
        holder.iv_avatar.setOnClickListener(new AvatarClickListener(mContext, commentBean.userid, commentBean.username));

        //评论是自己发的，则显示删除按钮
        if (commentBean.userid.equals(UserClass.getInstance().userId)) {
            holder.tv_delete.setVisibility(View.VISIBLE);
            holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserClass.getInstance().dongTaiDoCommentDelete(commentBean.cid, new VolleyInterface(mContext.getApplicationContext()) {
                        @Override
                        public void onMySuccess(JSONObject jsonObject) {
                            int index = mCommentBeanList.indexOf(commentBean);
                            mCommentBeanList.remove(commentBean);
                            notifyItemRemoved(index + 1);
                        }

                        @Override
                        public void onMyError(VolleyError error) {

                        }
                    });
                }
            });
        } else {
            holder.tv_delete.setVisibility(View.GONE);
        }

        ImageLoader.getInstance().displayImage(commentBean.avatar,
                new ImageViewAware(holder.iv_avatar), MyApplication.getCircleDisplayImageOptions());

    }

    @Override
    public int getItemCount() {
        return mCommentBeanList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        ImageView iv_avatar;
        TextView tv_name;
        TextView tv_content;
        TextView tv_time;

        TextView tv_delete;

        public MyHolder(View itemView) {
            super(itemView);
            iv_avatar = (ImageView) itemView.findViewById(R.id.dongtai_detail_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.dongtai_detail_name);
            tv_time = (TextView) itemView.findViewById(R.id.dongtai_detail_time);
            tv_content = (TextView) itemView.findViewById(R.id.dongtai_detail_content);

            tv_delete = (TextView) itemView.findViewById(R.id.dongtai_detail_item_delete);
        }
    }
}
