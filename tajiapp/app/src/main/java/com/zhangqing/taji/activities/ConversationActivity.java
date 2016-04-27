package com.zhangqing.taji.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
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
import io.rong.imlib.NativeObject;
import io.rong.imlib.model.Conversation;

/**
 * Created by Administrator on 2016/2/23.
 * 融云私聊对话框Activity
 */
public class ConversationActivity extends BaseActivity {
    private static final String CIRCLE_ADD = "收藏圈子";
    private static final String CIRCLE_DEL = "取消收藏";

    private String mTargetId;

    //刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
    private String mTargetIds;

    private Conversation.ConversationType mConversationType;

    private String title;


    /**
     * 圈子(聊天室)相关
     */
    private TextView mAddMyRoomTextView;
    private ChatRoomBean mChatRoomBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        mAddMyRoomTextView = (TextView) findViewById(R.id.conversation_add_myroom);

        getIntentDate(getIntent());


        if (mConversationType != Conversation.ConversationType.CHATROOM) {
            mAddMyRoomTextView.setVisibility(View.GONE);
        } else {
            mChatRoomBean = ChatRoomBean.getInstance(mTargetId);
            mAddMyRoomTextView.setText(mChatRoomBean.is_mine ? CIRCLE_DEL : CIRCLE_ADD);
            mAddMyRoomTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAddMyRoomTextView.setEnabled(false);
                    UserClass.getInstance().chatRoomAddDelMyRoom(mTargetId, !mChatRoomBean.is_mine, new VolleyInterface(getApplicationContext()) {
                        @Override
                        public void onMySuccess(JSONObject jsonObject) {
                            mAddMyRoomTextView.setEnabled(true);
                            mChatRoomBean.is_mine = !mChatRoomBean.is_mine;
                            mAddMyRoomTextView.setText(mChatRoomBean.is_mine ? CIRCLE_DEL : CIRCLE_ADD);
                            Toast.makeText(getApplicationContext(),
                                    jsonObject.optString("msg", (mChatRoomBean.is_mine ? "收藏成功" : "取消成功")),
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onMyError(VolleyError error) {
                            mAddMyRoomTextView.setEnabled(true);
                        }
                    });
                }
            });
        }

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