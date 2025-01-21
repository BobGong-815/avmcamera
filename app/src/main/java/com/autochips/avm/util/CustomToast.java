package com.autochips.avm.util;

import android.app.UiModeManager;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;

import me.goldze.mvvmhabit.utils.KLog;

public class CustomToast {
    private static Toast toast;

    public static void showToast(String message) {
        LayoutInflater inflater = (LayoutInflater) AvmApp.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);
        LinearLayout layou = layout.findViewById(R.id.toast_bg);

        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        text.setText(message);
        UiModeManager uiModeManager = (UiModeManager) AvmApp.getInstance().getSystemService(Context.UI_MODE_SERVICE);
        int uiMode = uiModeManager.getNightMode();
        switch (uiMode) {
            case UiModeManager.MODE_NIGHT_YES:
                layou.setBackgroundResource(R.drawable.shape_toast_bg);
                text.setTextColor(AvmApp.getInstance().getResources().getColor(R.color.white));
                KLog.e("黑夜模式");
                break;
            case UiModeManager.MODE_NIGHT_NO:
                KLog.e("白天模式");
                layou.setBackgroundResource(R.drawable.shape_toast_bg_day);
                text.setTextColor(AvmApp.getInstance().getResources().getColor(R.color.test_color_day));
                break;
        }

        toast = new Toast(AvmApp.getInstance());
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100); // 设置位置
        toast.setDuration(Toast.LENGTH_SHORT); // 设置持续时间
        toast.setView(layout);
        toast.show();
    }

    public static  void cancelToast(){
        if(toast!=null){
            toast.cancel();
            toast=null;
        }
    }
}
