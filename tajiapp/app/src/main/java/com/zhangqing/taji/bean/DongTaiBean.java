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

    /**
     * 2016-04-19更新，采用静态内存块存储bean，所有对象不重复分配内存
     */
    private static final WeakHashMap<String, DongTaiBean> mDongTaiMap = new WeakHashMap<String, DongTaiBean>();

    public static DongTaiBean getInstance(JSONObject jsonObject) throws JSONException {
        String tid = jsonObject.getString("tid");
        DongTaiBean dongtai = mDongTaiMap.get(tid);
        if (dongtai == null) {
            synchronized (PersonInfoBean.class) {
                if (dongtai == null) {
                    dongtai = new DongTaiBean(tid, jsonObject);
                    mDongTaiMap.put(tid, dongtai);
                }
            }
        } else {
            dongtai.update(jsonObject);
        }
        return dongtai;
    }

    public static DongTaiBean getInstance(String tid) {
        return mDongTaiMap.get(tid);
    }

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
    //是否已赞
    public boolean isLike = false;


    /**
     * 构造一条动态
     *
     * @param jsonObject 传入json数据
     * @throws JSONException 仅当无动态id时才抛出异常
     */
    private DongTaiBean(String tid, JSONObject jsonObject) throws JSONException {

        //动态ID
        mId = tid;
        //发布人昵称
//        mUserName = jsonObject.optString("username", "");
        mPersonInfo = PersonInfoBean.getInstance(jsonObject);
        update(jsonObject);

        Log.e("DongTaiBean", "end|" + toString());
    }

    private void update(JSONObject jsonObject) {
        if (jsonObject == null)
            return;

        //发布人ID
        mUserId = jsonObject.optString("userid", mUserId);
        //作者ID
        mAutherId = jsonObject.optString("author", mAutherId);
        //@人的ID
        mAtId = jsonObject.optString("at", mAtId);


        //类别
        mTag = jsonObject.optString("tag", mTag);
        //类别
        mType = jsonObject.optString("type", mType);
        //标题
        mContent = jsonObject.optString("content", mContent);
        //发布时间
        mTime = jsonObject.optString("time", mTime);

        //封面URL
        mCoverUrl = jsonObject.optString("media", mCoverUrl);
        //发布者头像URL
        mAvatarUrl = jsonObject.optString("avatar", mAvatarUrl);

        //赞
        mCountLike = jsonObject.optString("likes", mCountLike);
        //转发
        mCountForward = jsonObject.optString("forward", mCountForward);
        //浏览
        mCountViews = jsonObject.optString("views", mCountViews);
        //评论
        mCountComment = jsonObject.optString("comment", mCountComment);

        //是否师徒圈
        isMasterCircle = jsonObject.optString("mastercircle", (isMasterCircle ? "1" : "0")).equals("1");
        //是否已订阅
        isFollow = jsonObject.optBoolean("is_follow", isFollow);
        //是否已赞
        isLike = jsonObject.optBoolean("is_liked", isLike);
    }

    @Override
    public String toString() {
        return mId + "|" + mContent + "|" + mTime + "|" + mUserId + "|" + mCoverUrl;
    }
}
