package com.zhangqing.taji.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.bean.DongTaiClass;
import com.zhangqing.taji.view.ComplicatedMediaView;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/4/8.
 */
public class DongTaiListAdapter extends RecyclerView.Adapter<DongTaiListAdapter.MyViewHolder> {

    private OnItemClickListener mOnItemClickListener = null;

    /**
     * 由于自定义RecyclerView采用代理模式传入Adapter，该ParentAdapter为实际Adapter
     */
    private RecyclerView.Adapter mParentAdapter = null;

    public interface OnItemClickListener {
        public void onItemClick();
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }

    private Context mContext;

    private List<DongTaiClass> mDongTaiClassList = new ArrayList<DongTaiClass>();

    public DongTaiListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.view_home_hot_then_listview_item, parent, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final DongTaiClass dongTaiClass = mDongTaiClassList.get(position);

        holder.tv_name.setText(dongTaiClass.mPersonInfo.username);
        holder.tv_content.setText(dongTaiClass.mContent);
        holder.tv_label_parent.setText(dongTaiClass.mTag);

        holder.tv_count_forward.setText(dongTaiClass.mCountForward);
        holder.tv_count_comment.setText(dongTaiClass.mCountComment);
        holder.tv_count_like.setText(dongTaiClass.mCountLike);

        updateFollowButton(holder.tv_follow, dongTaiClass.mPersonInfo.is_follow);

        holder.iv_avatar.setOnClickListener(new AvatarClickListener(mContext, dongTaiClass.mUserId, dongTaiClass.mPersonInfo.username));

        holder.tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserClass.getInstance().doFollow(dongTaiClass.mUserId,
                        !dongTaiClass.mPersonInfo.is_follow, new VolleyInterface(mContext.getApplicationContext()) {
                            @Override
                            public void onMySuccess(JSONObject jsonObject) {
                                Log.e("json", jsonObject.toString());
                                if (jsonObject.has("msg"))
                                    dongTaiClass.mPersonInfo.is_follow = !dongTaiClass.mPersonInfo.is_follow;
                                Toast.makeText(mContext, jsonObject.optString("msg", "操作失败"), Toast.LENGTH_SHORT).show();

                                if (mParentAdapter != null) {
                                    mParentAdapter.notifyDataSetChanged();
                                } else {
                                    notifyDataSetChanged();
                                }
                                //updateFollowButton(holder.tv_follow, dongTaiClass.mPersonInfo.is_follow);
                            }

                            @Override
                            public void onMyError(VolleyError error) {

                            }
                        });

            }
        });

        holder.ll_count_forward_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int last_count = Integer.valueOf(dongTaiClass.mCountForward);
                holder.tv_count_forward.setText(last_count + 1 + "");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "转发失败", Toast.LENGTH_SHORT).show();
                        holder.tv_count_forward.setText(last_count + "");
                    }
                }, 500);
            }
        });
        holder.ll_count_like_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.cmv_media.picSingleImageView.setImageBitmap(null);
        ImageLoader.getInstance().displayImage(dongTaiClass.mAvatarUrl, holder.iv_avatar, MyApplication.getCircleDisplayImageOptions());
        ImageLoader.getInstance().displayImage(dongTaiClass.mCoverUrl, holder.cmv_media.picSingleImageView);
    }

    public void setParentAdapter(RecyclerView.Adapter adapter) {
        mParentAdapter = adapter;
    }

    private static void updateFollowButton(TextView tv_follow, boolean isToFollow) {
        tv_follow.setText(isToFollow ? "√ 已订阅" : "+订阅");
        tv_follow.setTextColor(isToFollow ? Color.parseColor("#9F61AA") : Color.parseColor("#16FBCC"));
        tv_follow.setBackgroundResource(isToFollow ? R.drawable.home_hot_btn_concern_bg_reverse : R.drawable.home_hot_btn_concern_bg);
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


    public int addData(JSONArray jsonArray, RecyclerViewPullable recyclerViewPullable) {
        int count = 0;
        int insert_position = mDongTaiClassList.size() - 1;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                //notifyItemInserted(lastCount+i);
                mDongTaiClassList.add(new DongTaiClass(jsonArray.getJSONObject(i)));
                insert_position++;
                if (recyclerViewPullable != null)
                    recyclerViewPullable.notifyItemInserted(insert_position);
                //notifyItemInserted(lastCount+i);
                //notifyItemRangeChanged(lastCount+i, getItemCount());
                count++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //notifyDataSetChanged();
        return count;
    }

    /**
     * 增加新数据
     *
     * @param jsonArray
     * @return 增加条数
     */
    public int addData(JSONArray jsonArray) {
        return addData(jsonArray, null);
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

        LinearLayout ll_count_forward_container;
        LinearLayout ll_count_comment_container;
        LinearLayout ll_count_like_container;

        TextView tv_follow;


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

            ll_count_forward_container = (LinearLayout) itemView.findViewById(R.id.home_hot_then_count_forward_container);
            ll_count_comment_container = (LinearLayout) itemView.findViewById(R.id.home_hot_then_count_comment_container);
            ll_count_like_container = (LinearLayout) itemView.findViewById(R.id.home_hot_then_count_like_container);

            tv_follow = (TextView) itemView.findViewById(R.id.home_hot_then_follow);
        }
    }
}
