package com.zhangqing.taji.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

    private Context context;
    Paint paint;
    Drawable lastDrawable;

    //当前调用该构造
    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
      //      Log.e("circleImageView", "context2 " + this.toString());
        }
        this.context = context;

        // TODO Auto-generated constructor stub
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Log.e("circleImageView", "context3 " + this.toString());
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public CircleImageView(Context context) {
        super(context);
        Log.e("circleImageView", "context1 " + this.toString());
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            super.onDraw(canvas);
            return;
        }
        //super.onDraw(canvas);
        // setLayoutParams(new LayoutParams(getHeight(), getHeight()));
        //Toast.makeText(context,getParent().toString()+"", Toast.LENGTH_SHORT).show();
//		if (allowedRedraw == false)
//			return;
//
       // Log.e("circleImageView", "onDraw " + this.toString());
        setScaleType(ScaleType.FIT_XY);
        Drawable drawable = getDrawable();

        if (null == drawable) {
            Log.e("circleImageView", "drawable null " + this.toString());
            return;
        }
      //  Log.e("circleImageView", "start " + this.toString());


        Bitmap bitmap = Bitmap.createBitmap(getHeight(), getHeight(), drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);

        Canvas srcCanvas = new Canvas(bitmap);

        // drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
        // drawable.getIntrinsicHeight());
        drawable.setBounds(0, 0, getHeight(), getHeight());

        drawable.draw(srcCanvas);

        float cx = getHeight() / 2;
        float cy = getHeight() / 2;

        float radius = getHeight() / 2;
//
//		Paint borderPaint = new Paint();
//		borderPaint.setAntiAlias(true);
//		borderPaint.setColor(Color.GREEN);
//
//		canvas.drawCircle(cx, cy, radius, borderPaint);

        // 画图

        if (paint == null || !(lastDrawable.equals(drawable))) {
            Log.e("circleImageView", "rePaint " + this.toString());
            lastDrawable = drawable;
            BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
                    TileMode.CLAMP);
            paint = new Paint();
            paint.setShader(shader);
            paint.setAntiAlias(true);
        }
        canvas.drawCircle(cx, cy, radius - 0, paint);



    }


}
