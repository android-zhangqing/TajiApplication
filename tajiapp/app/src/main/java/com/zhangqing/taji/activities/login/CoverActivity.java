package com.zhangqing.taji.activities.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.activities.TajiappActivity;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;

import org.json.JSONObject;

/**
 * 启动封面Activity
 */
public class CoverActivity extends AppCompatActivity {
    private static final int DELAY_MILLIS = 1000;

    private long mStartTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStartTimeMillis = System.currentTimeMillis();


        UserClass.getInstance().getMyUserInfo(new VolleyInterface(this.getApplicationContext()) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {
                if (!UserClass.getInstance().saveSharedPreference(jsonObject)) {
                    UserClass.getInstance().clear();
                    waitToStartActivity(true, null);
                    return;
                }
                waitToStartActivity(false, null);
            }

            @Override
            public void onMyError(VolleyError error) {
                waitToStartActivity(false, null);
            }
        });

//        UserClass.getInstance().dongTaiGetList(1, new VolleyInterface(getApplicationContext()) {
//            @Override
//            public void onMySuccess(JSONObject jsonObject) {
//                Log.e("sss", jsonObject.toString());
//            }
//
//            @Override
//            public void onMyError(VolleyError error) {
//
//            }
//        });
    }

    private void waitToStartActivity(final boolean isToLogin, final Bundle bundle) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mStartTimeMillis >= DELAY_MILLIS) {
            Intent intent;
            if (isToLogin) {
                intent = new Intent(CoverActivity.this,
                        LoginActivity.class);
            } else {
                intent = new Intent(CoverActivity.this,
                        TajiappActivity.class);
            }

            if (bundle != null) {
                intent.putExtras(bundle);
            }
            startActivity(intent);
            finish();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    if (isToLogin) {
                        intent = new Intent(CoverActivity.this,
                                LoginActivity.class);
                    } else {
                        intent = new Intent(CoverActivity.this,
                                TajiappActivity.class);
                    }

                    if (bundle != null) {
                        intent.putExtras(bundle);
                    }
                    startActivity(intent);
                    finish();
                }
            }, DELAY_MILLIS - (currentTime - mStartTimeMillis));
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }
}
