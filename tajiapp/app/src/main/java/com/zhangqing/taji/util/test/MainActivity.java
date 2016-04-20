package com.zhangqing.taji.util.test;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhangqing.taji.R;

/**
 * 2016-04-20
 * 网上找的关于 仿微信切换表情框与输入框 的实现方案，有时间好好研究下
 */
public class MainActivity extends Activity {
    private Window window;
    private InputMethodManager imm;
    private int virtual_key = 0; // 虚拟按键高度
    private DisplayMetrics dm = new DisplayMetrics();
    private SharedPreferences sp = null;
    private View view_v;
    private EditText input_edit;
    private KeyboardLayout input_listener;
    private ImageView input_image;
    private LinearLayout input_linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("UserMessage", Activity.MODE_PRIVATE);
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        window = getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 隐藏软键盘


        view_v = findViewById(R.id.view_v);
        input_edit = (EditText) findViewById(R.id.input_edit);
        input_listener = (KeyboardLayout) findViewById(R.id.input_listener);
        input_image = (ImageView) findViewById(R.id.input_image);
        input_linear = (LinearLayout) findViewById(R.id.input_linear);

        if (!sp.getString("input_height", "").trim().equals("")) {
            LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams) view_v.getLayoutParams();
            layout.height = Integer.valueOf(sp.getString("input_height", ""));
            view_v.setLayoutParams(layout);
        }

        input_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (view_v.getVisibility() == View.VISIBLE) {

                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                            | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 隐藏软键盘

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    view_v.setVisibility(View.INVISIBLE);

                    input_edit.setFocusable(true);
                    input_edit.setFocusableInTouchMode(true);
                    input_edit.requestFocus();

                    if (imm != null) {
                        imm.showSoftInput(input_edit, InputMethodManager.SHOW_FORCED);//强制显示键盘
                    }
                    LinearLayout.LayoutParams category_layout_params = (LinearLayout.LayoutParams) view_v.getLayoutParams();
                    if (!sp.getString("input_height", "").trim().equals("")) {
                        category_layout_params.height = Integer.valueOf(sp.getString("input_height", ""));
                    } else {
                        category_layout_params.height = (int) (dm.heightPixels * 0.5);
                    }
                    view_v.setLayoutParams(category_layout_params);

                } else {

                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                            | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 隐藏软键盘

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    view_v.setVisibility(View.VISIBLE);

                    if (imm != null) {
                        imm.hideSoftInputFromWindow(input_edit.getWindowToken(), 0); //强制隐藏键盘
                    }
                    LinearLayout.LayoutParams category_layout_params = (LinearLayout.LayoutParams) view_v.getLayoutParams();
                    if (!sp.getString("input_height", "").trim().equals("")) {
                        category_layout_params.height = Integer.valueOf(sp.getString("input_height", ""));
                    } else {
                        category_layout_params.height = (int) (dm.heightPixels * 0.5);
                    }
                    view_v.setLayoutParams(category_layout_params);
                }
            }
        });


        input_edit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    // 隐藏表情选择框
                    if (view_v.getVisibility() == View.VISIBLE) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        view_v.setVisibility(View.INVISIBLE);
                    }
                }

                return false;
            }
        });

        input_listener.setOnkbdStateListener(new KeyboardLayout.onKybdsChangeListener() {
            public void onKeyBoardStateChange(int state) {
                switch (state) {
                    case KeyboardLayout.KEYBOARD_STATE_INIT:
                        boolean isOpen = false;
                        if (imm != null) {
                            imm.isActive();
                        }
                        if (!isOpen && view_v.getVisibility() == View.INVISIBLE) {

                            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                    | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            view_v.setVisibility(View.GONE);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private class GetIput_height extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... arg0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int[] location = new int[2];
//            input_linear.getLocationOnScreen(location);
//            int y = location[1];
//
//            int sss = y + input_linear.getHeight();
//
//            if (imm.isActive()) {
//                if (sss < (dm.heightPixels * 0.8)) {
//
//                    int face_height = dm.heightPixels - sss- virtual_key;
//                    System.out.println("11----------face_height:" + face_height);
//                    Editor editor = sp.edit();// 获取编辑器
//                    editor.putString("input_height",String.valueOf(face_height));
//                    editor.commit();
//                }
//            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!sp.getString("input_height", "").trim().equals("")) {
                LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams) view_v.getLayoutParams();
                layout.height = Integer.valueOf(sp.getString("input_height", ""));
                view_v.setLayoutParams(layout);
            }
        }
    }

    // 点击空白处隐藏键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();

            if (sp.getString("input_height", "").trim().equals("")) {
                new GetIput_height().execute("");
            }
            if (isShouldHideInput(v, ev)) {
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    view_v.setVisibility(View.GONE);

                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                            | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();

            if (event.getX() < dm.widthPixels && event.getY() > top) {
                return false;
            } else if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top; // 手机状态栏高度
            int mobileheight = statusBarHeight + input_listener.getHeight();// 应用的高度+手机状态栏高度
            if (mobileheight != dm.heightPixels) { // 如果手机屏幕=应用的高度+手机状态栏高度，即没有虚拟按键。若是不相等 ,则虚拟按键存在.virtual_key的值就是虚拟按键的高度了
                virtual_key = dm.heightPixels - mobileheight;
            }
        }
    }
}
