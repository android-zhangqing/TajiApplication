package com.zhangqing.taji.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.session.model.User;
import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.bean.ChatRoomBean;

import org.json.JSONObject;

import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;

/**
 * Created by Administrator on 2016/2/23.
 * 融云私聊对话框Activity
 */
public class ConversationActivity extends BaseActivity {

    private String mTargetId;

    //刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
    private String mTargetIds;

    private Conversation.ConversationType mConversationType;

    private String title;


    /**
     * 圈子(聊天室)相关
     */
    private ImageView mChatRoomSetting;
    private ChatRoomBean mChatRoomBean;
    //用于[在线人数]逻辑的心跳线程的终止控制
    private boolean mLoopOnline = true;
    private Thread mLoopOnlineThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        mChatRoomSetting = (ImageView) findViewById(R.id.conversation_chatroom_setting);

        getIntentDate(getIntent());

        initListener();

        /**
         * 为聊天室时需要初始化右上角的设置图标
         */
        if (mConversationType != Conversation.ConversationType.CHATROOM) {
            mChatRoomSetting.setVisibility(View.GONE);
        } else {
            mChatRoomBean = ChatRoomBean.getInstance(mTargetId);
            mChatRoomSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ConversationActivity.this, ChatRoomDetailActivity.class);
                    intent.putExtra("rid", mChatRoomBean.rid);
                    startActivity(intent);
                }
            });
        }

    }

    private void initListener() {

        RongIM.setConversationBehaviorListener(new RongIM.ConversationBehaviorListener() {
            @Override
            public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                OthersDetailActivity.start(context, userInfo.getUserId(), userInfo.getName());
                return true;
            }

            @Override
            public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                return false;
            }

            @Override
            public boolean onMessageClick(Context context, View view, Message message) {
                if (message.getContent() instanceof ImageMessage) {
                    ImageMessage imageMessage = (ImageMessage) message.getContent();

                    Uri local_uri = imageMessage.getLocalUri();
                    PhotoViewerActivity.startPhotoView(context,
                            local_uri == null ? imageMessage.getRemoteUri() : local_uri
                            , imageMessage.getThumUri()
                    );
                    return true;
                }
                return false;
            }

            @Override
            public boolean onMessageLinkClick(Context context, String s) {
                return false;
            }

            @Override
            public boolean onMessageLongClick(Context context, View view, Message message) {
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mLoopOnlineThread.interrupt();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoopOnline = true;
        mLoopOnlineThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mLoopOnline) {
                    if (mChatRoomBean != null) {
                        UserClass.getInstance().chatRoomUpdateTimeStamp(mChatRoomBean.rid, new VolleyInterface(getApplicationContext()) {
                            @Override
                            public void onMySuccess(JSONObject jsonObject) {

                            }

                            @Override
                            public void onMyError(VolleyError error) {

                            }
                        });
                    }

                    try {
                        Thread.sleep(1000 * 120);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        mLoopOnline = false;
                    }
                }

            }
        });
        mLoopOnlineThread.start();
    }

    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {

        /**
         * 处理title中可能包含的 表明是已收藏聊天室 的[is_mine]
         */
        title = intent.getData().getQueryParameter("title");
        ((TextView) findViewById(R.id.title)).setText(title);


        //Log.e("RM.getIntentDate", intent.getData().getQueryParameter("title") + "|||" + ((intent.getData() == null) ? "null" : intent.getData().toString() + "|"));
        mTargetId = intent.getData().getQueryParameter("targetId");
        mTargetIds = intent.getData().getQueryParameter("targetIds");
        //intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        enterFragment(mConversationType, mTargetId);
    }

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType 会话类型
     * @param mTargetId         目标 Id
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }

    public void onClickBtnFinish(View v) {
        finish();
    }

    @Override
    public void onDetachedFromWindow() {
        //super.onDetachedFromWindow();
        Log.e("onDetachedFromWindow", "isDetached");
    }
}