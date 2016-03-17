package com.zhangqing.taji.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.VolleyError;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.base.VolleyRequest;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/3/15.
 */
public class ModifyInterestSkillActivity extends Activity {
    //声明AMapLocationClient类对象
    public AMapLocationClient mlocationClient = null;

    private EditText mInterestEditText;
    private EditText mSkillEditText;

    private EditText mNameEditText;
    private EditText mSchoolEditText;
    private EditText mSexEditText;
    private EditText mSignEditText;

    private EditText mCustomOne;
    private TextView mCustomTwo;
    private EditText mCustomThree;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_skill);

        mCustomOne = (EditText) findViewById(R.id.modify_custom_one_et);
        mCustomTwo = (TextView) findViewById(R.id.modify_custom_two_tv);
        mCustomThree = (EditText) findViewById(R.id.modify_custom_three_et);

        mCustomTwo.setText("?userid=" + MyApplication.getUser().userId +
                "&openid=" + MyApplication.getUser().openId + "&");
        mCustomThree.setText("uid=1000");


        mNameEditText = (EditText) findViewById(R.id.modify_name);
        mSchoolEditText = (EditText) findViewById(R.id.modify_school);
        mSexEditText = (EditText) findViewById(R.id.modify_sex);
        mSignEditText = (EditText) findViewById(R.id.modify_signature);

        mInterestEditText = (EditText) findViewById(R.id.modify_interest);
        mSkillEditText = (EditText) findViewById(R.id.modify_skill);

        mInterestEditText.setText(MyApplication.getUser().getStringByKey("interest"));
        mSkillEditText.setText(MyApplication.getUser().getStringByKey("skill"));

        mNameEditText.setText(MyApplication.getUser().getStringByKey("username"));
        mSchoolEditText.setText(MyApplication.getUser().getStringByKey("school"));
        mSexEditText.setText(MyApplication.getUser().getStringByKey("sex"));
        mSignEditText.setText(MyApplication.getUser().getStringByKey("signature"));

    }

    public void OnClickButtonCustomGet(View v) {
        final String url = "http://taji.whutech.com/" +
                mCustomOne.getText().toString() + mCustomTwo.getText().toString() + mCustomThree.getText().toString();
        new AlertDialog.Builder(this).setTitle("即将访问？").setMessage(url).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VolleyRequest.RequestGet(url, "customGet", new VolleyInterface(ModifyInterestSkillActivity.this) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        Toast.makeText(ModifyInterestSkillActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });

            }
        }).setNegativeButton("取消", null).show();
    }

    public void OnClickButtonSubmit(View v) {
        String newInterest = mInterestEditText.getText().toString();
        String newSkill = mSkillEditText.getText().toString();

        MyApplication.getUser().doModifyInterestSkill(newInterest, newSkill, new VolleyInterface(this) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {
                Toast.makeText(ModifyInterestSkillActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });

    }

    public void OnClickButtonSubmitDetail(View v) {
        MyApplication.getUser().doModifyMemberDetail(
                mNameEditText.getText().toString(),
                mSexEditText.getText().toString(),
                mSchoolEditText.getText().toString(),
                mSignEditText.getText().toString(),
                new VolleyInterface(this) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        Toast.makeText(ModifyInterestSkillActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });
    }

    public void onClickBtnLocated(View v) {
        getLocation();
    }

    @Override
    public void onPause() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();//停止定位
            mlocationClient.onDestroy();
        }
        super.onPause();

    }

    private void getLocation() {
        findViewById(R.id.modify_button_located).setEnabled(false);

        //声明定位回调监听器
        AMapLocationListener mLocationListener = null;
        //声明mLocationOption对象
        AMapLocationClientOption mLocationOption = null;

        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    findViewById(R.id.modify_button_located).setEnabled(true);
                    if (amapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        amapLocation.getLatitude();//获取纬度
                        amapLocation.getLongitude();//获取经度
                        Log.e("amapLocation", amapLocation.getLatitude() + "|" + amapLocation.getLongitude());
                        //Toast.makeText(getActivity(), amapLocation.getLatitude() + "|" + amapLocation.getLongitude() + "|" + amapLocation.getCity(), Toast.LENGTH_LONG).show();

                        if (MyApplication.getUser().getSchoolByLocation(ModifyInterestSkillActivity.this.getApplicationContext(),
                                amapLocation.getLatitude(), amapLocation.getLongitude(), amapLocation.getCity())) {
                            mSchoolEditText.setText(MyApplication.getUser().schoolName);
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
                        Toast.makeText(ModifyInterestSkillActivity.this, "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo(), Toast.LENGTH_LONG).show();

                    }
                }
            }
        };

//初始化定位
        mlocationClient = new AMapLocationClient(getApplicationContext());
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

}
