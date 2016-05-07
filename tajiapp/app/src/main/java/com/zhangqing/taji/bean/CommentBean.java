package com.zhangqing.taji.bean;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.WeakHashMap;

/**
 * Created by zhangqing on 2016/4/19.
 * 评论封装类
 */
public class CommentBean {

    /**
     * 2016-04-19更新，采用静态内存块存储bean，所有对象不重复分配内存
     */
    private static final WeakHashMap<String, CommentBean> mCommentMap = new WeakHashMap<String, CommentBean>();

    public static CommentBean getInstance(JSONObject jsonObject) throws JSONException {
        String cid = jsonObject.getString("cid");
        CommentBean comment = mCommentMap.get(cid);
        if (comment == null) {
            synchronized (PersonInfoBean.class) {
                if (comment == null) {
                    comment = new CommentBean(jsonObject);
                    mCommentMap.put(cid, comment);
                }
            }
        } else {
            comment.update(jsonObject);
        }
        return comment;
    }

    public String cid = "";
    public String tid = "";
    public String userid = "";
    public String username = "";
    public String avatar = "";
    public String content = "";
    public String time = "";

    private CommentBean(JSONObject jsonObject) throws JSONException {
        cid = jsonObject.getString("cid");
        update(jsonObject);
        Log.e("CommentBean", toString());
    }

    private void update(JSONObject jsonObject) {
        tid = jsonObject.optString("tid", tid);
        userid = jsonObject.optString("userid", userid);

        username = jsonObject.optString("username", username);
        content = jsonObject.optString("comment", content);
        time = jsonObject.optString("time_comment", time);
        avatar = jsonObject.optString("avatar", avatar);

    }

    @Override
    public String toString() {
        return "评论" + cid + "|" + username + "|" + content + "|" + time + "|" + avatar;
    }
}
