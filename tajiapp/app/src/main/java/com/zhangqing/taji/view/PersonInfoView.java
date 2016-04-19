package com.zhangqing.taji.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.bean.PersonInfoBean;
import com.zhangqing.taji.base.UserClass;

/**
 * Created by zhangqing on 2016/4/9.
 */
public class PersonInfoView extends LinearLayout {
    PersonInfoBean mPersonInfo;
    ViewGroup mMainContainer;

    private TextView mNameTextView;
    private TextView mSignTextView;
    private TextView mSchoolTextView;
    private ImageView mAvatarImageView;
    private ImageView mSexImageView;

    private TextView mCountDongtai;
    private TextView mCountTudi;
    private TextView mCountFans;
    private TextView mCountFollow;

    private LinearLayout mContainerInterest;
    private LinearLayout mContainerSkill;

    private TextView mFollowButton;
    private ImageView mIsMaster;

    public PersonInfoView(Context context) {
        super(context);
        initView();
    }

    public PersonInfoView(Context context, PersonInfoBean personInfo) {
        super(context);
        this.mPersonInfo = personInfo;
        initView();
        updateView();
    }


    public PersonInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PersonInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mMainContainer = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.view_others_info, this, false);

        mNameTextView = (TextView) mMainContainer.findViewById(R.id.my_head_name);
        mSchoolTextView = (TextView) mMainContainer.findViewById(R.id.my_head_school);
        mSignTextView = (TextView) mMainContainer.findViewById(R.id.my_head_sign);
        mAvatarImageView = (ImageView) mMainContainer.findViewById(R.id.my_head_face);
        mSexImageView = (ImageView) mMainContainer.findViewById(R.id.my_head_sex);

        mCountDongtai = (TextView) mMainContainer.findViewById(R.id.my_count_dongtai);
        mCountTudi = (TextView) mMainContainer.findViewById(R.id.my_count_tudi);
        mCountFans = (TextView) mMainContainer.findViewById(R.id.my_count_fans);
        mCountFollow = (TextView) mMainContainer.findViewById(R.id.my_count_follow);

        mContainerInterest = (LinearLayout) mMainContainer.findViewById(R.id.my_container_interest);
        mContainerSkill = (LinearLayout) mMainContainer.findViewById(R.id.my_container_skill);

        mFollowButton = (TextView) mMainContainer.findViewById(R.id.my_follow_button);
        mIsMaster = (ImageView) mMainContainer.findViewById(R.id.my_head_ismaster);


        addView(mMainContainer);
    }

    private void updateView() {
        if (mPersonInfo == null) return;

        String name = mPersonInfo.username;
        if (name.equals("") || name.equals("null")) {
            mNameTextView.setText("游客" + mPersonInfo.userid);
        } else {
            mNameTextView.setText(name);
        }

        String avatar = mPersonInfo.avatar;
        if (!avatar.equals("")) {
            Log.e("loadingavatar", "" + avatar);
            ImageLoader.getInstance().displayImage(avatar,new ImageViewAware(mAvatarImageView), MyApplication.getCircleDisplayImageOptions());
        }

        mSignTextView.setText(mPersonInfo.signature);

        mCountDongtai.setText(mPersonInfo.dongtai);
        mCountTudi.setText(mPersonInfo.tudi);
        mCountFans.setText(mPersonInfo.fans);
        mCountFollow.setText(mPersonInfo.follow);

        String interest = mPersonInfo.interest;

        addViewToContainer(interest, mContainerInterest, R.drawable.my_interst_lable_bg);


        String skill = mPersonInfo.skill;

        addViewToContainer(skill, mContainerSkill, R.drawable.my_skill_lable_bg);

        String sex = mPersonInfo.sex;
        if (sex.equals("女")) {
            mSexImageView.setImageResource(R.drawable.ic_launcher);
        } else {
            mSexImageView.setImageResource(R.drawable.icon_tab_my_head_male);
        }

        mSchoolTextView.setText(mPersonInfo.school);

        /**
         * 师傅关系的话，就显示出图标【师】
         */
        if (mPersonInfo.is_master) mIsMaster.setVisibility(View.VISIBLE);

        /**
         * 处理查看的个人主页是自己的主页的情况
         */
        Log.e("mPersonInfo", mPersonInfo.userid + "|");
        if (mPersonInfo.userid.equals(UserClass.getInstance().userId)) {
            mFollowButton.setVisibility(View.GONE);
        } else {
            mFollowButton.setText(mPersonInfo.is_follow ? "√ 已订阅" : "+ 订阅");
            mFollowButton.setTextColor(mPersonInfo.is_follow ? Color.parseColor("#9F61AA") : Color.parseColor("#16FBCC"));
            mFollowButton.setBackgroundResource(mPersonInfo.is_follow ? R.drawable.home_hot_btn_concern_bg_reverse : R.drawable.home_hot_btn_concern_bg);
        }
        
    }

    private static final int MARGIN_LABLE = 10;
    private static final int MARGIN_LABLE_Top = 0;
    private static final int PADDING_LABLE = 15;

    /**
     * 将技能兴趣标签加入view，自动换行，两端对齐
     *
     * @param stringWaitToSplit  多个标签点号分隔
     * @param layout             父容器
     * @param backgroundResource 标签背景色
     */
    private void addViewToContainer(String stringWaitToSplit, LinearLayout layout, int backgroundResource) {
        layout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(MARGIN_LABLE, MARGIN_LABLE_Top, MARGIN_LABLE, MARGIN_LABLE_Top);

        if (stringWaitToSplit.equals("") || stringWaitToSplit.equals("null")) {
            TextView tv = new TextView(getContext());
            tv.setText("标签未设置");
            layout.addView(tv, params);
            return;
        }

        String[] strings = stringWaitToSplit.split("\\.");

        layout.setGravity(Gravity.CENTER_VERTICAL);
        Log.e("addViewToContainer", stringWaitToSplit + "|" + layout.getWidth() + "|" + layout.toString());
        for (String i : strings) {

            if (i.equals("")) continue;
            TextView tv = new TextView(getContext());
            tv.setText(i);
            tv.setBackgroundResource(backgroundResource);
            tv.setPadding(PADDING_LABLE, 12, PADDING_LABLE, 12);
            tv.setTextColor(Color.parseColor("#CDCDCD"));
            tv.setTextSize(13);
            tv.setGravity(Gravity.CENTER);
            tv.setSingleLine(true);

            layout.addView(tv, params);
        }
    }

    public void setPersonInfo(PersonInfoBean personInfo) {
        mPersonInfo = personInfo;

    }


}
