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
        super.onCreateView(inflater, container, savedInstanceState);
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

    private void initListener() {

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
                log("ConversationListBeh", uiConversation.getConversationSenderId() + "|" + uiConversation.getConversationTargetId() + "|" + RongIM.getInstance().getRongIMClient().getTotalUnreadCount() + "");
                if (uiConversation.getConversationType() == Conversation.ConversationType.SYSTEM) {
                    switch (uiConversation.getConversationTargetId()) {
                        case "1010":
                            uiConversation.setUnReadMessageCount(0);
                            startActivity(new Intent(getActivity(), SkillMatchingActivity.class));
                            break;
                        case "1011":
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
                log("ll_at");
                insertMessage("1010", "又有人跟你匹配了呦！");
            }
        });




    }


    /**
     * 插入一条虚拟System类型消息
     *
     * @param targetId 头像userid
     * @param content  显示内容
     */
    private void insertMessage(final String targetId, final String content) {

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
                                Log.e("getConversation","onSuccess");
                                conversation.setUnreadMessageCount(2);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Log.e("getConversation","onError");

                            }
                        });
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        log("              setMessageSentStatus ERROR");
                    }
                });

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                log("              insertMessage ERROR");
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


}
