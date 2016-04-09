package com.zhangqing.taji.base;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/3/31.
 * 用户信息封装类
 */
public class PersonInfo {
    //"data":{"username":"GOSICFLY","sex":"男","school":"点击设置学校","mobile":"18015243501",
    // "avatar":"http:\/\/taji.whutech.com\/uploads\/4.jpg","signature":"这个人很懒，什么都没说！",
    // "fans":"26","follow":"9","dongtai":"3","tudi":"0","interest":"搭配.滑板","skill":"游戏.摄影.手工",
    // "is_master":false,"is_follow":true}
    String userid = "";
    String username = "";
    String sex = "";
    String school = "";
    String avatar = "";
    String signature = "";

    String fans = "";
    String follow = "";
    String dongtai = "";
    String tudi = "";

    String interest = "";
    String skill = "";

    boolean is_master;
    boolean is_follow;

    public PersonInfo(String userid, JSONObject jsonObject) throws JSONException {
        this.userid = userid;

        username = jsonObject.getString("username");

        sex = jsonObject.optString("sex", "");
        school = jsonObject.optString("school", "");
        avatar = jsonObject.optString("avatar", "");
        signature = jsonObject.optString("signature", "");

        fans = jsonObject.optString("fans", "");
        follow = jsonObject.optString("follow", "");
        dongtai = jsonObject.optString("dongtai", "");
        tudi = jsonObject.optString("tudi", "");

        interest = jsonObject.optString("interest", "");
        skill = jsonObject.optString("skill", "");

        is_master = jsonObject.optBoolean("is_master", false);
        is_follow = jsonObject.optBoolean("is_follow", false);
    }

}
