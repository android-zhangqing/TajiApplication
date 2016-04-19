package com.zhangqing.taji;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.zhangqing.taji.activities.SystemStatusManager;

/**
 * Created by zhangqing on 2016/4/12.
 * 自定义的Activity基类
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("**" + this.getClass().getSimpleName() + "**", "onCreate");
        SystemStatusManager.setTranslucentStatus(this);
    }

    public void log(String... log) {
        String result = null;
        for (int i = 0; i < log.length; i++) {
            if (result == null) {
                result = log[i];
            } else {
                result += "|" + log[i];
            }
        }
        Log.e(">>" + this.getClass().getSimpleName() + ">>" +
                Thread.currentThread().getStackTrace()[3].getMethodName(), result);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
