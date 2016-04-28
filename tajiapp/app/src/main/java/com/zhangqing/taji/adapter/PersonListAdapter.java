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
import com.zhangqing.taji.bean.PersonInfoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/4/26.
 * 新版的 列出人列表 Adapter
 */
public class PersonListAdapter extends MyRecyclerViewAdapter<PersonListAdapter.MyHolder> {

    private Context mContext;
    private List<PersonInfoBean> mPersonInfoList = new ArrayList<PersonInfoBean>();

    public PersonListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).
                inflate(R.layout.view_persons_listview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder viewHolder, int position) {
        PersonInfoBean personInfoBean = mPersonInfoList.get(position);

        viewHolder.name_tv.setText(personInfoBean.username);
        viewHolder.sign_tv.setText(personInfoBean.signature);

        viewHolder.container_ll.setOnClickListener(new AvatarClickListener(mContext, personInfoBean.userid, personInfoBean.username));

        ImageLoader.getInstance().displayImage(personInfoBean.avatar,
                new ImageViewAware(viewHolder.avatar_iv), MyApplication.getCircleDisplayImageOptions());
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

        public MyHolder(View convertView) {
            super(convertView);

            avatar_iv = (ImageView) convertView.findViewById(R.id.persons_item_avatar);
            name_tv = (TextView) convertView.findViewById(R.id.persons_item_name);
            sign_tv = (TextView) convertView.findViewById(R.id.persons_item_sign);
            container_ll = (LinearLayout) convertView.findViewById(R.id.persons_item_container);
        }
    }

    /**
     * 增加新数据
     *
     * @param jsonObject 网络获取得到的新数据
     * @return 返回新增数据条数
     */
    public synchronized int addData(JSONObject jsonObject) {
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

    public void clearData() {
        boolean willNotifyData = (mPersonInfoList.size() != 0);
        mPersonInfoList.clear();
        if (willNotifyData) notifyDataSetChanged();
    }
}
