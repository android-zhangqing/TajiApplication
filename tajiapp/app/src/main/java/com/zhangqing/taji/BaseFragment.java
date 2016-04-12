package com.zhangqing.taji;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangqing on 2016/4/12.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        simpleLog("onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        simpleLog("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        simpleLog("onResume");
        super.onResume();
    }

    @Override
    public void onStart() {
        simpleLog("onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        simpleLog("onStop");
        super.onStop();
    }

    @Override
    public void onPause() {
        simpleLog("onPause");
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        simpleLog("onHiddenChangedTo:" + hidden);
        super.onHiddenChanged(hidden);
    }

    private void simpleLog(String s) {
        Log.e("|****" + this.getClass().getSimpleName() + "****|", "##" + s);
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
        //">>" + this.getActivity().getClass().getSimpleName() +
        String method="??", classname="??";
        try {
            //method = Thread.currentThread().getStackTrace()[3].getMethodName();
            //classname = this.getClass().getSimpleName();
        } catch (Exception e) {
        }
        Log.e(">>" + classname + ">>" + method
                , result);
    }
}
