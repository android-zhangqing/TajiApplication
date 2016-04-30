package com.zhangqing.taji.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhangqing.taji.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/4/30.
 * 选择标签界面的子标签加载适配器
 */
public class LabelAdapter extends MyRecyclerViewAdapter<LabelAdapter.MyHolder> {
    private List<String> mLabelList = new ArrayList<String>();
    private Context mContext;

    public LabelAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int addData(JSONObject jsonObject) {
        return 0;
    }

    @Override
    public void clearData() {

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_label_select_item, parent));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.tv_label.setText(mLabelList.get(position));
    }

    @Override
    public int getItemCount() {
        return mLabelList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        TextView tv_label;

        public MyHolder(View itemView) {
            super(itemView);

            tv_label = (TextView) itemView.findViewById(R.id.label_item_text);
        }
    }
}
