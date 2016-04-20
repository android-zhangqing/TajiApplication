package com.zhangqing.taji.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/19.
 * 评论封装类
 */
public class CommentBean {

    public String cid = "";
    public String tid = "";
    public String userid = "";
    public String username = "";
    public String avatar = "";
    public String content = "";
    public String time = "";

    public CommentBean(JSONObject jsonObject) throws JSONException {
        cid = jsonObject.getString("cid");
        tid = jsonObject.getString("tid");
        userid = jsonObject.getString("userid");

        username = jsonObject.optString("username", "");
        content = jsonObject.optString("content", "");
        time = jsonObject.optString("time", "");
        avatar = jsonObject.optString("avatar", "");
    }


}
