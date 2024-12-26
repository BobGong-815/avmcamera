package com.autochips.avm.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.autochips.avm.service.AvmRuntime;

import me.goldze.mvvmhabit.utils.KLog;

public class AvmConstraintLayout extends ConstraintLayout {

    public AvmConstraintLayout(Context context) {
        super(context);
    }

    public AvmConstraintLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AvmConstraintLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) AvmRuntime.self().userTap();
        KLog.i("event.getAction:"+motionEvent.getAction()+"event x:"+ motionEvent.getX() +"event rx:"+ motionEvent.getRawX()
                + "event y:"+ motionEvent.getY() +"event ry:"+ motionEvent.getRawY());
        return super.onInterceptTouchEvent(motionEvent);
    }

}
