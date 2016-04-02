package com.zhangqing.taji.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.zhangqing.taji.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.ConversationConst;

/**
 * Created by zhangqing on 2016/4/2.
 */
public class SkillSelectAdapter extends BaseAdapter {
    private Context mContext;
    private List<HashMap<String, String>> hashMapList = new ArrayList<>();

    public SkillSelectAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return hashMapList.size();
    }

    @Override
    public Object getItem(int position) {
        return hashMapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_skill_setting_item, null);

        }
        return convertView;
    }


}
