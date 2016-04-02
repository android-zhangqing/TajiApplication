package com.zhangqing.taji.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;

import com.zhangqing.taji.R;

public class SkillSettingActivity extends AppCompatActivity {
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_setting);
        mGridView = (GridView) findViewById(R.id.skill_setting_gridview);

        mGridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Log.e("SkillSetting", mGridView.getMeasuredWidth() +
                        "|" + mGridView.getWidth() + "|" + mGridView.getMeasuredWidthAndState());
                mGridView.setColumnWidth(mGridView.getWidth() / 4);

            }
        });


    }
}
