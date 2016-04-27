package com.zhangqing.taji.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.activities.login.LoginActivity;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.database.DatabaseManager;
import com.zhangqing.taji.util.CameraUtil;
import com.zhangqing.taji.view.BottomBar;
import com.zhangqing.taji.view.BottomBar.OnTabClickListener;
import com.zhangqing.taji.view.TopBar;
import com.zhangqing.taji.view.TopBar.OnTopBarClickListener;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * 主界面Activity
 */
public class TajiappActivity extends BaseActivity implements OnTabClickListener,
        OnTopBarClickListener {
    public static final int REQUEST_SELECT_PIC = 1;
    public static final int REQUEST_SKILL_SETTING = 2;

    private Fragment[] fragments = new Fragment[7];
    private TopBar topBar;
    public BottomBar bottomBar;
    private int currentFragment = -1;

    private LinearLayout mPublishLeft;
    private LinearLayout mPublishRight;
    private View mPublishBg;
    private View mPublishClickableBg;
    private boolean isShowingPublish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Toast.makeText(this,getMac(),Toast.LENGTH_LONG).show();
        mPublishLeft = (LinearLayout) findViewById(R.id.main_publish_left);
        mPublishRight = (LinearLayout) findViewById(R.id.main_publish_right);

        mPublishBg = (View) findViewById(R.id.main_publish_bg);
        mPublishClickableBg = (View) findViewById(R.id.main_publish_clickable_bg);


        if (UserClass.getInstance().reLoadSharedPreferences() == false) {
            startLoginActivity();
            return;
        }


        MyApplication.connect(TajiappActivity.this);
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {


            @Override
            public UserInfo getUserInfo(String s) {

                UserInfo userInfo = DatabaseManager.getInstance().queryUserInfoById(s);
                if (userInfo != null) {
                    log("success" + s);
                    return userInfo;
                }
                DatabaseManager.getInstance().insert(s, TajiappActivity.this);
                log("null" + s);
                return null;
            }
        }, true);


        bottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        bottomBar.setOnTabClickListener(this);
        topBar = (TopBar)
                findViewById(R.id.topbar);

        topBar.setOnTopBarClickListener(this);

        showFragment(0);
        //Log.e("getMemmory", ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() + "|");
    }

    private void startLoginActivity() {
        UserClass.getInstance().clear();
        Intent intent = new Intent(TajiappActivity.this,
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showFragment(int whichFragment) {
        log(whichFragment + "");
        if (currentFragment == whichFragment) return;
        int lastFragment = currentFragment;
        currentFragment = whichFragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();


        if (fragments[currentFragment] == null) {
            fragments[currentFragment] = initNewFragment(whichFragment);
            if (fragments[currentFragment] != null) {
                ft.add(R.id.contentframe, fragments[currentFragment]);
            }
        } else {
            ft.show(fragments[currentFragment]);
        }
        if (lastFragment != -1 && fragments[lastFragment] != null) {
            ft.hide(fragments[lastFragment]);
        }
        ft.commit();
    }

    private Fragment initNewFragment(int whichFragment) {
        switch (whichFragment) {
            case 0:
                return new FragmentHomeHot();
            case 1:
                return new FragmentDongtaiFollow();
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
        if (isShowingPublish)
            tabClickPublishBtn();
        topBar.switchToParent(TajiappActivity.this, whichParent);
    }

    public void tabClickPublishBtn(View v) {
        tabClickPublishBtn();
    }

    @Override
    public void tabClickPublishBtn() {

        if (!isShowingPublish) {
            mPublishClickableBg.setVisibility(View.VISIBLE);
            isShowingPublish = true;
            Animation a = AnimationUtils.loadAnimation(this, R.anim.publish_btn_left_show);
            Animation b = AnimationUtils.loadAnimation(this, R.anim.publish_btn_right_show);
            Animation c = AnimationUtils.loadAnimation(this, R.anim.publish_btn_bg_show2);
            a.setFillAfter(false);
            b.setFillAfter(false);
            mPublishLeft.setAnimation(a);
            mPublishRight.setAnimation(b);
            mPublishBg.setAnimation(c);
            mPublishLeft.setVisibility(View.VISIBLE);
            mPublishRight.setVisibility(View.VISIBLE);
            mPublishBg.setVisibility(View.VISIBLE);
        } else {
            mPublishClickableBg.setVisibility(View.GONE);
            isShowingPublish = false;
            Animation a = AnimationUtils.loadAnimation(this, R.anim.publish_btn_left_hide);
            Animation b = AnimationUtils.loadAnimation(this, R.anim.publish_btn_right_hide);
            Animation c = AnimationUtils.loadAnimation(this, R.anim.publish_btn_bg_hide2);
            a.setFillAfter(false);
            b.setFillAfter(false);
            mPublishLeft.setAnimation(a);
            mPublishRight.setAnimation(b);
            mPublishBg.setAnimation(c);
            mPublishLeft.setVisibility(View.GONE);
            mPublishRight.setVisibility(View.GONE);
            mPublishBg.setVisibility(View.GONE);
        }
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
        builder.setTitle("Chat With Whom?").setView(inputServer).setNegativeButton("取消", null);
        builder.setPositiveButton("开始聊天", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String inputName = inputServer.getText().toString();
                RongIM.getInstance().startPrivateChat(TajiappActivity.this, inputName, "zz");
            }
        });
        builder.show();
    }

    long lastPressTime;

    @Override
    public void onBackPressed() {
        if (isShowingPublish) {
            tabClickPublishBtn();
            return;
        }
        long current = System.currentTimeMillis();
        //Log.e("onBackPressed", current + "|" + lastPressTime + "|" + (current - lastPressTime));
        if (current - lastPressTime > 2000) {
            Toast.makeText(TajiappActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            lastPressTime = current;
            return;
        }
        super.onBackPressed();
    }

    //退出登录
    public void onClickBtnClearUserInfo(View v) {
        //友盟统计 退出登录
        MobclickAgent.onProfileSignOff();

        startLoginActivity();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public void onClickUpload(View v) {
        tabClickPublishBtn();
        switch (v.getId()) {
            case R.id.main_publish_left: {
                startActivityForResult(CameraUtil.Picture.choosePicture(), REQUEST_SELECT_PIC);
                break;
            }
            case R.id.main_publish_right: {
                startActivity(new Intent(this, PublishActivity.class));
                overridePendingTransition(R.anim.activity_open_bottom_in, 0);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", "Activity");
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", requestCode + "|" + resultCode + "|" + (data == null ? "null" : (data)));
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_SELECT_PIC: {
                if (data == null || data.getData() == null) return;
                String path = CameraUtil.uri2filePath(this, data.getData());
                Intent intent = new Intent(this, PublishActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        }
    }
}