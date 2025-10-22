package com.autochips.avm.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StorageUtil {

    private static StorageUtil self;
    private Context mContext;
    private HashMap<String, Integer> entries;

    public static synchronized StorageUtil self() {
        if (self == null) self = new StorageUtil();
        return self;
    }

    private StorageUtil() {
        entries = new HashMap<>();
    }

    public void init(Context context) {
        mContext = context;
    }

    public void writeString(String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences("global", 0);
        sp.edit().putString(key, value).commit();
    }

    public String readString(String key) {
        SharedPreferences sp = mContext.getSharedPreferences("global", 0);
        return sp.getString(key, "");
    }

    public void writeLong(String key, Long value) {
        SharedPreferences sp = mContext.getSharedPreferences("global", 0);
        sp.edit().putLong(key, value).commit();
    }

    public Long readLong(String key) {
        SharedPreferences sp = mContext.getSharedPreferences("global", 0);
        return sp.getLong(key, 0);
    }

    public void writeInt(String key, Integer value) {
        SharedPreferences sp = mContext.getSharedPreferences("global", 0);
        sp.edit().putInt(key, value).commit();
    }

    public Integer readInt(String key, int defValue) {
        SharedPreferences sp = mContext.getSharedPreferences("global", 0);
        return sp.getInt(key, defValue);
    }

    public void writeBoolean(String key, boolean value) {
        SharedPreferences sp = mContext.getSharedPreferences("global", 0);
        sp.edit().putBoolean(key, value).commit();
    }

    public Boolean readBoolean(String key, boolean defValue) {
        SharedPreferences sp = mContext.getSharedPreferences("global", 0);
        return sp.getBoolean(key, defValue);
    }

    public int readKeyValue(String key) {
        Integer integer = entries.get(key);
        if (integer != null) {
            return integer.intValue();
        }

        return -1;
    }

    public void writeKeyValue(String key, Integer value) {
        entries.put(key, value);
    }

}
