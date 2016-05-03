package com.zhangqing.taji.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.bean.PersonInfoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/5/3.
 * 收到拜师邀请列表的适配器
 */
public class BaiShiInviteAdapter extends MyRecyclerViewAdapter<BaiShiInviteAdapter.MyHolder> {
    private Context mContext;
    private List<PersonInfoBean> mPersonInfoList = new ArrayList<PersonInfoBean>();

    public BaiShiInviteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int addData(JSONObject jsonObject) {
        JSONArray jsonArray;

        try {
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            return 0;
        }

        int count = 0;
        int insert_position = mPersonInfoList.size() - 1;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                mPersonInfoList.add(
                        new PersonInfoBean(jsonArray.getJSONObject(i).getString("userid"), jsonArray.getJSONObject(i))
                );
                count++;
                insert_position++;
                notifyItemChanged(insert_position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    @Override
    public void clearData() {
        boolean willNotify = mPersonInfoList.size() != 0;
        mPersonInfoList.clear();
        if (willNotify) notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_baishi_invite_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder viewHolder, int position) {
        final PersonInfoBean personInfoBean = mPersonInfoList.get(position);

        viewHolder.name_tv.setText(personInfoBean.username);
        viewHolder.sign_tv.setText(personInfoBean.signature);

        viewHolder.container_ll.setOnClickListener(new AvatarClickListener(mContext, personInfoBean.userid, personInfoBean.username));

        ImageLoader.getInstance().displayImage(personInfoBean.avatar,
                new ImageViewAware(viewHolder.avatar_iv), MyApplication.getCircleDisplayImageOptions());

        viewHolder.tv_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserClass.getInstance().shiTuDoInviteRefuse(personInfoBean.userid, new VolleyInterface(mContext.getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        Toast.makeText(mContext, jsonObject.optString("msg", "已拒绝"), Toast.LENGTH_SHORT).show();
                        removeItem(personInfoBean);
                    }

                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });
            }
        });
        viewHolder.tv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserClass.getInstance().shiTuDoInviteAccept(personInfoBean.userid, new VolleyInterface(mContext.getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        Toast.makeText(mContext, jsonObject.optString("msg", "确认师徒关系成功"), Toast.LENGTH_SHORT).show();
                        removeItem(personInfoBean);
                    }

                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });
            }
        });
    }

    private void removeItem(PersonInfoBean personInfoBean) {
        int index = mPersonInfoList.indexOf(personInfoBean);
        mPersonInfoList.remove(personInfoBean);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mPersonInfoList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        ImageView avatar_iv;
        TextView name_tv;
        TextView sign_tv;
        LinearLayout container_ll;

        TextView tv_accept;
        TextView tv_refuse;

        public MyHolder(View itemView) {
            super(itemView);

            avatar_iv = (ImageView) itemView.findViewById(R.id.baishi_item_avatar);
            name_tv = (TextView) itemView.findViewById(R.id.baishi_item_name);
            sign_tv = (TextView) itemView.findViewById(R.id.baishi_item_sign);
            container_ll = (LinearLayout) itemView.findViewById(R.id.baishi_item_container);

            tv_accept = (TextView) itemView.findViewById(R.id.baishi_item_accept);
            tv_refuse = (TextView) itemView.findViewById(R.id.baishi_item_refuse);

        }
    }
}
