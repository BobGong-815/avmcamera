package com.autochips.avm.util;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;

import java.lang.reflect.Field;

import me.goldze.mvvmhabit.utils.KLog;

public class RearviewToast {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static RearviewToast instance;
    private TextView text;
    private Context mContext;
    private View mView;


    public static RearviewToast getInstance() {
        if (instance == null)
            instance = new RearviewToast();
        return instance;
    }

    public RearviewToast() {
    }

    public void init(View view) {
        this.mView = view;
        mContext = view.getContext();
        text = (TextView) view.findViewById(R.id.toast_text);
        text.setText("");
        mView.setVisibility(View.GONE);
    }

    public void showToast(String message) {
        if (TextUtils.isEmpty(message)) {
            KLog.d("text is null or  message is empty !");
            mView.setVisibility(View.GONE);
            return;
        }
        text.setText(message);
        mView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages("toastShow");
        mHandler.postDelayed(() -> {
            mView.setVisibility(View.GONE);
        }, "toastShow", 3 * 1000);

    }

    public void uiMode(int uiMode) {
        switch (uiMode) {
            case UiModeManager.MODE_NIGHT_YES:
                mView.setBackgroundResource(R.drawable.shape_toast_bg);
                text.setTextColor(mContext.getResources().getColor(R.color.white));
                KLog.e("黑夜模式");
                break;
            case UiModeManager.MODE_NIGHT_NO:
                KLog.e("白天模式");
                mView.setBackgroundResource(R.drawable.shape_toast_bg_day);
                text.setTextColor(mContext.getResources().getColor(R.color.test_color_day));
                break;
        }
    }

    public void cancelToast() {

    }

    @SuppressLint("SoonBlockedPrivateApi")
    public static boolean isToastShowing(Toast toast) {
        if (toast == null) {
            return false;
        }
        try {
            // 反射方法获取mTN
            Field mTNField = Toast.class.getDeclaredField("mTN");
            mTNField.setAccessible(true);
            Object mTN = mTNField.get(toast);

            // 反射方法获取mNextView
            Field mNextViewField = mTN.getClass().getDeclaredField("mNextView");
            KLog.d("是否在显示mNextViewField:" + mNextViewField);
            mNextViewField.setAccessible(true);
            View mNextView = (View) mNextViewField.get(mTN);

            // 如果mNextView不为空，则Toast正在准备显示
            return mNextView != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
