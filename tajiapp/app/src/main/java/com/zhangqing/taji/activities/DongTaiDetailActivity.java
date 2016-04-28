package com.zhangqing.taji.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.CommentAdapter;
import com.zhangqing.taji.adapter.DongTaiListAdapter;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.bean.DongTaiBean;
import com.zhangqing.taji.util.AnimationUtil;
import com.zhangqing.taji.util.ImmUtil;
import com.zhangqing.taji.util.ScreenUtil;
import com.zhangqing.taji.view.pullable.RecyclerViewPullable;

import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/19.
 * 动态的详情页面
 */
public class DongTaiDetailActivity extends BaseActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    //基本数据
    private String mTid;
    private DongTaiBean mDongTai;

    //主界面视图相关
    private RecyclerViewPullable mRecyclerView;
    private CommentAdapter mRecyclerViewAdapter;
    private LinearLayout mHeaderView;


    //输入框相关视图
    private EmojiconEditText mEmojiEditText;
    private ImageView mEmojiImageView;
    private FrameLayout mEmojiGridFragment;
    private TextView mEmojiBtnSend;
    private LinearLayout mEmojiContainer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dongtai_detail);


        //初始化成员变量 最先执行
        initBasicView();

        //初始化输入框相关视图
        initInputView();

        //主界面适配器初始化数据
        initRecyclerView();

        //主界面headerView初始化
        addHeaderView();

        //开始加载评论数据
        mRecyclerView.setRefreshing(true);

    }


    /**
     * 成员变量的初始化
     */
    private void initBasicView() {
        mTid = getIntent().getExtras().getString("dongtai");
        mDongTai = DongTaiBean.getInstance(mTid);

        mRecyclerView = (RecyclerViewPullable) findViewById(R.id.dongtai_detail_recycler_view);

        mEmojiEditText = (EmojiconEditText) findViewById(R.id.dongtai_detail_emoji_edt);
        mEmojiImageView = (ImageView) findViewById(R.id.dongtai_detail_emoji_toggle_iv);
        mEmojiGridFragment = (FrameLayout) findViewById(R.id.dongtai_detail_emoji_framelayout);
        mEmojiBtnSend = (TextView) findViewById(R.id.dongtai_detail_emoji_send_btn);

        mEmojiContainer = (LinearLayout) findViewById(R.id.dongtai_detail_emoji_container);

    }

    /**
     * 初始化输入框相关视图
     */
    private void initInputView() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dongtai_detail_emoji_framelayout, EmojiconsFragment.newInstance(false))
                .commit();

        mEmojiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiGridFragment.getVisibility() == View.VISIBLE) {
                    mEmojiGridFragment.setAnimation(AnimationUtil.getSlideOutBottomAnimation());
                    mEmojiGridFragment.setVisibility(View.INVISIBLE);
                } else {
                    mEmojiGridFragment.setAnimation(AnimationUtil.getSlideInBottomAnimation());
                    mEmojiGridFragment.setVisibility(View.VISIBLE);
                }
            }
        });

        mEmojiBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiEditText.getText() == null || mEmojiEditText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "说点什么吧~", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserClass.getInstance().doComment(mTid, mEmojiEditText.getText().toString(),
                        new VolleyInterface(DongTaiDetailActivity.this) {
                            @Override
                            public void onMySuccess(JSONObject jsonObject) {
                                Toast.makeText(getApplicationContext(), "发表成功", Toast.LENGTH_SHORT).show();
                                mDongTai.mCountComment = (Integer.valueOf(mDongTai.mCountComment) + 1) + "";

                                DongTaiListAdapter.MyViewHolder myViewHolder = new DongTaiListAdapter.MyViewHolder(mHeaderView);
                                DongTaiListAdapter.updateViewHolder(DongTaiDetailActivity.this, myViewHolder, mDongTai, null);

                                mRecyclerView.setRefreshing(true);

                            }

                            @Override
                            public void onMyError(VolleyError error) {

                            }
                        });
            }
        });


    }

    /**
     * 捕捉整屏的点击事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                log(ev.getX() + "|" + ev.getY() + "|" +
                        mEmojiEditText.getY() + "|" + mEmojiEditText.getTop() + "|" + ScreenUtil.getRealViewTop(mEmojiGridFragment.getParent()) + "|" + ScreenUtil.getRealViewTop(mEmojiEditText.getParent()));

                /**
                 * 判断是否隐藏输入框容器
                 */
                if (mEmojiContainer.getVisibility() == View.VISIBLE) {

                    int editHeight = ScreenUtil.getRealViewTop(mEmojiEditText.getParent());
                    int gridHeight = ScreenUtil.getRealViewTop(mEmojiGridFragment.getParent());
                    if ((ev.getY() < editHeight && mEmojiGridFragment.getVisibility() != View.VISIBLE) ||
                            (ev.getY() < gridHeight && mEmojiGridFragment.getVisibility() == View.VISIBLE)) {
                        mEmojiContainer.setAnimation(AnimationUtil.getSlideOutBottomAnimation());
                        mEmojiContainer.setVisibility(View.GONE);
                        Log.e("dispatchTouchEvent", "return true");

                        ImmUtil.closeIMM(this);

                        return super.dispatchTouchEvent(ev);
                    }
                }
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 主界面数据加载
     */
    private void initRecyclerView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        mRecyclerView.getRecyclerView().setItemAnimator(new SlideInLeftAnimator());
//
//        mRecyclerView.getRecyclerView().getItemAnimator().setAddDuration(2000);
//        mRecyclerView.getRecyclerView().getItemAnimator().setRemoveDuration(2000);
//        mRecyclerView.getRecyclerView().getItemAnimator().setMoveDuration(2000);
//        mRecyclerView.getRecyclerView().getItemAnimator().setChangeDuration(2000);

        mRecyclerView.setAdapter(mRecyclerViewAdapter = new CommentAdapter(this));

        mRecyclerView.setOnLoadListener(new RecyclerViewPullable.OnLoadListener() {
            @Override
            public void onLoadMore(final int loadingPage) {

                UserClass.getInstance().getDongTaiComment(mTid, loadingPage, new VolleyInterface(getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        if (loadingPage == 1) {

                            mRecyclerView.setRefreshing(false);
                            mRecyclerViewAdapter.clearData();
                        }
                        if (mRecyclerViewAdapter.addData(jsonObject) != UserClass.Page_Per_Count) {
                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_End);
                        } else {
                            mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_Normal);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        if (loadingPage == 1) mRecyclerView.setRefreshing(false);
                        mRecyclerView.setLoadingMoreStatus(RecyclerViewPullable.LoadingMoreStatus_ERROR);
                    }
                });
            }
        });


    }

    /**
     * 构造动态详情HeaderView
     */
    private void addHeaderView() {
        mHeaderView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_home_hot_then_listview_item, null);

        mHeaderView.findViewById(R.id.home_hot_then_count_comment_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiContainer.getVisibility() != View.VISIBLE) {
                    mEmojiGridFragment.setVisibility(View.INVISIBLE);
                    mEmojiContainer.setAnimation(AnimationUtil.getSlideInBottomAnimation());
                    mEmojiContainer.setVisibility(View.VISIBLE);
                    mEmojiEditText.requestFocus();
                }
            }
        });


        if (mDongTai == null) return;

        DongTaiListAdapter.MyViewHolder myViewHolder = new DongTaiListAdapter.MyViewHolder(mHeaderView);

        DongTaiListAdapter.updateViewHolder(this, myViewHolder, mDongTai, null);

        mRecyclerView.setHeaderView(mHeaderView);
    }

    public void finishThis(View v) {
        finish();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mEmojiEditText, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mEmojiEditText);
    }
}
