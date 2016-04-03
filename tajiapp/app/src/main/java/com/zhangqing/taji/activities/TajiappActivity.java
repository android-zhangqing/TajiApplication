package com.zhangqing.taji.activities;

import android.animation.Animator;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.activities.login.LoginActivity;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.util.AnimationUtil;
import com.zhangqing.taji.view.BottomBar;
import com.zhangqing.taji.view.BottomBar.OnTabClickListener;
import com.zhangqing.taji.view.TopBar;
import com.zhangqing.taji.view.TopBar.OnTopBarClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class TajiappActivity extends FragmentActivity implements OnTabClickListener,
        OnTopBarClickListener {
    private Fragment[] fragments = new Fragment[7];
    private TopBar topBar;
    public BottomBar bottomBar;
    private int currentFragment = -1;

    private LinearLayout mPublishLeft;
    private LinearLayout mPublishRight;
    private View mPublishBg;
    private View mPublishBgBg;
    private boolean isShowingPublish = false;


    /**
     * 设置状态栏背景状态
     */
    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
        SystemStatusManager tintManager = new SystemStatusManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.bgcolor_systembar);//状态栏无背景
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.activity_main);

        mPublishLeft = (LinearLayout) findViewById(R.id.main_publish_left);
        mPublishRight = (LinearLayout) findViewById(R.id.main_publish_right);

        mPublishBg = (View) findViewById(R.id.main_publish_bg);
        mPublishBgBg = (View) findViewById(R.id.main_publish_bg_bg);


        if (UserClass.getInstance().reLoadSharedPreferences() == false) {
            startLoginActivity();
            return;
        }


        MyApplication.connect(TajiappActivity.this);
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            Map<String, UserInfo> map = new HashMap<String, UserInfo>();

            @Override
            public UserInfo getUserInfo(String s) {

                Log.e("getUserInfo", s + "|");
                if (map.containsKey(s)) {
                    return map.get(s);
                } else {
                    UserClass.getInstance().getOthersUserInfo(s, new VolleyInterface(TajiappActivity.this.getApplicationContext()) {
                        @Override
                        public void onMySuccess(JSONObject jsonObject) {
                            Log.e("onMySuccess", jsonObject.toString());
                            try {
                                String name = jsonObject.getString("username");
                                String avatar = jsonObject.getString("avatar");
                                String uid = jsonObject.getString("uid");

                                if (name.equals("") || name.equals("null"))
                                    name = "游客" + uid;
                                if (avatar.indexOf("http") == -1) return;
                                UserInfo userInfo = new UserInfo(uid, name, Uri.parse(avatar));
                                RongIM.getInstance().refreshUserInfoCache(userInfo);
                                map.put(uid, userInfo);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                return;
                            }

                        }

                        @Override
                        public void onMyError(VolleyError error) {

                        }
                    });


                }

                return null;
            }
        }, true);

        // if (MyApplication.rcHasConnect == false) {


        //  }


//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        for (int i = 0; i < fragments.length; i++) {
//            fragments[i] = initNewFragment(i);
//            if (fragments[i] != null) {
//                ft.add(R.id.contentframe, fragments[i]);
//            }
//        }
//        ft.commit();

        bottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        bottomBar.setOnTabClickListener(this);
        topBar = (TopBar)

                findViewById(R.id.topbar);

        topBar.setOnTopBarClickListener(this);

        showFragment(0);
        Log.e("getMemmory", ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() + "|");


//        UserClass.getInstance().isLogin(new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String arg0) {
//                // TODO Auto-generated method stub
////                Intent intent = new Intent(TajiappActivity.this,
////                        RegisterFirstActivity.class);
////                startActivityForResult(intent, UserClass.Request_Main);
//
//            }
//
//        }, TajiappActivity.this);
    }

    private void startLoginActivity() {
        UserClass.getInstance().clear();
        Intent intent = new Intent(TajiappActivity.this,
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showFragment(int whichFragment) {
        if (currentFragment == whichFragment) return;
        currentFragment = whichFragment;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == currentFragment) {
                if (fragments[i] == null) {
                    fragments[i] = initNewFragment(whichFragment);
                    if (fragments[i] != null) {
                        ft.add(R.id.contentframe, fragments[i]);
                    }
                } else {
                    ft.show(fragments[i]);
                }

            } else {
                if (fragments[i] != null) {
                    ft.hide(fragments[i]);

                }

            }
        }
        ft.commit();
    }

    private Fragment initNewFragment(int whichFragment) {
        switch (whichFragment) {
            case 0:
                return new FragmentHomeHot();
            case 3:
                return new FragmentCircle();
            case 4:
                return new FragmentMessage();
            case 5:
                return new FragmentMy();


        }
        return null;
    }

    @Override
    public void tabSwitchTo(int whichParent) {
        topBar.switchToParent(TajiappActivity.this, whichParent);
    }

    @Override
    public void tabClickPublishBtn() {

        if (!isShowingPublish) {
            isShowingPublish = true;

            // mPublishBgBg.setAlpha(1f);
            //AnimationUtil.startAnimationByMyself(mPublishLeft, -200, -100, null);
            AnimationUtil.startAnimationJumpIn(mPublishLeft, -200, -100, null);
            AnimationUtil.startAnimationJumpIn(mPublishRight, 200, -100, null);
            // AnimationUtil.startAnimationJumpIn(mPublishBg, 200, -100, null);

            Animation a = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            a.setInterpolator(new BounceInterpolator());
            a.setDuration(1000);
            mPublishBg.setAnimation(a);
            mPublishBg.setVisibility(View.VISIBLE);
        } else {
            isShowingPublish = false;

//            AnimationUtil.startAnimationFadeOut(mPublishLeft, null);
//            AnimationUtil.startAnimationFadeOut(mPublishRight, null);
//
//            Animation a = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                    0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
//            a.setInterpolator(new AccelerateInterpolator());
//            a.setDuration(500);
//            mPublishBg.setAnimation(a);
//            mPublishBg.setVisibility(View.GONE);
            Animation a = new AlphaAnimation(1, 0);
            a.setDuration(400);
            a.setFillAfter(false);
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mPublishLeft.setAlpha(0);
                    mPublishRight.setAlpha(0);
                    mPublishBg.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mPublishBgBg.startAnimation(a);
            //  AnimationUtil.startAnimationFadeOut(mPublishRight, null);
            // mPublishBg.setVisibility(View.INVISIBLE);

        }

//        Intent intent = new Intent(TajiappActivity.this, PublishActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.activity_open_bottom_in, 0);
    }


    @Override
    public void topbarSwitchToFragment(int whichFragment) {
        showFragment(whichFragment);
    }

    @Override
    public void topbarHomeSearchClick() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void topbarCircleSearchClick() {
        topbarHomeSearchClick();
    }

    @Override
    public void topbarCircleCreateClick() {

    }

    @Override
    public void topbarMessageCreateClick() {
        inputTitleDialog();
    }


    private void inputTitleDialog() {

        final EditText inputServer = new EditText(this);
        inputServer.setText("1007");
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chat With Whom?").setView(inputServer).setNegativeButton(
                "取消", null);
        builder.setPositiveButton("开始聊天",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String inputName = inputServer.getText().toString();
                        RongIM.getInstance().startPrivateChat(TajiappActivity.this, inputName, "");
                    }
                });
        builder.show();
    }

    long lastPressTime;

    @Override
    public void onBackPressed() {
        long current = System.currentTimeMillis();
        //Log.e("onBackPressed", current + "|" + lastPressTime + "|" + (current - lastPressTime));
        if (current - lastPressTime > 2000) {
            Toast.makeText(TajiappActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            lastPressTime = current;
            return;
        }
        super.onBackPressed();
    }

    public void onClickBtnClearUserInfo(View v) {
        startLoginActivity();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}