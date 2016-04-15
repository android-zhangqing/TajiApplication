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
    public String userid = "";
    public String username = "";
    public String sex = "";
    public String school = "";
    public String avatar = "";
    public String signature = "";

    public String fans = "";
    public String follow = "";
    public String dongtai = "";
    public String tudi = "";

    public String interest = "";
    public String skill = "";

    public boolean is_master = false;
    public boolean is_follow = false;

    public boolean hasMatching = false;

    //技能匹配时会用到
    public String i_want="";
    public String ta_want="";

    public PersonInfo() {

    }

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

        i_want = jsonObject.optString("i_want", "");
        ta_want = jsonObject.optString("ta_want", "");

    }

}
