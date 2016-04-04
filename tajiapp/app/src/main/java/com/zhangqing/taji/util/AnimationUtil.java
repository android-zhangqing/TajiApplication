package com.zhangqing.taji.util;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

/**
 * Created by zhangqing on 2016/4/2.
 */
public class AnimationUtil {

    /**
     * 要start 动画的那张图片的ImageView
     *
     * @param imageView
     */
    public static void startAnimationByMyself(ImageView imageView, int moveX, int moveY, Animator.AnimatorListener l) {

        //暂时不考虑moveX=0
        int count = moveX > 0 ? moveX : -moveX;
        Keyframe[] keyframes = new Keyframe[count];
        float keyStep = 1f / (float) count;

        //X坐标动画
        float key = keyStep;
        for (int i = 0; i < count; ++i) {
            keyframes[i] = Keyframe.ofFloat(key, moveX > 0 ? i + 1 : -i - 1);
            key += keyStep;
        }
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofKeyframe("translationX", keyframes);

        //Y坐标动画
        key = keyStep;
        float a = (float) moveY / (float) (moveX * moveX);

        for (int i = 0; i < count; ++i) {
            float y = a * i * i;
            keyframes[i] = Keyframe.ofFloat(key, y);
            key += keyStep;
        }

        PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("translationY", keyframes);
        ObjectAnimator yxBouncer = ObjectAnimator.ofPropertyValuesHolder(imageView, pvhY, pvhX).setDuration(1000);
        //yxBouncer.setInterpolator(new BounceInterpolator());弹跳多次
        //yxBouncer.setInterpolator(new AccelerateDecelerateInterpolator());
        yxBouncer.setInterpolator(new AccelerateDecelerateInterpolator());
        if (l != null)
            yxBouncer.addListener(l);
        yxBouncer.start();
    }

    /**
     * 上移淡出
     *
     * @param imageView
     */
    public static void startAnimationFadeOut(View imageView, Animator.AnimatorListener l) {

        //暂时不考虑moveX=0
        int count = 300;
        Keyframe[] keyframes = new Keyframe[count];
        float keyStep = 1f / (float) count;

        //Y坐标动画
        float key = keyStep;

        for (int i = 0; i < count; ++i) {
            keyframes[i] = Keyframe.ofFloat(key, i-10);
            key += keyStep;
        }
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("translationY", keyframes);

        //淡化动画
        key = keyStep;
        for (int i = 0; i < count; ++i) {
            keyframes[i] = Keyframe.ofFloat(key, 1 - key);
            key += keyStep;
        }
        PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofKeyframe("alpha", keyframes);


        ObjectAnimator yxBouncer = ObjectAnimator.ofPropertyValuesHolder(imageView, pvhY, pvhAlpha).setDuration(1000);
        //yxBouncer.setInterpolator(new BounceInterpolator());弹跳多次
        //yxBouncer.setInterpolator(new AccelerateDecelerateInterpolator());
        yxBouncer.setInterpolator(new AccelerateInterpolator());
        if (l != null)
            yxBouncer.addListener(l);
        yxBouncer.start();
    }


    /**
     * 弹跳进入
     *
     * @param imageView
     */
    public static void startAnimationJumpIn(View imageView, int moveX, int moveY, Animator.AnimatorListener l) {

        //暂时不考虑moveX=0
        int count = moveX > 0 ? moveX : -moveX;
        Keyframe[] keyframes = new Keyframe[count];
        float keyStep = 1f / (float) count;

        //X坐标动画
        float key = keyStep;
        for (int i = 0; i < count; ++i) {
            keyframes[i] = Keyframe.ofFloat(key, moveX > 0 ? i + 1 : -i - 1);
            key += keyStep;
        }
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofKeyframe("translationX", keyframes);

        //Y坐标动画
        key = keyStep;
        float a = (float) moveY / (float) (moveX * moveX);

        for (int i = 0; i < count; ++i) {
            float y = a * i * i;
            keyframes[i] = Keyframe.ofFloat(key, y);
            key += keyStep;
        }
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("translationY", keyframes);

        //淡化动画
        key = keyStep;
        for (int i = 0; i < count; ++i) {
            float y = a * i * i;
            keyframes[i] = Keyframe.ofFloat(key, key);
            key += keyStep;
        }
        PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofKeyframe("alpha", keyframes);

        //缩放动画
        key = keyStep;
        for (int i = 0; i < count; ++i) {
            float y = a * i * i;
            keyframes[i] = Keyframe.ofFloat(key, key);
            key += keyStep;
        }
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe("scaleX", keyframes);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe("scaleY", keyframes);

        ObjectAnimator yxBouncer = ObjectAnimator.ofPropertyValuesHolder(imageView, pvhY, pvhX, pvhAlpha, pvhScaleX, pvhScaleY).setDuration(1000);
        yxBouncer.setInterpolator(new AccelerateInterpolator());//弹跳多次
//        yxBouncer.setRepeatMode(ObjectAnimator.REVERSE);
//        yxBouncer.setRepeatCount(1);
        //yxBouncer.setInterpolator(new AccelerateDecelerateInterpolator());
        //yxBouncer.setInterpolator(new AccelerateDecelerateInterpolator());
        if (l != null)
            yxBouncer.addListener(l);
        yxBouncer.start();
    }


    /**
     * 要start 动画的那张图片的ImageView
     *
     * @param imageView
     */
    public static void startAnimationTopLeft(final View view, int start_top, int end_top, int start_left, int end_left, Animator.AnimatorListener l) {

        //暂时不考虑moveX=0
        int count = Math.abs(end_left - start_left);
        Keyframe[] keyframes = new Keyframe[count];
        float keyStep = 1f / (float) count;

        //X坐标动画
        float key = keyStep;
        for (int i = 0; i < count; ++i) {
            keyframes[i] = Keyframe.ofFloat(key, start_left < end_left ? start_left + i : -start_left - i);
            key += keyStep;
        }
        PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofKeyframe("left", keyframes);

        //Y坐标动画
        key = keyStep;
        float a = (float) (start_top - end_top) / (float) ((start_left - end_left) * (start_left - end_left));


        for (int i = 0; i < count; ++i) {
            float y = start_top - a * (start_left - i) * (start_left - i);
            keyframes[i] = Keyframe.ofFloat(key, y);
            key += keyStep;
        }
        PropertyValuesHolder pvhTop = PropertyValuesHolder.ofKeyframe("top", keyframes);

        ObjectAnimator yxBouncer = ObjectAnimator.ofPropertyValuesHolder(view, pvhLeft, pvhTop).setDuration(1000);
        //yxBouncer.setInterpolator(new BounceInterpolator());弹跳多次
        //yxBouncer.setInterpolator(new AccelerateDecelerateInterpolator());
        yxBouncer.setInterpolator(new AccelerateDecelerateInterpolator());
        // yxBouncer.addListener(l);
        yxBouncer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.postInvalidate();
                view.invalidate();
            }
        });
        yxBouncer.start();
    }
}
