package com.zhangqing.taji.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangqing on 2016/4/2.
 */
public class SkillSelectAdapter extends BaseAdapter {
    private static final int SKILL_ID = 1;
    private static final int SKILL_NAME = 2;
    private static final int SKILL_URL = 3;

    private Context mContext;
    private int mTotalWidth;
    private int mTotalHeight;
    private List<Map<Integer, String>> hashMapList = new ArrayList<>();

    private boolean[] selectedArray = new boolean[0];


    /**
     * 切换表项选择
     *
     * @param position 位置
     * @return 切换后的选中状态，选中为true
     */
    public boolean toggleSelector(int position) {
        if (position < 0 || position >= selectedArray.length) return false;
        selectedArray[position] = selectedArray[position] ? false : true;
        return selectedArray[position];
    }

    /**
     * 重置多选器，当前选中记录会被清除
     */
    public void resetSelector() {
        for (int i = 0; i < selectedArray.length; i++) {
            selectedArray[i] = false;
        }

        notifyDataSetInvalidated();

    }

    /**
     * 返回用户的选择
     *
     * @return 唱歌.游泳.画画
     */
    public String getSelector() {
        String result = "";
        for (int i = 0; i < selectedArray.length; i++) {
            if (selectedArray[i]) {
                if (result.equals("")) {
                    result += hashMapList.get(i).get(SKILL_NAME);
                } else {
                    result += "." + hashMapList.get(i).get(SKILL_NAME);
                }
            }
        }
        return result;
    }

    /**
     * 设置当前选中
     *
     * @param selector 点号分割
     * @return 选中条数
     */
    public int setSelector(String selector) {
        int count = 0;
        if (selector == null || selector.equals("")) {
            notifyDataSetChanged();
            return count;
        }
        Log.e("setSelector", selector);
        String[] selecArr = selector.contains(".") ? selector.split("\\.") : new String[]{selector};
        for (int i = 0; i < hashMapList.size(); i++) {
            String lable = hashMapList.get(i).get(SKILL_NAME);

            selectedArray[i] = false;
            for (int j = 0; j < selecArr.length; j++) {
                if (lable.equals(selecArr[j])) {
                    selectedArray[i] = true;
                    count++;
                    break;
                }
            }
        }

        String log = "";
        for (boolean i : selectedArray) {
            log = log + i + "|";
        }
        Log.e("setSelector", log + "|");

        notifyDataSetChanged();

        return count;
    }


    public SkillSelectAdapter(Context context, int totalWidth, int totalHeight) {
        this.mContext = context;
        this.mTotalHeight = totalHeight;
        this.mTotalWidth = totalWidth;
    }

    /**
     * 初始化其中的数据
     *
     * @param jsonObject 获取的网络数据
     * @return
     */
    public int initData(JSONObject jsonObject) {
        JSONArray jsonArray;

        try {
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            Log.e("ListViewinitDataFail", "|");
            return 0;
        }

        int count = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            Map<Integer, String> map = new HashMap<Integer, String>();
            JSONObject tempJsonObject;
            try {
                tempJsonObject = jsonArray.getJSONObject(i);
                //if (tempJsonObject.has("id"))
                map.put(SKILL_ID, tempJsonObject.getString("id"));
                //if (tempJsonObject.has("skill"))
                map.put(SKILL_NAME, tempJsonObject.getString("skill"));
                //if (tempJsonObject.has("pic"))
                map.put(SKILL_URL, tempJsonObject.getString("pic"));
                count++;
                hashMapList.add(map);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        selectedArray = new boolean[count];

        //notifyDataSetChanged();
        //局部更新,更新可视区域,
        //notifyDataSetInvalidated();
        // 整体更新,更新所有item对象,如果滑动过,更新后回到初始状态

        return count;


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
        ViewHolder viewHolder;

        if (convertView == null) {
            Log.e("SkillGetView", "convertView == null");
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_skill_setting_item, null);
            convertView.setLayoutParams(new AbsListView.LayoutParams(mTotalWidth / 4, mTotalHeight / 4));

            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.skill_setting_iv_icon);
            viewHolder.iv_selector = (ImageView) convertView.findViewById(R.id.skill_setting_iv_selector);
            viewHolder.tv_lable = (TextView) convertView.findViewById(R.id.skill_setting_tv_lable);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tv_lable.setText(hashMapList.get(position).get(SKILL_NAME));
        if (selectedArray[position]) {
            viewHolder.iv_selector.setImageResource(R.drawable.icon_intskill_table_select);
        } else {
            viewHolder.iv_selector.setImageResource(R.drawable.icon_intskill_table_unselect);
        }

        ImageLoader.getInstance().displayImage(hashMapList.get(position).get(SKILL_URL),
                new ImageViewAware(viewHolder.iv_icon),
                MyApplication.getCornerDisplayImageOptions(),
                new ImageSize(100, 100), null, null);

        return convertView;
    }


    public static class ViewHolder {
        ImageView iv_icon;
        TextView tv_lable;
        public ImageView iv_selector;
    }

}
