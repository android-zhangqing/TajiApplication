package com.zhangqing.taji.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zhangqing.taji.R;
import com.zhangqing.taji.activities.EditActivity;
import com.zhangqing.taji.bean.LabelBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/4/30.
 * 选择标签界面的子标签加载适配器
 */
public class LabelAdapter extends MyRecyclerViewAdapter<LabelAdapter.MyHolder> {
    private List<LabelBean> mLabelList = new ArrayList<LabelBean>();
    private Activity mContext;
    public int count_select = 0;
    private Button mButton;

    public LabelAdapter(Activity context, Button commitButton) {
        mContext = context;
        mButton = commitButton;
    }

    public void updateCommitButton() {
        mButton.setText("添加技能标签(" + count_select + "/3)");
        if (count_select == 0) {
            mButton.setEnabled(false);
        } else {
            mButton.setEnabled(true);
        }
    }

    public String getSelector() {
        String result = "";
        for (int i = 0; i < mLabelList.size(); i++) {
            if (mLabelList.get(i).is_select) {
                if (result.equals("")) {
                    result = mLabelList.get(i).label_name;
                } else {
                    result = result + "." + mLabelList.get(i).label_name;
                }
            }
        }
        return result;
    }

    @Override
    public int addData(JSONObject jsonObject) {

        int count = 0;
        JSONArray jsonArray;
        try {
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
            return count;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                mLabelList.add(new LabelBean(jsonArray.getJSONObject(i)));
                count++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
        updateCommitButton();
        return count;
    }

    public void addData(String label_name) {
        LabelBean labelBean = new LabelBean(label_name);
        mLabelList.add(labelBean);
        count_select++;
        if (count_select > 3) {
            labelBean.is_select = false;
            count_select--;
        }
        notifyDataSetChanged();
        updateCommitButton();
    }

    @Override
    public void clearData() {
        mLabelList.clear();
        updateCommitButton();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_label_select_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        //最后一个item自定义,跳转自定义界面
        if (position == mLabelList.size()) {
            holder.tv_label.setText("+自定义");
            holder.tv_label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EditActivity.class);
                    intent.putExtra("title", "自定义标签");
                    mContext.startActivityForResult(intent, EditActivity.REQUEST_ADD_SKILL_LABEL);
                }
            });
            return;
        }
        //除最后一个item外
        final LabelBean labelBean = mLabelList.get(position);
        holder.tv_label.setText(labelBean.label_name);
        holder.tv_label.setBackgroundResource(labelBean.is_select ?
                R.drawable.home_hot_btn_concern_bg_reverse : R.drawable.home_hot_btn_concern_bg);
        holder.tv_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count_select == 3 && !labelBean.is_select) return;
                labelBean.is_select = !labelBean.is_select;
                if (labelBean.is_select) {
                    count_select++;
                } else {
                    count_select--;
                }
                holder.tv_label.setBackgroundResource(labelBean.is_select ?
                        R.drawable.home_hot_btn_concern_bg_reverse : R.drawable.home_hot_btn_concern_bg);
                updateCommitButton();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLabelList.size() + 1;
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        TextView tv_label;

        public MyHolder(View itemView) {
            super(itemView);
            tv_label = (TextView) itemView.findViewById(R.id.label_item_text);
        }
    }
}
