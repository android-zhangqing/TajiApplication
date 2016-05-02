package com.zhangqing.taji.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;

/**
 * Created by zhangqing on 2016/5/2.
 * 用于输入的新activity
 */
public class EditActivity extends BaseActivity {
    public static final int REQUEST_ADD_SKILL_LABEL = 1;

    private EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mEditText = (EditText) findViewById(R.id.edit_edit);
        ((TextView) findViewById(R.id.edit_title)).setText(getIntent().getStringExtra("title"));

    }

    public void finishThis(View v) {
        finish();
    }

    public void onClickCommit(View v) {
        String result = mEditText.getText().toString();
        if (result.equals("")) {
            Toast.makeText(getApplicationContext(), "写点什么吧~", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = getIntent();
        intent.putExtra("data", result);
        setResult(RESULT_OK, intent);
        finish();
    }
}
