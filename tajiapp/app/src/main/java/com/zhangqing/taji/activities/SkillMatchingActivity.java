package com.zhangqing.taji.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;

/**
 * Created by zhangqing on 2016/4/13.
 * 技能匹配界面Activity
 */
public class SkillMatchingActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_skill_matching);

    }

    public void finishThis(View v) {
        finish();
    }
}