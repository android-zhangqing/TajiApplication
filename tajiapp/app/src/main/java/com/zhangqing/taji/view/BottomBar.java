package com.zhangqing.taji.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhangqing.taji.R;

public class BottomBar extends LinearLayout implements OnClickListener {


    private static final int[] imgs = {R.drawable.icon_tab_home_normal,
            R.drawable.icon_tab_circle_normal,
            R.drawable.icon_tab_message_normal, R.drawable.icon_tab_my_normal};
    private static final int[] imgs_down = {R.drawable.icon_tab_home_press,
            R.drawable.icon_tab_circle_press,
            R.drawable.icon_tab_message_press, R.drawable.icon_tab_my_press};

    private static final int[] layout_id = {
            R.id.main_tab_ry_home,
            R.id.main_tab_ry_circle,
            R.id.main_tab_ry_message,
            R.id.main_tab_ry_my
    };
    private static final int[] imageview_id = {
            R.id.main_tab_iv_home,
            R.id.main_tab_iv_circle,
            R.id.main_tab_iv_message,
            R.id.main_tab_iv_my

    };
    private static final int[] textview_id = {
            R.id.main_tab_tv_home,
            R.id.main_tab_tv_circle,
            R.id.main_tab_tv_message,
            R.id.main_tab_tv_my
    };
    private static final int[] pointview_id = {
            R.id.main_tab_point_home,
            R.id.main_tab_point_circle,
            R.id.main_tab_point_message,
            R.id.main_tab_point_my
    };


    private boolean points[] = {false, false, false, false};


    private int position;

    public int getPosition() {
        return position;
    }

    public Context context;
    private OnTabClickListener tabClickListener;

    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;


        // 取xml属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.mybar);
        position = Integer.parseInt(typedArray
                .getString(R.styleable.mybar_bottomposition));


        addView(inflate(getContext(), R.layout.view_bottom_bar, null), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //			if(i==0){
//				linearLayout.performClick();
//			}

        for (int i = 0; i < 4; i++) {
            findViewById(layout_id[i]).setOnClickListener(this);
        }
        findViewById(R.id.main_tab_ly_center).setOnClickListener(this);

        swichTo(position);


    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // Toast.makeText(context, v.getTag().toString(), 2000).show();

        for (int i = 0; i < 4; i++) {
            if (v.getId() == layout_id[i]) {
                swichTo(i);
                if (tabClickListener != null) {
                    tabClickListener.tabSwitchTo(i);
                }
                return;
            }
        }


        if (tabClickListener != null) {
            tabClickListener.tabClickPublishBtn();
        }
        return;

    }


    private void swichTo(int which) {
        position = which;
        //imageViews[2].setBackgroundResource(R.drawable.center_button_gradient);
        for (int i = 0; i < 4; i++) {
            if (i == which) {

                ((ImageView) findViewById(imageview_id[i])).setImageResource(imgs_down[i]);
                ((TextView) findViewById(textview_id[i])).setTextColor(context.getResources().getColor(
                        R.color.textcolor_bar_first_select));
            } else {
                ((ImageView) findViewById(imageview_id[i])).setImageResource(imgs[i]);
                ((TextView) findViewById(textview_id[i])).setTextColor(context.getResources().getColor(
                        R.color.textcolor_bar_first_unselect));
            }

        }
        invalidatePoints();
        //
        //
    }

    public void setPoints(int position, boolean isSelect) {
        points[position] = isSelect;
        invalidatePoints();
    }

    private void invalidatePoints() {
        for (int i = 0; i < 4; i++) {

            if (points[i]) {
                ImageView iv = (ImageView) findViewById(pointview_id[i]);
                iv.setVisibility(View.VISIBLE);
                if (i == position) {
                    iv.setImageResource(R.drawable.icon_tab_point_selected);
                } else {
                    iv.setImageResource(R.drawable.icon_tab_point_unselected);
                }
            } else {
                findViewById(pointview_id[i]).setVisibility(View.GONE);
            }
        }

    }


    public interface OnTabClickListener {
        public void tabSwitchTo(int whichParent);

        void tabClickPublishBtn();


    }

    public void setOnTabClickListener(OnTabClickListener tc) {
        this.tabClickListener = tc;
    }

}
//
//class ImageViewWithPoint extends ImageView {
//    Paint paint = new Paint();
//    Context context;
//    boolean withPoint = false;
//    int pointColor = R.color.bgcolor_bar_bottom_center;
//
//    public void setPointColor(int pointColor) {
//        this.pointColor = pointColor;
//    }
//
//    public void setWithPoint(boolean withPoint) {
//        this.withPoint = withPoint;
//    }
//
//    public ImageViewWithPoint(Context context) {
//        super(context);
//        this.context = context;
//        setScaleType(ScaleType.CENTER_INSIDE);
//        // TODO Auto-generated constructor stub
//    }
//
//    public ImageViewWithPoint(Context context, boolean withPoint) {
//        this(context);
//        this.withPoint = withPoint;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        // TODO Auto-generated method stub
//        super.onDraw(canvas);
//        if (withPoint == true) {
//            paint.setColor(context.getResources().getColor(pointColor));
//            paint.setAntiAlias(true);
//            canvas.drawCircle(getWidth() - 10, 10, 10, paint);
//
//            // Toast.makeText(context, getWidth()+"||"+getHeight(),
//            // 2000).show();
//        }
//
//    }
//
//}