package com.zhangqing.taji.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.util.DensityUtils;

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


    public boolean toggleSelector(int position) {
        if (position < 0 || position >= selectedArray.length) return false;
        selectedArray[position] = selectedArray[position] ? false : true;
        return selectedArray[position];
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


    public SkillSelectAdapter(Context context, int totalWidth, int totalHeight) {
        this.mContext = context;
        this.mTotalHeight = totalHeight;
        this.mTotalWidth = totalWidth;
    }

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

        notifyDataSetChanged();
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

//            ViewGroup.LayoutParams l=viewHolder.iv_icon.getLayoutParams();
//            l.height=l.width;

            //动态设置图片为正方形
//            int w = View.MeasureSpec.makeMeasureSpec(0,
//                    View.MeasureSpec.UNSPECIFIED);
//            int h = View.MeasureSpec.makeMeasureSpec(0,
//                    View.MeasureSpec.UNSPECIFIED);
//            viewHolder.iv_icon.measure(w, h);
//            Log.e("SkillGetView", "convertView == null" +
//                    viewHolder.iv_icon.getMeasuredWidth() +
//                    "|" + DensityUtils.px2dp(mContext, viewHolder.iv_icon.getMeasuredWidth()));
//            int realHeight = (int) DensityUtils.px2dp(mContext, viewHolder.iv_icon.getMeasuredWidth()) * 3 / 4;
//
//            viewHolder.iv_icon.getLayoutParams().height = realHeight;
//            viewHolder.iv_icon.getLayoutParams().width = realHeight;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tv_lable.setText(hashMapList.get(position).get(SKILL_NAME));

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
