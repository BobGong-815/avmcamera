package com.autochips.avm.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.autochips.avm.util.ServiceUtils;


public class BootReceiver extends BroadcastReceiver {
    Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//            mHandler.postDelayed(()->
//                            ServiceUtils.startCaptureService(context,AvmService.class)
//                   ,3000 );
//
//        }
    }
}