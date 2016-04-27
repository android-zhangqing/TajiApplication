package com.zhangqing.taji.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.bean.ChatRoomBean;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by zhangqing on 2016/4/16.
 * 圈子(聊天室)适配器
 */
public class CircleAdapter extends RecyclerView.Adapter<CircleAdapter.MyHolder> {
    public static final int CIRCLE_ALL = 1;
    public static final int CIRCLE_MINE = 2;

    private int mCircleType = CIRCLE_ALL;

    private List<ChatRoomBean> mChatRoomList = new ArrayList<ChatRoomBean>();
    private Context mContext;

    public CircleAdapter(Context context, int type) {
        mContext = context;
        mCircleType = type;
    }

    public int addData(JSONObject jsonObject, RecyclerViewPullable recyclerViewPullable) {
        int count = 0;
        int insert_position = mChatRoomList.size() - 1;

        JSONArray jsonArray;
        try {
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                ChatRoomBean chatRoomBean = ChatRoomBean.getInstance(jsonArray.getJSONObject(i));
                mChatRoomList.add(chatRoomBean);
                count++;
                insert_position++;
                recyclerViewPullable.notifyItemChanged(insert_position);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return count;
    }

    public void clearData() {
        mChatRoomList.clear();
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.view_circle_first_listview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final ChatRoomBean chatRoomBean = mChatRoomList.get(position);

        holder.textViewTitle.setText(chatRoomBean.name);
//        holder.textViewCountOnline.setText(mChatRoomList.get(position).count_online);
//        holder.textViewCountAll.setText("在线 /" + mChatRoomList.get(position).count_all + "人");

        holder.textViewDesc.setText(chatRoomBean.description);

        holder.textViewStartConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 传入聊天室name为对话标题
                 */
                RongIM.getInstance().startConversation(mContext,
                        Conversation.ConversationType.CHATROOM,
                        chatRoomBean.rid,
                        chatRoomBean.name);
            }

        });

        ImageLoader.getInstance().displayImage(mChatRoomList.get(position).avatar, new ImageViewAware(holder.imgViewIcon), MyApplication.getCornerDisplayImageOptions());
    }

    @Override
    public int getItemCount() {
        return mChatRoomList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        ImageView imgViewIcon;
        TextView textViewTitle;
//        TextView textViewCountOnline;
//        TextView textViewCountAll;

        TextView textViewDesc;

        TextView textViewStartConversation;

        public MyHolder(View convertView) {
            super(convertView);

            textViewTitle = (TextView) convertView.findViewById(R.id.circle_first_title);
//            textViewCountOnline = (TextView) convertView.findViewById(R.id.circle_first_count_online_tv);
//            textViewCountAll = (TextView) convertView.findViewById(R.id.circle_first_count_all_tv);
            imgViewIcon = (ImageView) convertView.findViewById(R.id.circle_first_icon_iv);
            textViewDesc = (TextView) convertView.findViewById(R.id.circle_first_describe);

            textViewStartConversation = (TextView) convertView.findViewById(R.id.circle_first_chat);

        }
    }
}
