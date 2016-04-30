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
        } else {
            chatRoomBean.updateData(jsonObject);
        }
        return chatRoomBean;
    }

    public static ChatRoomBean getInstance(String rid) {
        return mChatRoomMap.get(rid);
    }
    
    public String rid = "";
    public String name = "";
    public String description = "";
    public String avatar = "";
    public String count_all = "";
    public String count_online = "";

    public boolean is_mine = false;

    public String number = "";

    private ChatRoomBean(JSONObject jsonObject) throws JSONException {
        rid = jsonObject.getString("rid");
        updateData(jsonObject);
    }

    private void updateData(JSONObject jsonObject) {

        name = jsonObject.optString("name", name);
        description = jsonObject.optString("description", description);

        String temp_avatar = jsonObject.optString("avatar", avatar);
        if (temp_avatar.contains("http")) avatar = temp_avatar;

        count_all = jsonObject.optString("total", count_all);
        count_online = jsonObject.optString("user", count_online);

        is_mine = jsonObject.optBoolean("is_mine", is_mine);

        number = jsonObject.optString("number", number);
    }

}
