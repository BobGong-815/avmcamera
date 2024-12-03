package com.autochips.avm.util;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.autochips.avm.app.AvmApp;

import java.lang.reflect.Method;

import me.goldze.mvvmhabit.utils.KLog;

public class SystemProperties {

    private final static String TAG = "SystemProperties_AVM";

    public static String get(String key) {
//        Class<?> SysProp = null;
//        Method method = null;
//        String value = null;
//        try {
//            SysProp = Class.forName("android.os.SystemProperties");
//            method = SysProp.getMethod("get", String.class);
//            value = (String) method.invoke(null, key);
//            Log.i(TAG, "value：" + value);
//        } catch (Exception e) {
//            Log.e(TAG,"read SystemProperties error",e);
//        }
       String value = Settings.System.getString(AvmApp.getInstance().getContentResolver(),key);
        KLog.d(TAG,key +"AVM key获取属性值 String："+ value);
        return TextUtils.isEmpty(value) ? "0" : value;
    }

    public static String get(String key, String defaultValue) {
        Class<?> SysProp = null;
        Method method = null;
        String value = null;
        try {
            SysProp = Class.forName("android.os.SystemProperties");
            method = SysProp.getMethod("get", String.class, String.class);
            value = (String) method.invoke(null, key, defaultValue);
        } catch (Exception e) {
            Log.e(TAG,"read SystemProperties error",e);
        }
        return value;
    }

    private static int lastSettingRadarValue = -100;
    public static int getInt(String key, int defaultValue) {
        Class<?> SysProp = null;
        //        Method method = null;
        //        int value = 0;
        //        try {
        //            SysProp = Class.forName("android.os.SystemProperties");
        //            method = SysProp.getMethod("getInt", String.class, int.class);
        //            value = (Integer) method.invoke(null, key, defaultValue);
        //        } catch (Exception e) {
        //            Log.e(TAG,"read SystemProperties error",e);
        //        }
        int value = Settings.System.getInt(AvmApp.getInstance().getContentResolver(), key,defaultValue);
        if(!key.equals("settingRadarActivatedPanorama")) {
            KLog.d(TAG, key + "  key获取属性值：" + value);
        }else{
            if(lastSettingRadarValue != value){
                KLog.d(TAG, key + "  key获取属性值：" + value);
            }
            lastSettingRadarValue = value;
        }
        return value;
    }

    public static long getLong(String key, long defaultValue) {
        Class<?> SysProp = null;
        Method method = null;
        long value = 0;
        try {
            SysProp = Class.forName("android.os.SystemProperties");
            method = SysProp.getMethod("getLong", String.class, long.class);
            value = (Long) method.invoke(null, key, defaultValue);
        } catch (Exception e) {
            Log.e(TAG,"read SystemProperties error",e);
        }
        return value;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        Class<?> SysProp = null;
        Method method = null;
        boolean value = false;
        try {
            SysProp = Class.forName("android.os.SystemProperties");
            method = SysProp.getMethod("getBoolean", String.class, boolean.class);
            value = (Boolean) method.invoke(null, key, defaultValue);
        } catch (Exception e) {
            Log.e(TAG,"read SystemProperties error",e);
        }
        return value;
    }

    public static void set(String key, String value) {
//        Class<?> SysProp = null;
//        Method method = null;
//        try {
//            SysProp = Class.forName("android.os.SystemProperties");
//            method = SysProp.getMethod("set", String.class, String.class);
//            method.invoke(null, key, value);
//        } catch (Exception e) {
//            Log.e(TAG,"read SystemProperties error",e);
//        }

        Settings.System.putString(AvmApp.getInstance().getContentResolver(), key,value+"");
        KLog.d(TAG,key +" key系统设置 set ："+value);
    }

    public static void setGlobal(String key, int value) {
//        Class<?> SysProp = null;
//        Method method = null;
//        try {
//            SysProp = Class.forName("android.os.SystemProperties");
//            method = SysProp.getMethod("set", String.class, String.class);
//            method.invoke(null, key, value);
//        } catch (Exception e) {
//            Log.e(TAG,"read SystemProperties error",e);
//        }

        //Settings.System.putString(AvmApp.getInstance().getContentResolver(), key,value+"");
        Settings.Global.putInt(AvmApp.getInstance().getContentResolver(),key,value);
        KLog.d(TAG,key +" 状态 set ："+value);
    }

    public static int getGlobalInt(String key, int defVal) {
        return Settings.Global.getInt(AvmApp.getInstance().getContentResolver(), key, defVal);
    }

    public static void addChangeCallback(Runnable runnable) {
        Class<?> SysProp = null;
        Method method = null;
        try {
            SysProp = Class.forName("android.os.SystemProperties");
            method = SysProp.getMethod("addChangeCallback", Runnable.class);
            method.invoke(null, runnable);
        } catch (Exception e) {
            Log.e(TAG,"read SystemProperties error",e);
        }
    }
}