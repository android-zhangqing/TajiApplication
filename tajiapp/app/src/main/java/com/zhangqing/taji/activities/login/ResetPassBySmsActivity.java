package com.zhangqing.taji.activities.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/2/26.
 */
public class ResetPassBySmsActivity extends Activity {
    private TextView mMobileTextView;
    private TextView mVerifyTextView;
    private TextView mPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass_by_sms);

        mMobileTextView = (TextView) findViewById(R.id.reset_pass_by_sms_mobile_edttxt);
        mVerifyTextView = (TextView) findViewById(R.id.reset_pass_by_sms_verify_edttxt);
        mPasswordTextView = (TextView) findViewById(R.id.reset_pass_by_sms_new_pass_edttxt);


    }

    public void onClickButtonClearEdittext(View v) {
        mMobileTextView.setText("");
    }

    public void onClickBtnSend(View v) {
        UserClass.getInstance().doResetPassSmsSend(mMobileTextView.getText().toString(), new VolleyInterface(ResetPassBySmsActivity.this) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {
                Toast.makeText(ResetPassBySmsActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMyError(VolleyError error) {
                Toast.makeText(ResetPassBySmsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void finishThis(View v) {
        finish();
    }

    public void onClickBtnReset(View v) {
        UserClass.getInstance().doResetPasswordBySms(mMobileTextView.getText().toString(),
                mPasswordTextView.getText().toString(), mVerifyTextView.getText().toString(),
                new VolleyInterface(ResetPassBySmsActivity.this) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        Toast.makeText(ResetPassBySmsActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Toast.makeText(ResetPassBySmsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
