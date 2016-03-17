package com.zhangqing.taji.activities.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;

import org.json.JSONObject;


public class RegisterSecondActivity extends Activity {
    private String mobile;
    private Button btnReSend;
    private Button btnVerify;
    private EditText editSmsCode;
    private String smsCode;

    private Runnable runnable;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.arg1 != 0) {
                btnReSend.setText("重新获取(" + msg.arg1 + "s)");
            } else {
                btnReSend.setText("重新获取");
                btnReSend.setEnabled(true);
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second);
        btnReSend = (Button) findViewById(R.id.register_second_send_again_btn);
        editSmsCode = (EditText) findViewById(R.id.register_second_smsverifycode_edttxt);
        btnVerify = (Button) findViewById(R.id.register_second_verifysms_btn);

        editSmsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editSmsCode.getText().toString().length() > 3) {
                    btnVerify.setEnabled(true);
                } else {
                    btnVerify.setEnabled(false);
                }
            }
        });

        mobile = getIntent().getExtras().getString("mobile");
        ((TextView) findViewById(R.id.register_second_mobileshow_tv))
                .setText("您的手机号：" + mobile);

        runnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                for (int i = 30; i >= 0; i--) {

                    Message msg = new Message();
                    msg.arg1 = i;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }
                }
            }
        };

        new Thread(runnable).start();

    }

    public void onClickBtnReSms(View v) {
        btnReSend.setEnabled(false);

        final Context context_ = getApplicationContext();

        MyApplication.getUser().doRegisterSmsSend(mobile,
                new VolleyInterface(context_) {

                    @Override
                    public void onMyError(VolleyError error) {
                        // TODO Auto-generated method stub
                        btnReSend.setEnabled(true);
                    }

                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        // TODO Auto-generated method stub
                        new Thread(runnable).start();
                    }

                });

    }

    public void finishThis(View v) {
        finish();
    }

    public void onClickBtnCheckSmsverify(View v) {
        btnVerify.setEnabled(false);

        smsCode = editSmsCode.getText().toString();
        MyApplication.getUser().doRegisterSmsCheck(mobile, smsCode, new VolleyInterface(getApplicationContext()) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {
                // TODO Auto-generated method stub


                Intent intent = new Intent(RegisterSecondActivity.this, RegisterThirdActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mobile", mobile);
                bundle.putString("smsCode", smsCode);
                intent.putExtras(bundle);
                startActivityForResult(intent, UserClass.Request_Register_Second);
                Toast.makeText(getApplicationContext(),
                        "校验成功",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMyError(VolleyError error) {
                // TODO Auto-generated method stub
                btnVerify.setEnabled(true);
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != UserClass.Request_Register_Second) {
            return;
        }
        setResult(resultCode);
        finish();
    }
}
