package com.zhangqing.taji.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseFragment;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.database.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * Created by Administrator on 2016/2/22.
 * 消息板块
 */
public class FragmentMessage extends BaseFragment implements View.OnClickListener {
    ConversationListFragment fragment;
    Uri uri;


    @Override
    public void onResume() {
        super.onResume();

        ((TajiappActivity) getActivity()).bottomBar.
                setPoints(2, RongIM.getInstance().getRongIMClient().getTotalUnreadCount() != 0);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_message, container, false);


        initFragment();
        initListener(v);


        onHiddenChanged(false);
        return v;
    }

    private void checkMatching() {
        if (UserClass.getInstance().getStringByKey("is_to_insert").equals("1")) {
            UserClass.getInstance().setStringByKey("is_to_insert", "0");
            insertMessage("1010", "又有人跟你匹配了呦！");
        }
    }

    private void checkBaiShiInvite() {
        UserClass.getInstance().shiTuGetBaiShiList(1, new VolleyInterface(getActivity().getApplicationContext()) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    String name_show = "";
                    switch (jsonArray.length()) {
                        case 0:
                            return;
                        case 1:
                            name_show = jsonArray.getJSONObject(0).optString("username", "神秘人");
                            break;
                        default:
                            name_show = jsonArray.getJSONObject(0).optString("username", "神秘人1") + "、" +
                                    jsonArray.getJSONObject(1).optString("username", "神秘人2") + "等人";
                    }
                    insertMessage("1011", name_show + "对你发出拜师邀请");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        log("onHiddenChanged", hidden + "");
        if (!hidden) {
            checkMatching();
            checkBaiShiInvite();
        }
    }

    private void initListener(View v) {

        //监听三个按钮的点击
        v.findViewById(R.id.message_btn_at).setOnClickListener(this);
        v.findViewById(R.id.message_btn_comment).setOnClickListener(this);
        v.findViewById(R.id.message_btn_favor).setOnClickListener(this);


        RongIM.getInstance().setSendMessageListener(new RongIM.OnSendMessageListener() {
            @Override
            public Message onSend(Message message) {
                log("setSendMessageListener", message.getTargetId() + "|");
                return message;
            }

            @Override
            public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
                log("onSent", message.getTargetId() + "|");
                return false;
            }
        });


        //set Listener
        RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                log("****onReceived****", i + "|" + new String(message.getContent().encode()) + "|" + message.getConversationType().getName() + "|" + message.getReceivedStatus().isRead());
                return false;
            }
        });

        RongIM.setConversationListBehaviorListener(new RongIM.ConversationListBehaviorListener() {

            @Override
            public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
                if (conversationType == Conversation.ConversationType.SYSTEM) {
                    clickSystemMessage(s);
                    return true;
                }
                OthersDetailActivity.start(getActivity(), s, DatabaseManager.getInstance().queryUserInfoById(s).getName());
                return true;
            }

            @Override
            public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
                return false;
            }

            @Override
            public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
                return false;
            }

            @Override
            public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
                log("ConversationListBeh", uiConversation.getConversationSenderId() + "|" + uiConversation.getConversationTargetId() + "|" + RongIM.getInstance().getRongIMClient().getTotalUnreadCount() + "");
                if (uiConversation.getConversationType() == Conversation.ConversationType.SYSTEM) {
                    clickSystemMessage(uiConversation.getConversationTargetId());
                    return true;
                }
                return false;
            }
        });
    }

    private void clickSystemMessage(String targetId) {
        switch (targetId) {
            case "1010":
                startActivity(new Intent(getActivity(), SkillMatchingActivity.class));
                break;
            case "1011":
                startActivity(new Intent(getActivity(), BaiShiInviteListActivity.class));
                break;
        }
    }


    /**
     * 插入一条虚拟System类型消息
     *
     * @param targetId 头像userid
     * @param content  显示内容
     */
    public void insertMessage(final String targetId, final String content) {
        if (fragment == null) return;

        RongIMClient.getInstance().insertMessage(Conversation.ConversationType.SYSTEM, targetId, UserClass.getInstance().userId, new TextMessage(content), new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(final Message message) {
                RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), Message.SentStatus.SENT, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        fragment.onEventMainThread(new Event.OnReceiveMessageEvent(message, 0));
                        RongIMClient.getInstance().getConversation(Conversation.ConversationType.SYSTEM, targetId, new RongIMClient.ResultCallback<Conversation>() {
                            @Override
                            public void onSuccess(Conversation conversation) {
                                Log.e("getConversation", "onSuccess");
                                conversation.setUnreadMessageCount(2);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Log.e("getConversation", "onError");

                            }
                        });
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        Log.e("insertMessage", "              setMessageSentStatus ERROR");
                    }
                });

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e("insertMessage", "              insertMessage ERROR");
            }
        });

    }

    private void initFragment() {
        fragment = (ConversationListFragment) getChildFragmentManager().findFragmentById(R.id.conversationlist);
        uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();
        fragment.setUri(uri);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_btn_at: {
                break;
            }
            case R.id.message_btn_comment: {
                break;
            }
            case R.id.message_btn_favor: {
                break;
            }
        }
    }
}
