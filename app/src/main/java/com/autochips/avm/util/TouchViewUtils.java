package com.autochips.avm.util;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import me.goldze.mvvmhabit.utils.KLog;

public class TouchViewUtils {
    public static void setTouchViewListener(String tag, View touchView, View viewBg) {
        KLog.v("tag is touch:"+tag);
        touchView.setOnTouchListener(new View.OnTouchListener() {
            private final Rect buttonRect = new Rect();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchView.getGlobalVisibleRect(buttonRect);
                        viewBg.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getRawX();
                        float moveY = event.getRawY();
                        // 判断是否移出按钮范围
                        if (!buttonRect.contains((int) moveX, (int) moveY)) {
                            viewBg.setVisibility(View.GONE);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    default:
                        viewBg.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });
    }
}
