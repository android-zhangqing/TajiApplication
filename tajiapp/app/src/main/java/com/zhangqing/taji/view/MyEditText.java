package com.zhangqing.taji.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.zhangqing.taji.R;

import java.lang.reflect.Field;

public class MyEditText extends EditText {
    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void insertDrawable(String id) {

        int resourceId = R.drawable.ic_launcher;
        try {
            Field field = R.drawable.class.getDeclaredField("emoji_" + id);
            resourceId = Integer.parseInt(field.get(null).toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        String describe = "[emoji_" + id + "]";
        final SpannableString ss = new SpannableString(describe);
        //得到drawable对象，即所要插入的图片
        Drawable d = getResources().getDrawable(resourceId);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        //用这个drawable对象代替字符串easy
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        //包括0但是不包括"easy".length()即：4。[0,4)。值得注意的是当我们复制这个图片的时候，实际是复制了"easy"这个字符串。
        ss.setSpan(span, 0, describe.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        Log.e("insertDrawable", getSelectionStart() + "|" + getSelectionEnd());
        append(ss,getSelectionStart(),getSelectionStart()+1);
        setSelection(0);

    }
}