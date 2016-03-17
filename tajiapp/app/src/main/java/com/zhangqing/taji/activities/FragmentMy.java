package com.zhangqing.taji.activities;


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
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.activities.login.LoginActivity;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;

import org.json.JSONObject;


/**
 * Created by Administrator on 2016/2/14.
 */
public class FragmentMy extends Fragment implements View.OnClickListener {
    //声明AMapLocationClient类对象
    public AMapLocationClient mlocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

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
                MyApplication.getUser().getUserInfo(new VolleyInterface(getActivity().getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (!MyApplication.getUser().saveSharedPreference(jsonObject)) {
                            MyApplication.getUser().clear();
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
        String name = MyApplication.getUser().getStringByKey("username");
        String signature = MyApplication.getUser().getStringByKey("signature");

        mCountDongtai.setText(MyApplication.getUser().getStringByKey("dongtai"));
        mCountTudi.setText(MyApplication.getUser().getStringByKey("tudi"));
        mCountFans.setText(MyApplication.getUser().getStringByKey("fans"));
        mCountFollow.setText(MyApplication.getUser().getStringByKey("follow"));

        if (name.equals("") || name.equals("null")) {
            mNameTextView.setText("游客" + MyApplication.getUser().userId);
        } else {
            mNameTextView.setText(name);
        }
        mSignTextView.setText(signature);


        String interest = MyApplication.getUser().getStringByKey("interest");
        if (!interest.equals("")) {
            addViewToContainer(interest, mContainerInterest, R.drawable.my_interst_lable_bg);
        }

        String skill = MyApplication.getUser().getStringByKey("skill");
        if (!skill.equals("")) {
            addViewToContainer(skill, mContainerSkill, R.drawable.my_skill_lable_bg);
        }

        String avatar = MyApplication.getUser().getStringByKey("avatar");
        if (!avatar.equals("")) {
            Log.e("loadingavatar", "" + avatar);
            ImageLoader.getInstance().displayImage(avatar, mAvatarImageView, MyApplication.getDisplayImageOptions());
        }

        String sex = MyApplication.getUser().getStringByKey("sex");
        if (sex.equals("女")) {
            mSexImageView.setImageResource(R.drawable.ic_launcher);
        } else {
            mSexImageView.setImageResource(R.drawable.icon_tab_my_head_male);
        }

        String school = MyApplication.getUser().getStringByKey("school");
        if (school.equals("") || school.equals("null")) {
            getLocation();
        } else {
            mSchoolTextView.setText(school);
        }
    }

    private void addViewToContainer(String stringWaitToSplit, LinearLayout layout, int backgroundResource) {
        if (stringWaitToSplit.equals("") || stringWaitToSplit.equals("null")) return;

        String[] strings = stringWaitToSplit.split("\\.");

        layout.removeAllViews();
        layout.setGravity(Gravity.CENTER_VERTICAL);
        Log.e("addViewToContainer", stringWaitToSplit + "|" + layout.toString());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(12, 0, 12, 0);
        for (String i : strings) {
            TextView tv = new TextView(getActivity());
            tv.setText(i);
            tv.setBackgroundResource(backgroundResource);
            tv.setPadding(22, 3, 22, 3);
            tv.setTextColor(Color.parseColor("#CDCDCD"));
            tv.setTextSize(13);
            layout.addView(tv, params);
        }
    }

    private void getLocation() {
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        amapLocation.getLatitude();//获取纬度
                        amapLocation.getLongitude();//获取经度
                        Log.e("amapLocation", amapLocation.getLatitude() + "|" + amapLocation.getLongitude());
                        //Toast.makeText(getActivity(), amapLocation.getLatitude() + "|" + amapLocation.getLongitude() + "|" + amapLocation.getCity(), Toast.LENGTH_LONG).show();

                        if (MyApplication.getUser().getSchoolByLocation(getActivity(),
                                amapLocation.getLatitude(), amapLocation.getLongitude(), amapLocation.getCity())) {
                            mSchoolTextView.setText(MyApplication.getUser().schoolName + "(点击设置)");
                        }
                        ;

//                        amapLocation.getAccuracy();//获取精度信息
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        Date date = new Date(amapLocation.getTime());
//                        df.format(date);//定位时间
//                        amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                        amapLocation.getCountry();//国家信息
//                        amapLocation.getProvince();//省信息
//                        amapLocation.getCity();//城市信息
//                        amapLocation.getDistrict();//城区信息
//                        amapLocation.getStreet();//街道信息
//                        amapLocation.getStreetNum();//街道门牌号信息
//                        amapLocation.getCityCode();//城市编码
//                        amapLocation.getAdCode();//地区编码
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                        Toast.makeText(getActivity(), "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo(), Toast.LENGTH_LONG).show();

                    }
                }
            }
        };

//初始化定位
        mlocationClient = new AMapLocationClient(getActivity().getApplicationContext());
//设置定位回调监听
        mlocationClient.setLocationListener(mLocationListener);


//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
//设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
//设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
//设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
//给定位客户端对象设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
//启动定位
        mlocationClient.startLocation();


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
                Intent intent = new Intent(getActivity(), ModifyInterestSkillActivity.class);
                startActivity(intent);
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
        if (mlocationClient != null) {
            mlocationClient.stopLocation();//停止定位
            mlocationClient.onDestroy();
        }
        super.onPause();

    }


}