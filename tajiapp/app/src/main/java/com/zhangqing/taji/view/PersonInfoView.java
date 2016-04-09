package com.zhangqing.taji.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zhangqing.taji.R;
import com.zhangqing.taji.base.PersonInfo;

import java.util.zip.Inflater;

/**
 * Created by zhangqing on 2016/4/9.
 */
public class PersonInfoView extends LinearLayout {
    PersonInfo mPersonInfo;
    ViewGroup mMainContainer;

    public PersonInfoView(Context context) {
        super(context);
    }

    public PersonInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PersonInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mMainContainer= (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.merge_person_detail, this, true);

        //addView(mMainContainer);
    }

    public void setPersonInfo(PersonInfo personInfo) {
        mPersonInfo = personInfo;
        invalidate();
    }


}
