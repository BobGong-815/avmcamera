package com.autochips.avm.util;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.autochips.avm.app.AvmApp;

import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;

public class Utils {

  /*  public static boolean isActivityOnTop(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> tasks = am.getAppTasks();
        if (!tasks.isEmpty()) {
            try {
                ActivityManager.RecentTaskInfo info = tasks.get(0).getTaskInfo();
                if (info.baseIntent.getComponent().getPackageName().equals(context.getPackageName())) {
                    // 当前应用的Activity处于顶部
                    return true;
                }
            } catch (Exception e) {
                // 处理异常
            }
        }
        return false;
    }*/

    public static void restartApp() {
        Intent intent = AvmApp.getInstance().getPackageManager().getLaunchIntentForPackage(AvmApp.getInstance().getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        AvmApp.getInstance().startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }


    /**
     * Activity是否在前台
     * @param context
     * @return
     */
    public static boolean isOnForground(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        if(appProcessInfoList == null){
            return false;
        }

        String packageName = context.getPackageName();
        for(ActivityManager.RunningAppProcessInfo processInfo : appProcessInfoList){
            if(processInfo.processName.equals(packageName) && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ){
                return true;
            }
        }
        return false;
    }


    public boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;

        String serviceClassName = serviceClass.getName();
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (runningServices == null) return false;

        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (serviceClassName.equals(service.service.getClassName())) {
                return service.foreground;
            }
        }

        return false;
    }

}
