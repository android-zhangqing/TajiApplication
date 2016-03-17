package com.zhangqing.taji.base;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.zhangqing.taji.MyApplication;

import java.io.UnsupportedEncodingException;

public class VolleyRequest {
    public static StringRequest stringRequest;


    /**
     * 使用Volley进行get请求
     *
     * @param url
     * @param tag 该请求标志
     * @param vif 回调
     */
    public static void RequestGet(String url, String tag, VolleyInterface vif) {

        String versionName = "";
        PackageManager pm = vif.mContext.getPackageManager();
        try {
            PackageInfo pi = null;
            pi = pm.getPackageInfo(vif.mContext.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        url = url + "&version=" + versionName;

        Log.e("VolleyRequest", url);
        MyApplication.getRequestQeuee().cancelAll(tag);
        stringRequest = new MyStringRequest(Method.GET, url, vif.loadingListener(), vif.errorListener());
        stringRequest.setTag(tag);

        MyApplication.getRequestQeuee().add(stringRequest);
        MyApplication.getRequestQeuee().start();
    }


}

class MyStringRequest extends StringRequest {
    public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, "UTF-8");
        } catch (UnsupportedEncodingException var4) {
            parsed = new String(response.data);
        }
        Log.e("VolleyResponse", parsed);

        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}