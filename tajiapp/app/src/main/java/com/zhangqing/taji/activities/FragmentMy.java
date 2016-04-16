package com.zhangqing.taji.activities;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhangqing.taji.BaseFragment;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.activities.login.LoginActivity;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.util.LocationUtil;

import org.json.JSONObject;


/**
 * 首页底部栏【我的】
 */
public class FragmentMy extends BaseFragment implements View.OnClickListener {


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

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my, container, false);

        mNameTextView = (TextView) v.findViewById(R.id.my_head_name);
        mSchoolTextView = (TextView) v.findViewById(R.id.my_head_school);
        mSignTextView = (TextView) v.findViewById(R.id.my_head_sign);
        mAvatarImageView = (ImageView) v.findViewById(R.id.my_head_face);
        mSexImageView = (ImageView) v.findViewById(R.id.my_head_sex);

        mCountDongtai = (TextView) v.findViewById(R.id.my_count_dongtai);
        mCountTudi = (TextView) v.findViewById(R.id.my_count_tudi);
        mCountFans = (TextView) v.findViewById(R.id.my_count_fans);
        mCountFollow = (TextView) v.findViewById(R.id.my_count_follow);

        mContainerInterest = (LinearLayout) v.findViewById(R.id.my_container_interest);
        mContainerSkill = (LinearLayout) v.findViewById(R.id.my_container_skill);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.my_swipe_refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UserClass.getInstance().getMyUserInfo(new VolleyInterface(getActivity().getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (!UserClass.getInstance().saveSharedPreference(jsonObject)) {
                            UserClass.getInstance().clear();
                            Intent intent = new Intent(getActivity(),
                                    LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                            return;
                        }
                        initView();
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                });

            }
        });

        ((LinearLayout) (v.findViewById(R.id.my_click_tudi))).setOnClickListener(this);
        ((LinearLayout) (v.findViewById(R.id.my_click_shifu))).setOnClickListener(this);
        ((LinearLayout) (v.findViewById(R.id.my_click_beidingyue))).setOnClickListener(this);
        ((LinearLayout) (v.findViewById(R.id.my_click_dingyue))).setOnClickListener(this);
        ((LinearLayout) (v.findViewById(R.id.my_click_modify_intadskill))).setOnClickListener(this);
        (v.findViewById(R.id.my_click_modify_member_detail)).setOnClickListener(this);


        v.post(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        });

        return v;
    }

    private void initView() {
        String name = UserClass.getInstance().getStringByKey("username");
        String signature = UserClass.getInstance().getStringByKey("signature");

        mCountDongtai.setText(UserClass.getInstance().getStringByKey("dongtai"));
        mCountTudi.setText(UserClass.getInstance().getStringByKey("tudi"));
        mCountFans.setText(UserClass.getInstance().getStringByKey("fans"));
        mCountFollow.setText(UserClass.getInstance().getStringByKey("follow"));

        if (name.equals("") || name.equals("null")) {
            mNameTextView.setText("游客" + UserClass.getInstance().userId);
        } else {
            mNameTextView.setText(name);
        }
        mSignTextView.setText(signature);


        String interest = UserClass.getInstance().getStringByKey("interest");

        addViewToContainer(interest, mContainerInterest, R.drawable.my_interst_lable_bg);


        String skill = UserClass.getInstance().getStringByKey("skill");

        addViewToContainer(skill, mContainerSkill, R.drawable.my_skill_lable_bg);


        String avatar = UserClass.getInstance().getStringByKey("avatar");
        if (!avatar.equals("")) {
            //Log.e("loadingavatar", "" + avatar);
            ImageLoader.getInstance().displayImage(avatar, mAvatarImageView, MyApplication.getCircleDisplayImageOptions());
        }

        String sex = UserClass.getInstance().getStringByKey("sex");
        if (sex.equals("女")) {
            mSexImageView.setImageResource(R.drawable.ic_launcher);
        } else {
            mSexImageView.setImageResource(R.drawable.icon_tab_my_head_male);
        }

        String school = UserClass.getInstance().getStringByKey("school");
        if (school.equals("") || school.equals("null")) {
            LocationUtil.getLocation(getActivity(), new LocationUtil.OnLocatedListener() {
                @Override
                public void onLocatedSuccess(String location) {
                    mSchoolTextView.setText(location + "(点击设置)");
                }
            });
        } else {
            mSchoolTextView.setText(school);
        }
    }

    private static final int MARGIN_LABLE = 15;
    private static final int MARGIN_LABLE_Top = 15;
    private static final int PADDING_LABLE = 22;

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
            TextView tv = new TextView(getActivity());
            tv.setText("戳我立即添加标签");
            layout.addView(tv, params);
            return;
        }

        String[] strings = stringWaitToSplit.split("\\.");

        layout.setGravity(Gravity.CENTER_VERTICAL);
        Log.e("addViewToContainer", stringWaitToSplit + "|" + layout.getWidth() + "|" + layout.toString());

        int restWidth = layout.getWidth();
        LinearLayout layoutSubContainer = new LinearLayout(getActivity());
        layout.addView(layoutSubContainer, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (String i : strings) {

            TextView tv = new TextView(getActivity());
            tv.setText(i);
            tv.setBackgroundResource(backgroundResource);
            tv.setPadding(PADDING_LABLE, 12, PADDING_LABLE, 12);
            tv.setTextColor(Color.parseColor("#CDCDCD"));
            tv.setTextSize(13);
            tv.setGravity(Gravity.CENTER);
            tv.setSingleLine(true);

            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            tv.measure(w, h);

            int costWidth = tv.getMeasuredWidth() + MARGIN_LABLE * 2;
            restWidth -= costWidth;
            //Log.e("measureResult", "|" + costWidth + "|" + restWidth);

            if (restWidth <= 0) {
                //改变前一个标签的宽度，使之两端对齐
                TextView lastTv = (TextView) layoutSubContainer.getChildAt(layoutSubContainer.getChildCount() - 1);
                int lastTvWidth = lastTv.getMeasuredWidth() + costWidth + restWidth - 2 * MARGIN_LABLE;
                //Log.e("lastTvWidth", lastTvWidth + "|");

                LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(lastTvWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                tempParams.setMargins(MARGIN_LABLE, MARGIN_LABLE_Top, MARGIN_LABLE, MARGIN_LABLE_Top);
                lastTv.setLayoutParams(tempParams);

                restWidth = layout.getWidth() - costWidth;
                layoutSubContainer = new LinearLayout(getActivity());
                layout.addView(layoutSubContainer, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }


            layoutSubContainer.addView(tv, params);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_click_tudi:
                startListPersonsActivity(UserClass.Persons_Button_tudi, "徒弟");
                break;
            case R.id.my_click_beidingyue:
                startListPersonsActivity(UserClass.Persons_Button_beidingyue, "被订阅");
                break;
            case R.id.my_click_dingyue:
                startListPersonsActivity(UserClass.Persons_Button_dingyue, "订阅");
                break;
            case R.id.my_click_shifu:
                startListPersonsActivity(UserClass.Persons_Button_shifu, "我的师傅");
                break;
            case R.id.my_click_modify_intadskill:
                Intent intent = new Intent(getActivity(), SkillSettingActivity.class);
                startActivityForResult(intent, UserClass.Request_Setting_Lable);
                break;
            case R.id.my_click_modify_member_detail:
                Intent intent2 = new Intent(getActivity(), ModifyInterestSkillActivity.class);
                startActivity(intent2);
                break;

        }

    }

    private void startListPersonsActivity(int which, String titleString) {
        Intent intent = new Intent(getActivity(), ListPersonsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", titleString);
        bundle.putInt("which", which);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        if (LocationUtil.mlocationClient != null) {
            LocationUtil.mlocationClient.stopLocation();//停止定位
            LocationUtil.mlocationClient.onDestroy();
        }
        super.onPause();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != UserClass.Request_Setting_Lable) return;
        if (resultCode == Activity.RESULT_OK) {
            swipeRefreshLayout.setRefreshing(true);
            UserClass.getInstance().setStringByKey("is_to_insert", "1");
        }
    }
}
