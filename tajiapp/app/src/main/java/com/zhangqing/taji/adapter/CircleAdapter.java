package com.zhangqing.taji.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhangqing.taji.R;
import com.zhangqing.taji.bean.ChatRoomBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/4/16.
 * 圈子(聊天室)适配器
 */
public class CircleAdapter extends RecyclerView.Adapter<CircleAdapter.MyHolder> {

    private List<ChatRoomBean> mChatRoomList = new ArrayList<ChatRoomBean>();
    private Context mContext;

    public CircleAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.view_circle_first_listview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.textViewTitle.setText(mChatRoomList.get(position).title);
        holder.textViewCountOnline.setText(mChatRoomList.get(position).count_online);
        holder.textViewCountAll.setText("在线 /" + mChatRoomList.get(position).count_all + "人");
    }

    @Override
    public int getItemCount() {
        return mChatRoomList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        ImageView imgViewIcon;
        TextView textViewTitle;
        TextView textViewCountOnline;
        TextView textViewCountAll;

        public MyHolder(View convertView) {
            super(convertView);

            textViewTitle = (TextView) convertView.findViewById(R.id.circle_first_title);
            textViewCountOnline = (TextView) convertView.findViewById(R.id.circle_first_count_online_tv);
            textViewCountAll = (TextView) convertView.findViewById(R.id.circle_first_count_all_tv);
            imgViewIcon = (ImageView) convertView.findViewById(R.id.circle_first_icon_iv);

        }
    }
}
