package com.zhangqing.taji.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/5/2.
 * 标签bean
 */
public class LabelBean {
    public String label_id = "";
    public String label_name = "";
    public boolean is_select = false;

    public boolean is_new_define = false;

    public LabelBean(JSONObject jsonObject) throws JSONException {
        this.label_id = jsonObject.optString("id", label_id);
        this.label_name = jsonObject.getString("skill");
    }

    //创建新定义的标签
    public LabelBean(String label_name) {
        this.label_name = label_name;
        is_new_define = true;
        is_select=true;
    }
}
