package com.zhangqing.taji.activities.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.activities.TajiappActivity;
import com.zhangqing.taji.base.CustomProgress;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;

import org.json.JSONObject;


public class LoginActivity extends Activity {

    private EditText mMobileEditText;
    private EditText mPasswordEditText;

    private String mMobileString;
    private String mPasswordString;

    private Dialog _dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mMobileEditText = (EditText) findViewById(R.id.login_username);
        mPasswordEditText = (EditText) findViewById(R.id.login_userpass);

    }

    public void onClickBtnRegister(View v) {
        Intent intent = new Intent(this, RegisterFirstActivity.class);
        startActivityForResult(intent, UserClass.Request_Main);
    }

    public void onClickBtnResetPassBySms(View v) {
        Intent intent = new Intent(this, ResetPassBySmsActivity.class);
        startActivity(intent);

    }

    public void onClickBtnLogin(View v) {
        //_dialog=CustomProgress.show(this, "正在登录", true, null);
        mMobileString = mMobileEditText.getText().toString();
        mPasswordString = mPasswordEditText.getText().toString();
        MyApplication.getUser().doLogin(mMobileString, mPasswordString, new VolleyInterface(this) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        Log.e("onMySuccess"," _dialog.dismiss");
//                        _dialog.dismiss();
//
//                    }
//                }, 1000);

                Log.e("onMySuccess", jsonObject.toString() + "|");

                if (!MyApplication.getUser().saveSharedPreference(jsonObject)) {
                    Toast.makeText(getApplicationContext(),
                            "登录失败",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(LoginActivity.this, TajiappActivity.class);
                startActivity(intent);
                finish();
                ;

            }

            @Override
            public void onMyError(VolleyError error) {

//                new Handler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        _dialog.dismiss();
//                    }
//                });
                Log.e("onMyError", error.toString() + "|");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != UserClass.Request_Main) {
            return;
        }
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(this, TajiappActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
