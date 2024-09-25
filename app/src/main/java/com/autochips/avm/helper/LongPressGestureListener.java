package com.autochips.avm.helper;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class LongPressGestureListener extends  GestureDetector.SimpleOnGestureListener{
    private static final long LONG_PRESS_TIME = 10000; // 长按时间10秒
    private final Handler handler = new Handler();
    private final Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            // 长按后的操作
            // 例如：
            // Toast.makeText(context, "长按10秒触发", Toast.LENGTH_SHORT).show();
        }
    };

    private View view;
    private Context context;

    public LongPressGestureListener(View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (e == null) {
            return false;
        }
        // 当按下时，启动一个延迟任务
        handler.postDelayed(longPressRunnable, LONG_PRESS_TIME);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // 当按下并且还没有移动或者释放时调用
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // 当触发单击事件时取消长按事件
        handler.removeCallbacks(longPressRunnable);
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // 当滑动时取消长按事件
        handler.removeCallbacks(longPressRunnable);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // 当长按事件触发时，不会执行此方法
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // 当滑动时取消长按事件
        handler.removeCallbacks(longPressRunnable);
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        // 当双击时取消长按事件
        handler.removeCallbacks(longPressRunnable);
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        // 当双击事件触发时取消长按事件
        handler.removeCallbacks(longPressRunnable);
        return false;
    }
}
