package com.zhangqing.taji.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;

/**
 * Created by zhangqing on 2016/4/26.
 * 我的个人信息设置界面
 */
public class ModifyPersonInfoActivity extends BaseActivity {
    private TextView tv_name;
    private TextView tv_sex;
    private TextView tv_sign;
    private TextView tv_school;

    private ImageView iv_avatar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_person_info);

        tv_name = (TextView) findViewById(R.id.modify_person_name);
        tv_sex = (TextView) findViewById(R.id.modify_person_sex);
        tv_sign = (TextView) findViewById(R.id.modify_person_sign);
        tv_school = (TextView) findViewById(R.id.modify_person_school);

        iv_avatar = (ImageView) findViewById(R.id.modify_person_avatar);

        tv_name.setText(UserClass.getInstance().getStringByKey("username"));
        tv_sex.setText(UserClass.getInstance().getStringByKey("sex"));
        tv_sign.setText(UserClass.getInstance().getStringByKey("signature"));
        tv_school.setText(UserClass.getInstance().getStringByKey("school"));

        ImageLoader.getInstance().displayImage(UserClass.getInstance().getStringByKey("avatar"), new ImageViewAware(iv_avatar), MyApplication.getCircleDisplayImageOptions());

    }

    public void finishThis(View v) {
        finish();
    }
}
