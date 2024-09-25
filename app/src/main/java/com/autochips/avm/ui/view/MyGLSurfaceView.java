package com.autochips.avm.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

    private SurfaceHolder holder;

    public MyGLSurfaceView(Context context, AttributeSet attributes) {
        super(context, attributes);
        init();
    }

    public MyGLSurfaceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

//    @Override
//    public void run() {
//        Paint paint = new Paint();
//        paint.setColor(Color.BLUE);
//        Rect rect = new Rect(100, 100, 300, 300);
//        int k = 1;
//
//        while (running) {
//            Canvas c = null;
//            rect.bottom = 100+(++k)%300;
//            try {
//                c = holder.lockCanvas();
//                synchronized (holder) {
//                    // 绘制代码
//                    c.drawRect(rect, paint);
//                }
//            } finally {
//                if (c != null) {
//                    holder.unlockCanvasAndPost(c);
//                }
//            }
//
//            try {
//                Thread.sleep(30);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
