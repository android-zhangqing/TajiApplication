package com.zhangqing.taji.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/16.
 * 圈子(聊天室)基本封装类
 */
public class ChatRoomBean {
    public String rid;
    public String name;
    public String description;
    public String avatar;
    public String count_all;
    public String count_online;

    public ChatRoomBean(JSONObject jsonObject) throws JSONException {

        rid = jsonObject.getString("rid");
        name = jsonObject.optString("name", "");
        description = jsonObject.optString("description", "");
        avatar = jsonObject.optString("avatar", "");
        if (!avatar.contains("http")) avatar = "";
        count_all = jsonObject.optString("count_all", "");
        count_online = jsonObject.optString("count_online", "");

    }

}
