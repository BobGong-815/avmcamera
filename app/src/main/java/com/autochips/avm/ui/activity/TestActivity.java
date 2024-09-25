package com.autochips.avm.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;

import com.autochips.avm.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        getWindow().setAttributes(layoutParams);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    private void createWindowView() {
        Button btnView = new Button(getApplicationContext());
        btnView.setBackgroundResource(R.mipmap.ic_launcher);
        WindowManager windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        // 设置Window Type
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // 设置悬浮框不可触摸
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应
        params.format = PixelFormat.RGBA_8888;
        // 设置悬浮框的宽高
        params.width = 200;
        params.height = 200;
        params.gravity = Gravity.LEFT;
        params.x = 200;
        params.y = 000;
        // 设置悬浮框的Touch监听
        windowManager.addView(btnView, params);
    }

}