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
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.listener.AvatarClickListener;
import com.zhangqing.taji.adapter.listener.DongTaiClickListener;
import com.zhangqing.taji.bean.ZanBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/5/7.
 * 赞我的、评论我的、at我的
 */
public class ZanListAdapter extends RecyclerView.Adapter<ZanListAdapter.MyHolder> {


    private Context mContext;
    private List<ZanBean> mZanList = new ArrayList<ZanBean>();

    private String mType;

    public ZanListAdapter(Context context, String type) {
        mContext = context;
        mType = type;
    }


    public int addData(JSONObject jsonObject) {

        JSONArray jsonArray;
        try {
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }

        int count = 0;
        int insert_position = mZanList.size() - 1;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                //notifyItemChanged(lastCount+i);
                mZanList.add(new ZanBean(mType, jsonArray.getJSONObject(i)));
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


    public void clearData() {
        boolean willNotify = mZanList.size() != 0;
        mZanList.clear();
        if (willNotify) notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_zan_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        ZanBean zanBean = mZanList.get(position);

        holder.tv_author.setText("@" + zanBean.mDongTai.mPersonInfo.username);
        holder.tv_time.setText(zanBean.time);
        holder.tv_event.setText(zanBean.eventString);
        holder.tv_name.setText(zanBean.event_username);
        holder.tv_content.setText(zanBean.mDongTai.mContent);

        ImageLoader.getInstance().displayImage(zanBean.event_avatar, new ImageViewAware(holder.iv_avatar), MyApplication.getCircleDisplayImageOptions());
        ImageLoader.getInstance().displayImage(zanBean.mDongTai.mCoverUrl, new ImageViewAware(holder.iv_cover), MyApplication.getNormalDisplayImageOptions());

        holder.iv_avatar.setOnClickListener(new AvatarClickListener(mContext, zanBean.event_userid, zanBean.event_username));
        holder.ll_container_dongtai.setOnClickListener(new DongTaiClickListener(mContext, zanBean.mDongTai.mTid));
    }

    @Override
    public int getItemCount() {
        return mZanList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        ImageView iv_avatar;
        ImageView iv_cover;

        TextView tv_name;
        TextView tv_time;

        TextView tv_event;

        TextView tv_author;
        TextView tv_content;

        LinearLayout ll_container_dongtai;

        public MyHolder(View itemView) {
            super(itemView);

            iv_avatar = (ImageView) itemView.findViewById(R.id.zan_list_item_avatar);
            iv_cover = (ImageView) itemView.findViewById(R.id.zan_list_item_cover);

            tv_author = (TextView) itemView.findViewById(R.id.zan_list_item_author);
            tv_content = (TextView) itemView.findViewById(R.id.zan_list_item_content);
            tv_event = (TextView) itemView.findViewById(R.id.zan_list_item_event_tv);
            tv_name = (TextView) itemView.findViewById(R.id.zan_list_item_name);
            tv_time = (TextView) itemView.findViewById(R.id.zan_list_item_time);

            ll_container_dongtai = (LinearLayout) itemView.findViewById(R.id.zan_list_item_container_dongtai);
        }
    }
}
