package com.zhangqing.taji;

import android.app.Fragment;
import android.util.Log;

/**
 * Created by zhangqing on 2016/4/12.
 */
public class BaseFragment extends Fragment {
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
        Log.e(">>" + this.getClass().getSimpleName() + ">>" +
                Thread.currentThread().getStackTrace()[3].getMethodName(), result);
    }
}
