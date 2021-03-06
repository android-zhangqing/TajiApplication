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
import com.zhangqing.taji.adapter.listener.AvatarClickListener;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.bean.PersonInfoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/4/15.
 * 技能匹配适配器
 */
public class SkillMatchingAdapter extends RecyclerView.Adapter<SkillMatchingAdapter.MyViewHolder> {
    private List<PersonInfoBean> mPersonInfoList = new ArrayList<PersonInfoBean>();

    private static String MY_NAME = null;
    private static String MY_AVATAR = null;

    private Context mContext;

    public SkillMatchingAdapter(Context context) {
        mContext = context;

        MY_NAME = UserClass.getInstance().getStringByKey("username");
        MY_AVATAR = UserClass.getInstance().getStringByKey("avatar");

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.activity_skill_matching_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PersonInfoBean personInfoBean = mPersonInfoList.get(position);
        holder.my_name.setText(MY_NAME);
        holder.my_label.setText(personInfoBean.i_want);
        ImageLoader.getInstance().displayImage(MY_AVATAR, new ImageViewAware(holder.my_avatar),
                MyApplication.getCircleDisplayImageOptions());

        holder.to_name.setText(personInfoBean.username);
        holder.to_label.setText(personInfoBean.ta_want);

        holder.to_avatar.setOnClickListener(new AvatarClickListener(mContext, personInfoBean.userid, personInfoBean.username));

        ImageLoader.getInstance().displayImage(personInfoBean.avatar,
                new ImageViewAware(holder.to_avatar),
                MyApplication.getCircleDisplayImageOptions());
    }

    @Override
    public int getItemCount() {
        return mPersonInfoList.size();
    }

    public int addData(JSONArray jsonArray) {
        int count = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                PersonInfoBean personInfo = PersonInfoBean.getInstance(jsonObject);
                mPersonInfoList.add(personInfo);
                count++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public void clearData() {
        mPersonInfoList.clear();
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView my_avatar;
        public TextView my_name;
        public TextView my_label;

        public ImageView to_avatar;
        public TextView to_name;
        public TextView to_label;

        public MyViewHolder(View itemView) {
            super(itemView);

            my_avatar = (ImageView) itemView.findViewById(R.id.skill_matching_my_avatar);
            my_name = (TextView) itemView.findViewById(R.id.skill_matching_my_name);
            my_label = (TextView) itemView.findViewById(R.id.skill_matching_my_label);

            to_avatar = (ImageView) itemView.findViewById(R.id.skill_matching_to_avatar);
            to_name = (TextView) itemView.findViewById(R.id.skill_matching_to_name);
            to_label = (TextView) itemView.findViewById(R.id.skill_matching_to_label);

        }
    }

}
