package com.zhangqing.taji.activities;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.session.model.User;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.listener.AvatarClickListener;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.bean.ChatRoomBean;
import com.zhangqing.taji.bean.PersonInfoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/30.
 * 圈子（聊天室）详情界面
 */
public class ChatRoomDetailActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mPersonGridView;

    private TextView mRoomName;
    private TextView mRoomDesc;
    private SwitchCompat mRoomIsMine;

    private int mContainerWidth;
    private int mContainerHeight;

    private ChatRoomBean mChatRoom;

    private void initView() {
        mPersonGridView = (LinearLayout) findViewById(R.id.chatroom_detail_label_container);
        mRoomName = (TextView) findViewById(R.id.chatroom_detail_name_tv);
        mRoomDesc = (TextView) findViewById(R.id.chatroom_detail_desc_tv);
        mRoomIsMine = (SwitchCompat) findViewById(R.id.chatroom_detail_switch);
        ((TextView) findViewById(R.id.chatroom_detail_title)).setText(
                mChatRoom.number.equals("") ? "圈子" : "圈子(" + mChatRoom.number + ")");
        mRoomIsMine.setThumbResource(R.drawable.icon_point);
        mRoomIsMine.setTrackResource(
                mChatRoom.is_mine ? R.drawable.icon_track_select : R.drawable.icon_track_unselect);
        mRoomIsMine.setChecked(mChatRoom.is_mine);
        mRoomIsMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("mRoomIsMine", "onClick|" + mRoomIsMine.isChecked());

                mRoomIsMine.setTrackResource(
                        mRoomIsMine.isChecked() ? R.drawable.icon_track_select : R.drawable.icon_track_unselect);
                UserClass.getInstance().chatRoomAddDelMyRoom(mChatRoom.rid, mRoomIsMine.isChecked(), new VolleyInterface(getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        mChatRoom.is_mine = mRoomIsMine.isChecked();
                        Toast.makeText(getApplicationContext(), jsonObject.optString(
                                "msg", mChatRoom.is_mine ? "收藏成功" : "取消成功"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        mRoomIsMine.setChecked(!mRoomIsMine.isChecked());
                    }
                });

            }
        });

        mRoomName.setText(mChatRoom.name);
        mRoomDesc.setText(mChatRoom.description);

        findViewById(R.id.chatroom_detail_desc).setOnClickListener(this);
        findViewById(R.id.chatroom_detail_name).setOnClickListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_detail);

        String rid = getIntent().getStringExtra("rid");
        if (rid == null) finish();

        mChatRoom = ChatRoomBean.getInstance(rid);

        initView();

        mPersonGridView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public synchronized void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mPersonGridView.removeOnLayoutChangeListener(this);
                Log.e("mPersonGridView", "onLayoutChange|" + left + "|" + top + "|" + right + "|" + bottom);
                mContainerWidth = right - left;
                mContainerHeight = bottom - top;
                UserClass.getInstance().getPersonsList(UserClass.Persons_Button_Chatroom, mChatRoom.rid, -1, new VolleyInterface(getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        initPersonGridView(jsonObject);
                    }

                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });
            }
        });
    }


    private void initPersonGridView(JSONObject jsonObject) {

        JSONArray jsonArray = jsonObject.optJSONArray("data");
        if (jsonArray == null) return;

        //已插入的person数
        int count_insert = 0;

        //一个insideContainer放四个person的view
        LinearLayout insideContainer = null;

        //生成lp
        LinearLayout.LayoutParams lp_inside_container = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContainerHeight / 4);
        LinearLayout.LayoutParams lp_item = new LinearLayout.LayoutParams(mContainerWidth / 4, ViewGroup.LayoutParams.MATCH_PARENT);

        //生成cornerOptions
        DisplayImageOptions cornerOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(0) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon_tab_my_head_male) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_launcher) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(8, 0)) // 设置成圆角图片
                .build(); // 构建完成

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                PersonInfoBean personInfoBean = PersonInfoBean.getInstance(jsonArray.getJSONObject(i));

                if (count_insert % 4 == 0) {
                    insideContainer = new LinearLayout(this);
                    insideContainer.setOrientation(LinearLayout.HORIZONTAL);
                    mPersonGridView.addView(insideContainer, lp_inside_container);
                }

                {
                    LinearLayout itemView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_chatroom_detail_item, null);
                    ImageView imageView = (ImageView) itemView.findViewById(R.id.chatroom_detail_item_avatar);
                    imageView.setOnClickListener(new AvatarClickListener(this, personInfoBean.userid, personInfoBean.username));
                    ImageLoader.getInstance().displayImage(personInfoBean.avatar,
                            new ImageViewAware(imageView), cornerOptions);
                    ((TextView) itemView.findViewById(R.id.chatroom_detail_item_name)).setText(personInfoBean.username);

                    insideContainer.addView(itemView, lp_item);
                }
                count_insert++;

                //处理最后一个item查看更多
                if (count_insert == 15) {
                    LinearLayout itemView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_chatroom_detail_item, null);
                    ImageView imageView = (ImageView) itemView.findViewById(R.id.chatroom_detail_item_avatar);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ChatRoomDetailActivity.this, ListPersonsActivity.class);
                            intent.putExtra("id", mChatRoom.rid);
                            intent.putExtra("title", "圈子成员");
                            intent.putExtra("which", UserClass.Persons_Button_Chatroom);
                            startActivity(intent);
                        }
                    });
                    imageView.setImageResource(R.drawable.icon_chatroom_detail_view_more);
                    imageView.setBackgroundResource(R.drawable.chatroom_detail_corner_bg);
                    ((TextView) itemView.findViewById(R.id.chatroom_detail_item_name)).setText("查看更多");

                    insideContainer.addView(itemView, lp_item);
                }
                Log.e("count_insert", count_insert + "|");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void finishThis(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatroom_detail_name: {
                break;
            }
            case R.id.chatroom_detail_desc: {
                break;
            }

        }
    }
}
