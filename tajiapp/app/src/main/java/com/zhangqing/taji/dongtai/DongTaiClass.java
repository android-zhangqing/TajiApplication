package com.zhangqing.taji.dongtai;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/2.
 */
public class DongTaiClass {
    //动态ID
    public String mId = "";
    //发布人ID
    public String mUserId = "";
    //作者ID
    public String mAutherId = "";
    //@人的ID
    public String mAtId = "";

    //类别
    public String mType = "";
    //标题
    public String mTitle = "";
    //发布时间
    public String mTime = "";

    //封面URL
    public String mCoverUrl = "";
    //发布者头像URL
    public String mAvatarUrl = "";

    //赞
    public String mCountLike = "";
    //转发
    public String mCountForward = "";
    //浏览
    public String mCountViews = "";

    //是否师徒圈
    public boolean isMasterCircle = false;


    public DongTaiClass(JSONObject jsonObject) throws JSONException {
        //Log.e("DongTaiClass", "start|" + jsonObject.toString());

        //动态ID
        mId = jsonObject.getString("id");
        //发布人ID
        mUserId = jsonObject.optString("userid", "");
        //作者ID
        mAutherId = jsonObject.optString("author", "");
        //@人的ID
        mAtId = jsonObject.optString("at", "");

        //类别
        mType = jsonObject.optString("type", "");
        //标题
        mTitle = jsonObject.optString("content", "");
        //发布时间
        mTime = jsonObject.optString("time", "");

        //封面URL
        mCoverUrl = jsonObject.optString("media", "");
        //发布者头像URL
        mAvatarUrl = jsonObject.optString("avatar", "");

        //赞
        mCountLike = jsonObject.optString("like", "");
        //转发
        mCountForward = jsonObject.optString("forward", "");
        //浏览
        mCountViews = jsonObject.optString("views", "");

        //是否师徒圈
        isMasterCircle = jsonObject.optString("mastercircle", "").equals("1");
        //Log.e("DongTaiClass", "end" + jsonObject.toString());
    }

    @Override
    public String toString() {
        return mId + "|" + mTitle + "|" + mTime + "|" + mUserId+"|"+ mCoverUrl;
    }
}
