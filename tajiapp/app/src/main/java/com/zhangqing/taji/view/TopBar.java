package com.zhangqing.taji.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhangqing.taji.R;

public class TopBar extends LinearLayout implements OnClickListener {

    private static final int ACTION_HOME_SEARCH = 0;
    private static final int ACTION_CIRCLE_CREATE = 1;
    private static final int ACTION_CIRCLE_SEARCH = 2;
    private static final int ACTION_MESSAGE_CREATE = 3;
    private static final int ACTION_MY_SETTING = 4;


    //parent=0 首页上方
    private TextView textViewHomeHot;
    private TextView textViewHomeConcern;
    private TextView textViewHomeCamp;
    private ImageView imgViewHomeSearch;

    //parent=1 圈子上方
    private TextView textViewCircleCreate;
    private TextView textViewCircleTitle;
    private ImageView imgViewCircleSearch;


    //parent=2 消息上方

    private TextView textViewMessageTitle;
    private TextView textViewMessageCreate;
    private ImageView imgViewMessageBlank;


    //parent=3,我的 上方
    private ImageView imageViewMyBlank;
    private TextView textViewMyTitle;
    private ImageView imageViewMySetting;


    private int currentParent = -1;
    private int currentChild = -1;
    private OnTopBarClickListener topBarClickListener;

    private Context context;

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // 取xml属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.mybar);
        int position = Integer.parseInt(typedArray
                .getString(R.styleable.mybar_topposition));
        switchToParent(context, 0);
    }


    public void switchToParent(Context context, int whichParent) {
        if (currentParent == whichParent)
            return;
        currentParent = whichParent;
        removeAllViews();
        setBackgroundResource(R.drawable.home_hot_title_bar_bg);
        //setBackgroundResource(R.color.bgcolor_bar_first);

        int topbarHeight = getResources().getDimensionPixelSize(R.dimen.topbar_height);
        switch (whichParent) {
            case 0:
                if (textViewHomeHot == null) {
                    textViewHomeHot = new TextView(context);
                    textViewHomeHot.setText("热门");
                    textViewHomeHot.setTextSize(20);
                    textViewHomeHot.setGravity(Gravity.CENTER);
                    setMyTag(textViewHomeHot, "fragment", "0", "switch to another frament");

                    textViewHomeHot.setOnClickListener(this);
                }
                if (textViewHomeConcern == null) {
                    textViewHomeConcern = new TextView(context);
                    textViewHomeConcern.setText("订阅");
                    textViewHomeConcern.setTextSize(20);
                    textViewHomeConcern.setGravity(Gravity.CENTER);
                    setMyTag(textViewHomeConcern, "fragment", "1", "switch to another frament");
                    textViewHomeConcern.setOnClickListener(this);
                }
                if (textViewHomeCamp == null) {
                    textViewHomeCamp = new TextView(context);
                    textViewHomeCamp.setText("活动");
                    textViewHomeCamp.setTextSize(20);
                    textViewHomeCamp.setGravity(Gravity.CENTER);
                    setMyTag(textViewHomeCamp, "fragment", "2", "switch to another frament");
                    textViewHomeCamp.setOnClickListener(this);
                }
                if (imgViewHomeSearch == null) {
                    imgViewHomeSearch = new ImageView(context);
                    imgViewHomeSearch.setImageResource(R.drawable.icon_tab_search_normal);
                    imgViewHomeSearch.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imgViewHomeSearch.setBackgroundResource(R.drawable.press_bg);
                    setMyTag(imgViewHomeSearch, "action", ACTION_HOME_SEARCH + "", "首页搜索按钮");
                    imgViewHomeSearch.setOnClickListener(this);
                }
                LayoutParams la = getMyLayoutParams(0, LayoutParams.MATCH_PARENT, 1, 0, 0);
                addView(textViewHomeHot, la);
                addView(textViewHomeConcern, la);
                addView(textViewHomeCamp, la);
                addView(imgViewHomeSearch, getMyLayoutParams(topbarHeight, topbarHeight, 0, 0, 0));
                switchToFragment(context, 0);
                break;
            case 1:
                if (textViewCircleCreate == null) {
                    textViewCircleCreate = new TextView(context);
                    textViewCircleCreate.setText("创建");
                    textViewCircleCreate.setBackgroundResource(R.drawable.press_bg);
                    textViewCircleCreate.setTextSize(14);
                    textViewCircleCreate.setTextColor(getResources().getColor(R.color.textcolor_bar_first_unselect));
                    setMyTag(textViewCircleCreate, "action", ACTION_CIRCLE_CREATE + "", "圈子创建按钮");
                    textViewCircleCreate.setGravity(Gravity.CENTER);
                    textViewCircleCreate.setOnClickListener(this);
                }
                if (textViewCircleTitle == null) {
                    textViewCircleTitle = new TextView(context);
                    textViewCircleTitle.setText("圈子");
                    textViewCircleTitle.setTextColor(getResources().getColor(R.color.textcolor_bar_first_unselect));
                    textViewCircleTitle.setTextSize(20);
                    textViewCircleTitle.setGravity(Gravity.CENTER);

                }

                if (imgViewCircleSearch == null) {
                    imgViewCircleSearch = new ImageView(context);
                    imgViewCircleSearch
                            .setImageResource(R.drawable.icon_tab_search_normal);
                    imgViewCircleSearch.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imgViewCircleSearch.setBackgroundResource(R.drawable.press_bg);
                    setMyTag(imgViewCircleSearch, "action", ACTION_CIRCLE_SEARCH + "", "圈子搜索按钮");
                    imgViewCircleSearch.setOnClickListener(this);
                }

                textViewCircleCreate.setPadding(20, 0, 20, 0);
                addView(textViewCircleCreate, getMyLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 0, 0, 0));
                addView(textViewCircleTitle, getMyLayoutParams(0, LayoutParams.MATCH_PARENT, 1, 0, 0));
                addView(imgViewCircleSearch, getMyLayoutParams(topbarHeight, topbarHeight, 0, 0, 0));

                switchToFragment(context, 3);
                break;
            case 2:

                if (textViewMessageTitle == null) {
                    textViewMessageTitle = new TextView(context);
                    textViewMessageTitle.setText("消息");
                    textViewMessageTitle.setTextColor(getResources().getColor(R.color.textcolor_bar_first_unselect));
                    textViewMessageTitle.setTextSize(20);
                    textViewMessageTitle.setGravity(Gravity.CENTER);
                }

                if (textViewMessageCreate == null) {
                    textViewMessageCreate = new TextView(context);
                    textViewMessageCreate.setText("聊天");
                    textViewMessageCreate.setBackgroundResource(R.drawable.press_bg);
                    textViewMessageCreate.setTextSize(14);
                    textViewMessageCreate.setTextColor(getResources().getColor(R.color.textcolor_bar_first_unselect));
                    setMyTag(textViewMessageCreate, "action", ACTION_MESSAGE_CREATE + "", "消息发起按钮");
                    textViewMessageCreate.setGravity(Gravity.CENTER);
                    textViewMessageCreate.setOnClickListener(this);
                }
                if (imgViewMessageBlank == null) {
                    imgViewMessageBlank = new ImageView(context);
                    imgViewMessageBlank.setImageResource(R.drawable.icon_blank_42);
                }

                textViewMessageCreate.setPadding(30, 0, 30, 0);
                addView(imgViewMessageBlank, getMyLayoutParams(topbarHeight, topbarHeight, 0, 0, 0));
                addView(textViewMessageTitle, getMyLayoutParams(0, LayoutParams.MATCH_PARENT, 1, 0, 0));
                addView(textViewMessageCreate, getMyLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 0, 0, 0));

                switchToFragment(context, 4);
                break;
            case 3:
                if (textViewMyTitle == null) {
                    textViewMyTitle = new TextView(context);
                    textViewMyTitle.setText("个人中心");
                    textViewMyTitle.setTextColor(getResources().getColor(R.color.textcolor_bar_first_unselect));
                    textViewMyTitle.setTextSize(20);
                    textViewMyTitle.setGravity(Gravity.CENTER);
                }

                if (imageViewMySetting == null) {
                    imageViewMySetting = new ImageView(context);
                    imageViewMySetting.setImageResource(R.drawable.icon_tab_my_titlebar_setting);
                    imageViewMySetting.setScaleType(ImageView.ScaleType.CENTER);
                    imageViewMySetting.setBackgroundResource(R.drawable.press_bg);
                    setMyTag(imageViewMySetting, "action", ACTION_MY_SETTING + "", "我的设置");
                    imageViewMySetting.setOnClickListener(this);
                }
                if (imageViewMyBlank == null) {
                    imageViewMyBlank = new ImageView(context);
                    imageViewMyBlank.setImageResource(R.drawable.icon_blank_42);
                }
                //imageViewMySetting.setPadding(20, 20, 20, 20);
                addView(imageViewMyBlank, getMyLayoutParams(topbarHeight, topbarHeight, 0, 0, 0));
                addView(textViewMyTitle, getMyLayoutParams(0, LayoutParams.MATCH_PARENT, 1, 0, 0));
                addView(imageViewMySetting, getMyLayoutParams(topbarHeight, topbarHeight, 0, 0, 0));

                switchToFragment(context, 5);
                break;

            default:
                break;
        }

    }

    private void setMyTag(View v, String type, String which, String describe) {
        v.setTag(R.id.tagkey_type, type);
        v.setTag(R.id.tagkey_which, which);
        v.setTag(R.id.tagkey_describe, describe);
    }

    private LinearLayout.LayoutParams getMyLayoutParams(int width, int height,
                                                        int weight, int marginTop, int marginBottom) {
        LinearLayout.LayoutParams layoutParams = new LayoutParams(width,
                height, weight);
        layoutParams.setMargins(0, marginTop, 0, marginBottom);
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }

    private LinearLayout.LayoutParams getMyLayoutParams(int width, int height,
                                                        int weight, int marginTop, int marginBottom, int marginLeft, int marginRight) {
        LinearLayout.LayoutParams layoutParams = new LayoutParams(width,
                height, weight);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }

    private void switchToFragment(Context context, int whichFragment) {
        if (whichFragment < 3) {
            switchToChild(context, whichFragment);
        }
        if (topBarClickListener != null)
            topBarClickListener.topbarSwitchToFragment(whichFragment);
    }

    private void switchToChild(Context context, int whichChild) {
        if (currentParent != 0) return;
        currentChild = whichChild;
        int unselectTextColor = context.getResources().getColor(
                R.color.textcolor_bar_first_unselect);
        int selectTextColor = context.getResources().getColor(
                R.color.textcolor_bar_first_select);

        textViewHomeHot.setTextColor(unselectTextColor);
        textViewHomeCamp.setTextColor(unselectTextColor);
        textViewHomeConcern.setTextColor(unselectTextColor);
        switch (whichChild) {
            case 0:
                textViewHomeHot.setTextColor(selectTextColor);
                break;
            case 1:
                textViewHomeConcern.setTextColor(selectTextColor);
                break;
            case 2:
                textViewHomeCamp.setTextColor(selectTextColor);
                break;
            default:
                break;
        }

    }

    public interface OnTopBarClickListener {
        public void topbarSwitchToFragment(int whichFragment);

        public void topbarHomeSearchClick();

        public void topbarCircleSearchClick();

        public void topbarCircleCreateClick();

        public void topbarMessageCreateClick();


    }

    public void setOnTopBarClickListener(OnTopBarClickListener tc) {
        this.topBarClickListener = tc;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub


        String type = (String) v.getTag(R.id.tagkey_type);
        if (type.equals("fragment")) {
            String whichFragment = (String) v.getTag(R.id.tagkey_which);
            switchToFragment(context, Integer.valueOf(whichFragment).intValue());
            Log.e("TopbarSwitchToFragment", "" + Integer.valueOf(whichFragment).intValue());
        } else if (type.equals("action")) {
            int whichAction = Integer.parseInt((String) v.getTag(R.id.tagkey_which));
            if (topBarClickListener != null) {
                switch (whichAction) {
                    case ACTION_HOME_SEARCH:
                        topBarClickListener.topbarHomeSearchClick();
                        break;
                    case ACTION_CIRCLE_CREATE:
                        topBarClickListener.topbarCircleCreateClick();
                        break;
                    case ACTION_CIRCLE_SEARCH:
                        topBarClickListener.topbarCircleSearchClick();
                        break;
                    case ACTION_MESSAGE_CREATE:
                        topBarClickListener.topbarMessageCreateClick();
                        break;


                }
            }


        }


//        switch ((Integer) v.getTag()) {
//            case 0:
//                switchToChild(context, 0);
//                if (topBarClickListener != null)
//                    topBarClickListener.topbarP1HotClick();
//                break;
//            case 1:
//                switchToChild(context, 1);
//                if (topBarClickListener != null)
//                    topBarClickListener.topbarP1ConcernClick();
//                break;
//            case 2:
//                switchToChild(context, 2);
//                if (topBarClickListener != null)
//                    topBarClickListener.topbarP1CampClick();
//                break;
//            case 3:
//                if (topBarClickListener != null)
//                    topBarClickListener.topbarP1SearchClick();
//                break;
//
//        }
    }

}
