package com.zhangqing.taji.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.WeakHashMap;

/**
 * Created by zhangqing on 2016/4/16.
 * 圈子(聊天室)基本封装类
 */
public class ChatRoomBean {

    /**
     * 2016-04-27更新，采用静态内存块存储bean，所有对象不重复分配内存
     */
    private static final WeakHashMap<String, ChatRoomBean> mChatRoomMap = new WeakHashMap<String, ChatRoomBean>();

    public static ChatRoomBean getInstance(JSONObject jsonObject) throws JSONException {
        String rid = jsonObject.getString("rid");
        ChatRoomBean chatRoomBean = mChatRoomMap.get(rid);
        if (chatRoomBean == null) {
            synchronized (PersonInfoBean.class) {
                if (chatRoomBean == null) {
                    chatRoomBean = new ChatRoomBean(jsonObject);
                    mChatRoomMap.put(rid, chatRoomBean);
                }
            }
        }
        return chatRoomBean;
    }

    public static ChatRoomBean getInstance(String rid) {
        return mChatRoomMap.get(rid);
    }


    public String rid;
    public String name;
    public String description;
    public String avatar;
    public String count_all;
    public String count_online;

    public boolean is_mine;

    private ChatRoomBean(JSONObject jsonObject) throws JSONException {

        rid = jsonObject.getString("rid");
        name = jsonObject.optString("name", "");
        description = jsonObject.optString("description", "");
        avatar = jsonObject.optString("avatar", "");
        if (!avatar.contains("http")) avatar = "";
        count_all = jsonObject.optString("total", "");
        count_online = jsonObject.optString("user", "");

        is_mine = jsonObject.optBoolean("is_mine", false);

    }

}
