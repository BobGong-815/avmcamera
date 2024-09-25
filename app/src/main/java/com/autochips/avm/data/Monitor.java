package com.autochips.avm.data;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;

import com.autochips.avm.helper.CameraViewModelHelper;

public class Monitor {
    private final Handler mHandler;

    public Monitor() {
        HandlerThread test = new HandlerThread("Test");
        test.start();
        mHandler = new Handler(test.getLooper());
        mHandler.post(() -> {
            // 打断一次
            Looper.getMainLooper().setMessageLogging(new Printer() {
                @Override
                public void println(String x) {
                    if (">>>>> Dispatching to".startsWith(x)) {
                        // 消息开始
                       start();
                    } else {
                        // 消息结束
                       end();
                        //Log.w("Monitor","消息结束");
                    }
                }
            });
        });
    }

    public void start() {
        mHandler.postDelayed(mRunnable, 1000);
    }

    public void end() {
        mHandler.removeCallbacks(mRunnable);
    }

    private final Runnable mRunnable = () -> {
        Log.w("Monitor","发现主线程有耗时操作");
        CameraViewModelHelper.getInstance().dismissView(false, 0, "0");
        Thread.dumpStack();
        Looper.getMainLooper().getThread().getStackTrace();
    };
}
