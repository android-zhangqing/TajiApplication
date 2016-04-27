package com.zhangqing.taji.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;

/**
 * Created by zhangqing on 2016/4/26.
 * 我的个人信息设置界面
 */
public class ModifyPersonInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_person_info);
    }

    public void finishThis(View v) {
        finish();
    }
}
