package com.zhangqing.taji.bean;

import com.zhangqing.taji.activities.ZanListActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/5/7.
 * 赞我的 评论我的 at我的 ，此处不使用单例模式
 */
public class ZanBean {

    public DongTaiBean mDongTai;

    public String event_userid = "";
    public String event_username = "";
    public String event_avatar = "";
    public String eventString = "";

    public String time = "";

    public ZanBean(String type, JSONObject jsonObject) throws JSONException {
        event_userid = jsonObject.optString("uid", event_userid);
        event_username = jsonObject.optString("uname", event_username);
        event_avatar = jsonObject.optString("uavatar", event_avatar);

        mDongTai = DongTaiBean.getInstance(jsonObject);

        switch (type) {
            case ZanListActivity.LIST_ZAN: {
                eventString = "赞了这条动态";
                time = jsonObject.optString("time_like", time);
                break;
            }
            case ZanListActivity.LIST_AT: {
                eventString = "提到了你";
                time = jsonObject.optString("time", time);
                break;
            }
            case ZanListActivity.LIST_COMMENT: {
                eventString = jsonObject.optString("comment_content", eventString);
                time = jsonObject.optString("time_comment", time);
                break;
            }
        }
    }
}
