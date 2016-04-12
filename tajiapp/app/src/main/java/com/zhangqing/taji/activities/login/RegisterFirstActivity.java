package com.zhangqing.taji.activities.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;

import org.json.JSONObject;


public class RegisterFirstActivity extends BaseActivity {
    private EditText mobileEditText;
    private Button submitButton;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_first);

        mobileEditText = (EditText) findViewById(R.id.register_first_mobile_edttxt);
        submitButton = (Button) findViewById(R.id.register_first_submitmobile_btn);

        mobileEditText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mobileEditText.getText().toString().length() == 11) {
                    submitButton.setEnabled(true);
                } else {
                    submitButton.setEnabled(false);
                }

            }
        });

    }

    public void finishThis(View v) {
        finish();
    }

    public void onClickButtonClearEdittext(View v) {
        mobileEditText.setText("");
    }

    public void onClickButtonSubmitMobile(View v) {
        mobile = mobileEditText.getText().toString();
        if (mobile.length() != 11) {
            Toast.makeText(getApplicationContext(), "手机号格式错误",
                    Toast.LENGTH_LONG).show();
            return;
        }
        final Context context_ = getApplicationContext();
        submitButton.setEnabled(false);
//        final Dialog dialog_ = new Dialog(this, R.style.LodingDialog);
//        dialog_.setContentView(R.layout.layout_progressbar);
//        dialog_.setCancelable(false);
//        dialog_.show();

        UserClass.getInstance().doRegisterSmsSend(mobile,
                new VolleyInterface(context_) {

                    @Override
                    public void onMySuccess(JSONObject jsonObject) {

//                        dialog_.dismiss();
                        submitButton.setEnabled(true);

                        Intent intent = new Intent(context_,
                                RegisterSecondActivity.class);
                        Bundle b = new Bundle();
                        b.putString("mobile", mobile);
                        intent.putExtras(b);
                        startActivityForResult(intent, UserClass.Request_Register_First);
                       // finish();
                    }

                    @Override
                    public void onMyError(VolleyError error) {
//                        dialog_.dismiss();
                        submitButton.setEnabled(true);
                    }

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode!= UserClass.Request_Register_First){
            return;
        }
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

}
