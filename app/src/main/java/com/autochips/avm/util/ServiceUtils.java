package com.autochips.avm.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.autochips.avm.app.AvmApp;
import com.autochips.avm.service.AvmService;

import me.goldze.mvvmhabit.utils.KLog;

public class ServiceUtils {

    /**
     * 判断服务是否起来
     * @param context
     * @param serviceClass
     * @return
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void  startCaptureService(Context context, Class<?> serviceClass){
        if (isServiceRunning(context,serviceClass)){
            KLog.d("AVM 服务已经起来");
            return;
        }
        Intent intentService =  new Intent(context, serviceClass);
        context.startService( intentService);
    }

    /**
     * 获取版本号
     * @return
     */
    public static String getVersionName() {
        KLog.d("getVersionName");
        String versionName = "";
        try {
            PackageManager packageManager = AvmApp.getInstance().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(AvmApp.getInstance().getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
