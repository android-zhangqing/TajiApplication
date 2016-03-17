package com.zhangqing.taji.base;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.activities.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class VolleyInterface {

    Context mContext;
    private Listener<String> listener;
    private Dialog mDialog;
    private ErrorListener errorListener;
    private long startTime;

    public VolleyInterface(Context context) {
        this.mContext = context;
        startTime = System.currentTimeMillis();
        if (context instanceof Activity) {
            mDialog = CustomProgress.show(context, "", true, null);
        }
    }


    public abstract void onMySuccess(JSONObject jsonObject);

    public abstract void onMyError(VolleyError error);

    private void dismissDialog(){
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                    }
                });
            }
        }
    }

    public Listener<String> loadingListener() {
        listener = new Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                dismissDialog();

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(arg0);

                    if (!(jsonObject.getString("status").equals("200"))) {
                        if (jsonObject.has("status")) {
                            if (jsonObject.getString("status").equals("300")) {
                                if (jsonObject.has("msg")) {
                                    if (jsonObject.getString("msg").equals("用户不存在")) {
                                        onMyError(new VolleyError("用户不存在"));
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startLoginActivity();
                                            }
                                        }, 500);

                                        return;
                                    }
                                }
                            }

                        }

                        onMyError(new VolleyError(jsonObject.getString("status")
                                + "错误"));
                        Toast.makeText(mContext, jsonObject.getString("msg"),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    onMyError(new VolleyError("网络解析异常"));
                    Toast.makeText(mContext, "网络解析异常", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (jsonObject.has("data")) {
                    try {
                        onMySuccess(jsonObject.getJSONObject("data"));
                        return;
                    } catch (JSONException e) {
                        if (jsonObject.isNull("data")) {
                            onMySuccess(jsonObject);
                            Log.e("jsonObject.isNull", "|");
                            return;
                        }
                        try {
                            jsonObject.getJSONArray("data");
                            onMySuccess(jsonObject);
                            Log.e("jsonObject.getJSONArray", "|");
                            return;
                        } catch (JSONException e1) {
                            onMyError(new VolleyError("data网络解析异常"));
                            Toast.makeText(mContext, "data网络解析异常", Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }

                    }
                } else {
                    onMySuccess(jsonObject);
                }


            }
        };
        return listener;
    }

    private void startLoginActivity() {
        if (mContext instanceof LoginActivity) {
            Toast.makeText(mContext, "用户不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(mContext, "身份验证失败，请重新登录", Toast.LENGTH_SHORT).show();
        if (mContext instanceof Activity) {

            MyApplication.getUser().clear();
            Intent intent = new Intent(mContext,
                    LoginActivity.class);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        }

    }

    public ErrorListener errorListener() {
        errorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                Log.e("VolleyOnError", arg0.toString() + "|");
                dismissDialog();

                onMyError(arg0);
                //onMyError(new VolleyError("网络解析异常"));
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }
        };
        return errorListener;
    }

    public void performErrorListener(String exceptionMessage) {
        errorListener.onErrorResponse(new VolleyError(exceptionMessage));
    }

    public void performErrorListener() {
        errorListener.onErrorResponse(null);
    }
}
