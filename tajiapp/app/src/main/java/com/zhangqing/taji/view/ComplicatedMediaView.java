package com.zhangqing.taji.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhangqing.taji.R;

/**
 * Created by zhangqing on 2016/4/8.
 */
public class ComplicatedMediaView extends LinearLayout {
    public static final int MODE_PIC_SINGLE = 1;
    public static final int MODE_PIC_MULTI = 2;
    public static final int MODE_PIC_MOVIE = 3;

    public ImageView picSingleImageView=null;

    private int currentMode = MODE_PIC_SINGLE;

    private View layoutPicSingle;
    private View layoutPicMulti;
    private View layoutMovie;

    private String mSinglePicUrl;


    public ComplicatedMediaView(Context context) {
        super(context);
        initView();
    }

    public ComplicatedMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ComplicatedMediaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        setBackgroundColor(Color.parseColor("#F1F1F1"));

        layoutPicSingle = LayoutInflater.from(getContext()).inflate(R.layout.view_media_pic_single, this, false);
        picSingleImageView= (ImageView) layoutPicSingle.findViewById(R.id.image);




        setCurrentMode(currentMode);
    }

    /**
     * 设置当前展示模式：PicSingle、PicMulti、Movie
     *
     * @param currentMode ComplicatedMediaView.MODE_**
     */
    public void setCurrentMode(int currentMode) {
        this.currentMode = currentMode;
        removeAllViews();
        switch (currentMode) {
            case MODE_PIC_SINGLE: {
                addView(layoutPicSingle);
                break;
            }
            case MODE_PIC_MULTI: {
                addView(layoutPicMulti);
                break;
            }
            case MODE_PIC_MOVIE: {
                addView(layoutMovie);
                break;
            }
        }
    }

    /**
     * singlePic专用，用于设置图片url
     * @param urlPic 图片url
     */
    public void setData(String urlPic){

    }

    /**
     * 获取当前所在模式：PicSingle、PicMulti、Movie
     * @return ComplicatedMediaView.MODE_**
     */
    public int getCurrentMode() {
        return currentMode;
    }
}
