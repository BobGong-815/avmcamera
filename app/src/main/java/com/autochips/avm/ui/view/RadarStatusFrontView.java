package com.autochips.avm.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.autochips.avm.R;

/**
 * 雷达状态显示- 车头显示
 */
public class RadarStatusFrontView extends View {

    private Bitmap bmRadar1_red_1;
    private Bitmap bmRadar1_red_2;
    private Bitmap bmRadar1_red_3;

    private Bitmap bmRadar2_orange_1;
    private Bitmap bmRadar2_orange_2;
    private Bitmap bmRadar2_orange_3;

    private Bitmap bmRadar3_yellow_1;
    private Bitmap bmRadar3_yellow_2;
    private Bitmap bmRadar3_yellow_3;

    private Bitmap bmRadar4_green_1;
    private Bitmap bmRadar4_green_2;
    private Bitmap bmRadar4_green_3;
    private Paint paint = new Paint();

    public RadarStatusFrontView(Context context) {
        super(context);
        init();
    }

    public RadarStatusFrontView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarStatusFrontView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();



    }

    private void init() {
        // 初始化图片资源
        bmRadar1_red_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar1_red_1);
        bmRadar1_red_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar1_red_2);
        bmRadar1_red_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar1_red_3);

        bmRadar2_orange_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar2_orange_1);
        bmRadar2_orange_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar2_orange_2);
        bmRadar2_orange_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar2_orange_3);

        bmRadar3_yellow_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar3_yellow_1);
        bmRadar3_yellow_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar3_yellow_2);
        bmRadar3_yellow_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar3_yellow_3);

        bmRadar4_green_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar4_green_1);
        bmRadar4_green_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar4_green_2);
        bmRadar4_green_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar4_green_3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 获取 View 的宽度和高度
        int width = getWidth();
        int height = getHeight();

        // 绘制图片
        if (bmRadar1_red_1 != null) {
            float scaleX = (float) width / bmRadar1_red_1.getWidth();
            float scaleY = (float) height / bmRadar1_red_1.getHeight();
            float scale = Math.max(scaleX, scaleY);
            int scaledWidth = (int) (bmRadar1_red_1.getWidth() * scale);
            int scaledHeight = (int) (bmRadar1_red_1.getHeight() * scale);
            int left = 20;
            int top = 150 ;
            canvas.drawBitmap(bmRadar1_red_1, null, new Rect(0, 0, bmRadar1_red_1.getWidth(),  bmRadar1_red_1.getHeight()), paint);
            canvas.drawBitmap(bmRadar1_red_2, null, new Rect(0, 0, bmRadar1_red_2.getWidth(),  bmRadar1_red_2.getHeight()), paint);
            canvas.drawBitmap(bmRadar1_red_3, null, new Rect(0, 0, bmRadar1_red_2.getWidth(),  bmRadar1_red_3.getHeight()), paint);

            canvas.drawBitmap(bmRadar2_orange_1, null, new Rect(0, 0, bmRadar2_orange_1.getWidth(),  bmRadar2_orange_1.getHeight()), paint);
            canvas.drawBitmap(bmRadar2_orange_2, null, new Rect(0, 0, bmRadar2_orange_2.getWidth(),  bmRadar2_orange_2.getHeight()), paint);
            canvas.drawBitmap(bmRadar2_orange_3, null, new Rect(0, 0, bmRadar2_orange_3.getWidth(),  bmRadar2_orange_3.getHeight()), paint);

            canvas.drawBitmap(bmRadar3_yellow_1, null, new Rect(0, 0, bmRadar3_yellow_1.getWidth(),  bmRadar3_yellow_1.getHeight()), paint);
            canvas.drawBitmap(bmRadar3_yellow_2, null, new Rect(0, 0, bmRadar3_yellow_2.getWidth(),  bmRadar3_yellow_2.getHeight()), paint);
            canvas.drawBitmap(bmRadar3_yellow_3, null, new Rect(0, 0, bmRadar3_yellow_3.getWidth(),  bmRadar3_yellow_3.getHeight()), paint);

            canvas.drawBitmap(bmRadar4_green_1, null, new Rect(0, 0, bmRadar4_green_1.getWidth(),  bmRadar4_green_1.getHeight()), paint);
            canvas.drawBitmap(bmRadar4_green_2, null, new Rect(0, 0, bmRadar4_green_2.getWidth(),  bmRadar4_green_2.getHeight()), paint);
            canvas.drawBitmap(bmRadar4_green_3, null, new Rect(0, 0, bmRadar4_green_3.getWidth(),  bmRadar4_green_3.getHeight()), paint);

        }
    }
}
