package com.zhangqing.taji.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.listener.AvatarClickListener;
import com.zhangqing.taji.adapter.listener.DongTaiClickListener;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.bean.DongTaiBean;
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

    private Context mContext;
    private boolean isToEchoTime = false;

    private List<DongTaiBean> mDongTaiList = new ArrayList<DongTaiBean>();

    public DongTaiListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.view_home_hot_then_listview_item, parent, false));
        return myViewHolder;
    }

    /**
     * 该静态方法同时还供【动态详情】页面进行代码复用
     *
     * @param mContext
     * @param holder
     * @param dongTaiClass
     * @param adapter      不为null表示供bindView调用， 为null表示供外界复用
     */
    public static void updateViewHolder(final Context mContext, final MyViewHolder holder, final DongTaiBean dongTaiClass, final RecyclerView.Adapter adapter) {
        holder.tv_name.setText(dongTaiClass.mPersonInfo.username);
        holder.tv_content.setText(dongTaiClass.mContent);
        holder.tv_label_parent.setText(dongTaiClass.mLabelParentSkill);
        holder.tv_label_child.setText(dongTaiClass.mLabelChildTag.replaceAll("\\.","  "));

        holder.tv_count_forward.setText(dongTaiClass.mCountForward);
        holder.tv_count_comment.setText(dongTaiClass.mCountComment);
        holder.tv_count_like.setText(dongTaiClass.mCountLike);

        holder.iv_count_like_icon.setImageResource(dongTaiClass.isLike ?
                R.drawable.icon_tab_home_hot_favor_selelct2 : R.drawable.icon_tab_home_hot_favor);

        if (dongTaiClass.mPersonInfo.userid.equals(UserClass.getInstance().userId)) {
            holder.tv_follow.setVisibility(View.INVISIBLE);
        } else {
            holder.tv_follow.setVisibility(View.VISIBLE);
            updateFollowButton(holder.tv_follow, dongTaiClass.mPersonInfo.is_follow);
        }

        holder.iv_avatar.setOnClickListener(new AvatarClickListener(mContext, dongTaiClass.mUserId, dongTaiClass.mPersonInfo.username));

        if (adapter != null) {
            View.OnClickListener onClickListener = new DongTaiClickListener(mContext,
                    dongTaiClass.mId);
            holder.cmv_media.setOnClickListener(onClickListener);
            holder.ll_count_comment_container.setOnClickListener(onClickListener);
        }

        /**
         * 订阅、关注 按钮
         */
        holder.tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserClass.getInstance().dongTaiDoFollow(dongTaiClass.mUserId,
                        !dongTaiClass.mPersonInfo.is_follow, new VolleyInterface(mContext.getApplicationContext()) {
                            @Override
                            public void onMySuccess(JSONObject jsonObject) {
                                Log.e("json", jsonObject.toString());
                                if (jsonObject.has("msg"))
                                    dongTaiClass.mPersonInfo.is_follow = !dongTaiClass.mPersonInfo.is_follow;
                                Toast.makeText(mContext, jsonObject.optString("msg", "操作失败"), Toast.LENGTH_SHORT).show();

                                if (adapter != null) {
                                    //供RecyclerView调用，以实现联动
                                    adapter.notifyDataSetChanged();
                                } else {
                                    //供动态详情页面调用，只需更新当前的View的按钮即可
                                    updateFollowButton(holder.tv_follow, dongTaiClass.mPersonInfo.is_follow);
                                }
                            }

                            @Override
                            public void onMyError(VolleyError error) {

                            }
                        });

            }
        });

        /**
         * 转发 按钮
         */
        holder.ll_count_forward_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ll_count_forward_container.setEnabled(false);
                UserClass.getInstance().dongTaiDoForward(dongTaiClass.mId, new VolleyInterface(mContext.getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        holder.ll_count_forward_container.setEnabled(true);
                        holder.tv_count_forward.setText("" + (Integer.valueOf(dongTaiClass.mCountForward) + 1));
                        Toast.makeText(mContext.getApplicationContext(), "转发成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        holder.ll_count_forward_container.setEnabled(true);
                    }
                });
            }
        });

        /**
         * 赞 按钮
         */
        holder.ll_count_like_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ll_count_like_container.setEnabled(false);
                UserClass.getInstance().dongTaiDoLike(dongTaiClass.mId, new VolleyInterface(mContext.getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {

                        holder.ll_count_like_container.setEnabled(true);
                        try {
                            String msg = jsonObject.getString("msg");
                            if (msg.equals("点赞成功")) {
                                if (!dongTaiClass.isLike) {
                                    dongTaiClass.mCountLike = "" + (Integer.valueOf(dongTaiClass.mCountLike) + 1);
                                }
                                dongTaiClass.isLike = true;
                            } else if (msg.equals("取消点赞成功")) {
                                if (dongTaiClass.isLike) {
                                    dongTaiClass.mCountLike = "" + (Integer.valueOf(dongTaiClass.mCountLike) - 1);
                                }
                                dongTaiClass.isLike = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dongTaiClass.isLike = !dongTaiClass.isLike;
                        }

                        holder.iv_count_like_icon.setImageResource(dongTaiClass.isLike ?
                                R.drawable.icon_tab_home_hot_favor_selelct2 : R.drawable.icon_tab_home_hot_favor);
                        holder.tv_count_like.setText(dongTaiClass.mCountLike);

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        holder.ll_count_like_container.setEnabled(true);
                    }
                });
            }
        });

        ImageLoader.getInstance().displayImage(dongTaiClass.mAvatarUrl, new ImageViewAware(holder.iv_avatar), MyApplication.getCircleDisplayImageOptions());
        ImageLoader.getInstance().displayImage(dongTaiClass.mCoverUrl, new ImageViewAware(holder.cmv_media.picSingleImageView));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final DongTaiBean dongTaiClass = mDongTaiList.get(position);

        updateViewHolder(mContext, holder, dongTaiClass, this);

        if (isToEchoTime) {
            holder.tv_follow.setVisibility(View.VISIBLE);
            holder.tv_follow.setText(dongTaiClass.mTime.substring(dongTaiClass.mTime.indexOf("-") + 1, dongTaiClass.mTime.lastIndexOf(":")));
            holder.tv_follow.setTextColor(Color.parseColor("#A861B3"));
            holder.tv_follow.setBackgroundResource(0);
        }

    }

    public void setToEchoTime(boolean isToEchoTime) {
        this.isToEchoTime = isToEchoTime;
    }

    private static void updateFollowButton(TextView tv_follow, boolean isToFollow) {
        tv_follow.setText(isToFollow ? "√ 已订阅" : "+订阅");
        tv_follow.setTextColor(isToFollow ? Color.parseColor("#9F61AA") : Color.parseColor("#16FBCC"));
        tv_follow.setBackgroundResource(isToFollow ? R.drawable.home_hot_btn_concern_bg_reverse : R.drawable.home_hot_btn_concern_bg);
    }


    @Override
    public int getItemCount() {
        return mDongTaiList.size();
    }

    /**
     * 清空所有原数据
     */
    public void clearData() {
        boolean willNotifyData = false;
        if (mDongTaiList.size() != 0) willNotifyData = true;
        mDongTaiList.clear();
        if (willNotifyData)
            notifyDataSetChanged();
    }


    public int addData(JSONArray jsonArray, RecyclerViewPullable recyclerViewPullable) {
        int count = 0;
        int insert_position = mDongTaiList.size() - 1;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                //notifyItemChanged(lastCount+i);
                mDongTaiList.add(DongTaiBean.getInstance(jsonArray.getJSONObject(i)));
                insert_position++;
                notifyItemChanged(insert_position);
                //notifyItemChanged(lastCount+i);
                //notifyItemRangeChanged(lastCount+i, getItemCount());
                count++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        ImageView iv_avatar;
        ComplicatedMediaView cmv_media;
        TextView tv_content;

        TextView tv_label_parent;
        TextView tv_label_child;
        LinearLayout ll_label_container;

        TextView tv_count_forward;
        TextView tv_count_comment;
        TextView tv_count_like;

        LinearLayout ll_count_forward_container;
        LinearLayout ll_count_comment_container;
        LinearLayout ll_count_like_container;
        ImageView iv_count_like_icon;

        TextView tv_follow;


        public MyViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.home_hot_then_name);
            iv_avatar = (ImageView) itemView.findViewById(R.id.home_hot_then_avatar);
            cmv_media = (ComplicatedMediaView) itemView.findViewById(R.id.home_hot_then_media);
            tv_content = (TextView) itemView.findViewById(R.id.home_hot_then_content);

            tv_label_parent = (TextView) itemView.findViewById(R.id.home_hot_then_label_parent);
            tv_label_child = (TextView) itemView.findViewById(R.id.home_hot_then_label_child);
            ll_label_container = (LinearLayout) itemView.findViewById(R.id.home_hot_then_label_container);

            tv_count_forward = (TextView) itemView.findViewById(R.id.home_hot_then_count_forward);
            tv_count_comment = (TextView) itemView.findViewById(R.id.home_hot_then_count_comment);
            tv_count_like = (TextView) itemView.findViewById(R.id.home_hot_then_count_like);

            ll_count_forward_container = (LinearLayout) itemView.findViewById(R.id.home_hot_then_count_forward_container);
            ll_count_comment_container = (LinearLayout) itemView.findViewById(R.id.home_hot_then_count_comment_container);
            ll_count_like_container = (LinearLayout) itemView.findViewById(R.id.home_hot_then_count_like_container);

            iv_count_like_icon = (ImageView) itemView.findViewById(R.id.home_hot_then_like_icon);

            tv_follow = (TextView) itemView.findViewById(R.id.home_hot_then_follow);
        }
    }
}
