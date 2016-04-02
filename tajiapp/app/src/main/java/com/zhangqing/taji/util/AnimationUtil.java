package com.zhangqing.taji.util;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
    public static void startAnimation2(ImageView imageView, int moveX, int moveY, Animator.AnimatorListener l) {

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
        yxBouncer.addListener(l);
        yxBouncer.start();
    }
}
