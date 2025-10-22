package com.autochips.avm.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.autochips.avm.app.AvmApp;
import com.autochips.avm.util.DataDefine;
import com.autochips.avm.util.GlobalSetting;

public class MContentObserver extends ContentObserver {
    private static final String TAG = "MContentObserver";
    private ContentResolver mContentResolver;

    // 构造方法：传入Handler（确保回调在主线程）
    public MContentObserver(Handler handler, ContentResolver contentResolver) {
        super(handler);
        this.mContentResolver = contentResolver;
    }

    // 当监听的设置项变化时回调
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        // 验证Uri是否为avm_state的Uri（避免无关回调）
        Log.d(TAG, "onChange : " + uri);
        if (uri.equals(Settings.Global.getUriFor(GlobalSetting.AVM_SETTING_EXIT_P))) {
            Log.d(TAG, GlobalSetting.AVM_SETTING_EXIT_P + " 发生变化，Uri: " + uri);
            // 获取最新值（根据实际类型选择getInt/getString等）
            int newValue = getSettingValue(GlobalSetting.AVM_SETTING_EXIT_P);
            Log.d(TAG, GlobalSetting.AVM_SETTING_EXIT_P + " 新值: " + newValue);
            // 在这里处理变化逻辑（例如发送广播、更新UI等）
            onSettingChanged(GlobalSetting.AVM_SETTING_EXIT_P, newValue);
        } else if (uri.equals(Settings.Global.getUriFor(GlobalSetting.AVM_SETTING_TURN_LIGHT_ACTIVATION))) {
            Log.d(TAG, GlobalSetting.AVM_SETTING_TURN_LIGHT_ACTIVATION + " 发生变化，Uri: " + uri);
            // 获取最新值（根据实际类型选择getInt/getString等）
            int newValue = getSettingValue(GlobalSetting.AVM_SETTING_TURN_LIGHT_ACTIVATION);
            Log.d(TAG, GlobalSetting.AVM_SETTING_TURN_LIGHT_ACTIVATION + " 新值: " + newValue);
            // 在这里处理变化逻辑（例如发送广播、更新UI等）
            onSettingChanged(GlobalSetting.AVM_SETTING_TURN_LIGHT_ACTIVATION, newValue);
        } else if (uri.equals(Settings.Global.getUriFor(GlobalSetting.AVM_SETTING_TRAJECTORY))) {
            Log.d(TAG, GlobalSetting.AVM_SETTING_TRAJECTORY + " 发生变化，Uri: " + uri);
            // 获取最新值（根据实际类型选择getInt/getString等）
            int newValue = getSettingValue(GlobalSetting.AVM_SETTING_TRAJECTORY);
            Log.d(TAG, GlobalSetting.AVM_SETTING_TRAJECTORY + " 新值: " + newValue);
            // 在这里处理变化逻辑（例如发送广播、更新UI等）
            onSettingChanged(GlobalSetting.AVM_SETTING_TRAJECTORY, newValue);
        } else if (uri.equals(Settings.Global.getUriFor(GlobalSetting.AVM_SETTING_TRANSPARENT_CHASSIS))) {
            Log.d(TAG, GlobalSetting.AVM_SETTING_TRANSPARENT_CHASSIS + " 发生变化，Uri: " + uri);
            // 获取最新值（根据实际类型选择getInt/getString等）
            int newValue = getSettingValue(GlobalSetting.AVM_SETTING_TRANSPARENT_CHASSIS);
            Log.d(TAG, GlobalSetting.AVM_SETTING_TRANSPARENT_CHASSIS + " 新值: " + newValue);
            // 在这里处理变化逻辑（例如发送广播、更新UI等）
            onSettingChanged(GlobalSetting.AVM_SETTING_TRANSPARENT_CHASSIS, newValue);
        } else if (uri.equals(Settings.Global.getUriFor(GlobalSetting.AVM_SETTING_RADAR_ACTIVATION))) {
            Log.d(TAG, GlobalSetting.AVM_SETTING_RADAR_ACTIVATION + " 发生变化，Uri: " + uri);
            // 获取最新值（根据实际类型选择getInt/getString等）
            int newValue = getSettingValue(GlobalSetting.AVM_SETTING_RADAR_ACTIVATION);
            Log.d(TAG, GlobalSetting.AVM_SETTING_RADAR_ACTIVATION + " 新值: " + newValue);
            // 在这里处理变化逻辑（例如发送广播、更新UI等）
            onSettingChanged(GlobalSetting.AVM_SETTING_RADAR_ACTIVATION, newValue);
        }
    }

    private int getSettingValue(String setting) {
        try {
            int value = Settings.Global.getInt(AvmApp.getInstance().getContentResolver(), setting);
            return value;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // 回调接口：供外部处理变化
    private OnSettingsChangedListener mListener;
    public interface OnSettingsChangedListener {
        void onChanged(String setting, int newValue);
    }
    public void setOnSettingsChangedListener(OnSettingsChangedListener listener) {
        this.mListener = listener;
    }
    private void onSettingChanged(String setting, int newValue) {
        if (mListener != null) {
            mListener.onChanged(setting, newValue);
        }
    }
}
