package com.zhangqing.taji.activities;

import android.animation.Animator;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.SkillSelectAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.util.AnimationUtil;
import com.zhangqing.taji.util.DensityUtils;
import com.zhangqing.taji.util.ScreenUtil;
import com.zhangqing.taji.view.AdvancedGridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SkillSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private GridView mGridView;
    private SkillSelectAdapter mGridViewAdapter;
    private RelativeLayout mGridViewContainer;
    private Button mButton;

    private int current_selected_num = 0;

    private List<ImageView> mImageViewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_setting);
        mGridView = (GridView) findViewById(R.id.skill_setting_gridview);
        mGridViewContainer = (RelativeLayout) findViewById(R.id.skill_setting_gridview_container);
        mButton = (Button) findViewById(R.id.skill_setting_button);

        mButton.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Log.e("SkillSetting", mGridView.getMeasuredWidth() +
                        "|" + mGridView.getMeasuredHeight() + "|" + mGridView.getHeight() + "|" + mGridView.getMeasuredWidthAndState());
                mGridView.setNumColumns(4);
                mGridView.setCacheColorHint(0);
                mGridView.setAdapter(mGridViewAdapter = new SkillSelectAdapter(SkillSettingActivity.this, mGridView.getMeasuredWidth(), mGridView.getMeasuredHeight()));

                initData();

            }
        });


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.e("setOnItemClickListener", position + "|" + id);
                if (mGridView.getChildAt(position).getTag() instanceof SkillSelectAdapter.ViewHolder) {
                    ImageView iv = ((SkillSelectAdapter.ViewHolder) mGridView.getChildAt(position).getTag()).iv_selector;


                    if (mGridViewAdapter.toggleSelector(position)) {
                        iv.setImageResource(R.drawable.icon_intskill_table_select);

                        //取状态栏高度
                        int statuBarHeight = ScreenUtil.getStatusBarHeight(SkillSettingActivity.this);
                        //距屏幕左边
                        int real_left = position % 4 * mGridView.getChildAt(position).getMeasuredWidth() + iv.getLeft();
                        //距顶部
                        int real_top = position / 4 * mGridView.getChildAt(position).getMeasuredHeight() + iv.getTop() + (int) DensityUtils.px2dp(SkillSettingActivity.this, statuBarHeight);

                        Log.e("true", statuBarHeight + "|" + real_left + "|" + iv.getLeft() + "|" + mGridView.getChildAt(position) + "|" + mGridView.getChildAt(position).getLeft());

                        ImageView test_iv = mImageViewList.get(position);
                        test_iv.setVisibility(View.VISIBLE);

//                        final ImageView test_iv = new ImageView(SkillSettingActivity.this);
//                        test_iv.setImageResource(R.drawable.icon_intskill_table_unselect);
//
//                        int pxWidthHeight = DensityUtils.dp2px(SkillSettingActivity.this, 30);
//                        mGridViewContainer.addView(test_iv, pxWidthHeight, pxWidthHeight);

//                        int w = View.MeasureSpec.makeMeasureSpec(0,
//                                View.MeasureSpec.UNSPECIFIED);
//                        int h = View.MeasureSpec.makeMeasureSpec(0,
//                                View.MeasureSpec.UNSPECIFIED);
//                        test_iv.measure(w, h);
                        Log.e("test_iv", test_iv + "|" + test_iv.getMeasuredWidth() + "|" +
                                test_iv.getMeasuredHeight() + "|" + real_left + "|" + real_top);

                        final int test_real_left = real_left;
                        final int test_real_top = real_top;

//                        test_iv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                            @Override
//                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

//                        if (v != test_iv) return;
//                        test_iv.removeOnLayoutChangeListener(this);
                        test_iv.layout(test_real_left, test_real_top,
                                test_real_left + test_iv.getMeasuredWidth(),
                                test_real_top + test_iv.getMeasuredHeight());
                        int moveX = mGridView.getMeasuredWidth() / 2 - test_real_left;
                        int moveY = mGridView.getMeasuredHeight() - test_real_top + 20;
                        AnimationUtil.startAnimation2(test_iv, moveX, moveY, new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mImageViewList.get(position).setVisibility(View.INVISIBLE);
                                current_selected_num++;
                                updataButtonText();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
//                        });
                        Log.e("startAnimOver", "|||");
                    } else {
                        iv.setImageResource(R.drawable.icon_intskill_table_unselect);
                        current_selected_num--;
                        updataButtonText();
                    }
                }
            }

        });
    }

    private void updataButtonText() {
        mButton.setText("选择你感兴趣的标签" + current_selected_num + "/" + mImageViewList.size());
    }

    public void initData() {
        UserClass.getInstance().getSkillListAll(new VolleyInterface(this) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {
                mGridViewAdapter.initData(jsonObject);

                try {
                    for (int i = 0; i < (jsonObject.getJSONArray("data").length()); i++) {
                        ImageView test_iv = new ImageView(SkillSettingActivity.this);
                        test_iv.setImageResource(R.drawable.icon_intskill_table_unselect);
                        int pxWidthHeight = DensityUtils.dp2px(SkillSettingActivity.this, 30);
                        mGridViewContainer.addView(test_iv, pxWidthHeight, pxWidthHeight);
                        mImageViewList.add(test_iv);
                        test_iv.setVisibility(View.INVISIBLE);
                        Log.e("getSkillListAll", "addAnimImageView");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updataButtonText();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }

    public void onClickBtnFinish(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (current_selected_num == 0) {
            Toast.makeText(this, "请至少选择一项标签", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, mGridViewAdapter.getSelector(), Toast.LENGTH_SHORT).show();


    }
}
