package com.zhangqing.taji.bean;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.WeakHashMap;

/**
 * Created by zhangqing on 2016/4/2.
 * 动态bean
 */
public class DongTaiBean {
    //动态ID
    public String mId = "";
    //发布人ID
    public String mUserId = "";
    //    //发布人昵称
//    public String mUserName = "";
    //作者ID
    public String mAutherId = "";
    //@人的ID
    public String mAtId = "";

    public static WeakHashMap<String, PersonInfoBean> mPersonInfoMap = new WeakHashMap<String, PersonInfoBean>();
    public PersonInfoBean mPersonInfo;

    //所属大类，如绘画
    public String mTag = "";
    //类别
    public String mType = "";
    //内容
    public String mContent = "";
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
    //评论
    public String mCountComment = "";

    //是否师徒圈
    public boolean isMasterCircle = false;
    //是否已订阅
    public boolean isFollow = false;


    /**
     * 构造一条动态
     *
     * @param jsonObject 传入json数据
     * @throws JSONException 仅当无动态id时才抛出异常
     */
    public DongTaiBean(JSONObject jsonObject) throws JSONException {


        //动态ID
        mId = jsonObject.getString("tid");
        //发布人昵称
//        mUserName = jsonObject.optString("username", "");
        //发布人ID
        mUserId = jsonObject.optString("userid", "");
        //作者ID
        mAutherId = jsonObject.optString("author", "");
        //@人的ID
        mAtId = jsonObject.optString("at", "");

        synchronized (this) {
            mPersonInfo = mPersonInfoMap.get(mUserId);
            if (mPersonInfo == null) {
                mPersonInfo = new PersonInfoBean(mUserId, jsonObject);
                mPersonInfoMap.put(mUserId, mPersonInfo);
            }
        }

        //类别
        mTag = jsonObject.optString("tag", "");
        //类别
        mType = jsonObject.optString("type", "");
        //标题
        mContent = jsonObject.optString("content", "");
        //发布时间
        mTime = jsonObject.optString("time", "");

        //封面URL
        mCoverUrl = jsonObject.optString("media", "");
        //发布者头像URL
        mAvatarUrl = jsonObject.optString("avatar", "");

        //赞
        mCountLike = jsonObject.optString("likes", "");
        //转发
        mCountForward = jsonObject.optString("forward", "");
        //浏览
        mCountViews = jsonObject.optString("views", "");
        //评论
        mCountComment = jsonObject.optString("comment", "");

        //是否师徒圈
        isMasterCircle = jsonObject.optString("mastercircle", "").equals("1");
        //是否已订阅
        isFollow = jsonObject.optBoolean("is_follow", false);
        Log.e("DongTaiBean", "end|" + toString());
    }

    @Override
    public String toString() {
        return mId + "|" + mContent + "|" + mTime + "|" + mUserId + "|" + mCoverUrl;
    }
}
