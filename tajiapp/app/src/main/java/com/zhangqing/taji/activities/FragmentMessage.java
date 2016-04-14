package com.zhangqing.taji.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zhangqing.taji.BaseFragment;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;

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
public class FragmentMessage extends BaseFragment {
    ConversationListFragment fragment;
    Uri uri;

    LinearLayout ll_at;

    @Override
    public void onResume() {
        super.onResume();

        ((TajiappActivity) getActivity()).bottomBar.
                setPoints(2, RongIM.getInstance().getRongIMClient().getTotalUnreadCount() != 0);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_message, container, false);

        ll_at = (LinearLayout) v.findViewById(R.id.message_btn_at);

        initFragment();
        initListener();

        return v;
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


        }
    }

    private void initListener(){

        RongIM.getInstance().setSendMessageListener(new RongIM.OnSendMessageListener() {
            @Override
            public Message onSend(Message message) {
                log("setSendMessageListener", message.getTargetId() + "|");
                return null;
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

//        RongIM.setConversationBehaviorListener(new RongIM.ConversationBehaviorListener() {
//            @Override
//            public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
//                return false;
//            }
//
//            @Override
//            public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
//                return false;
//            }
//
//            @Override
//            public boolean onMessageClick(Context context, View view, Message message) {
//                return false;
//            }
//
//            @Override
//            public boolean onMessageLinkClick(Context context, String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onMessageLongClick(Context context, View view, Message message) {
//                return false;
//            }
//        });
//
        RongIM.setConversationListBehaviorListener(new RongIM.ConversationListBehaviorListener() {

            @Override
            public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
                OthersDetailActivity.start(getActivity(), s, "User " + s);
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
                log("ConversationListBeh",uiConversation.getConversationSenderId()+"|"+ RongIM.getInstance().getRongIMClient().getTotalUnreadCount() + "");
                if (uiConversation.getConversationType() == Conversation.ConversationType.SYSTEM) {
                    switch (uiConversation.getConversationSenderId()) {
                        case "1010":
                            startActivity(new Intent(getActivity(), SkillMatchingActivity.class));
                            break;
                    }
                    return true;
                }
                return false;
            }


        });


        ll_at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fragment.getAdapter()
                log("ll_at");
                //RongIM.getInstance().refreshUserInfoCache(new UserInfo("1010","system",Uri.parse("http://taji.whutech.com/uploads/8.jpg")));
//                    Message message = Message.obtain("1010", Conversation.ConversationType.SYSTEM, new TextMessage("test"));
//                    message.setReceivedTime(System.currentTimeMillis());
//                    message.setSentTime(System.currentTimeMillis());
                RongIMClient.getInstance().insertMessage(Conversation.ConversationType.SYSTEM, "1010", "1010", new TextMessage("又有人跟你匹配了呦！"), new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {
                        RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), Message.SentStatus.SENT,null);
                        fragment.onEventMainThread(new Event.OnReceiveMessageEvent(message, 0));

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });

            }
        });
    }

    private void initFragment() {
        log("1");
        fragment = (ConversationListFragment) getChildFragmentManager().findFragmentById(R.id.conversationlist);
        log("2");
        uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();
        log("3" + (fragment == null ? "null" : "n") + "|" + (uri == null ? "a" : "b"));
        fragment.setUri(uri);
        log("4");

        //RongIMClient.getInstance().getConversationList()

    }


}
