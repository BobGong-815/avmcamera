package com.autochips.avm.provider;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

public class SnapshotObserver extends ContentObserver {

    public SnapshotObserver() {
        super(new Handler(Looper.getMainLooper())); // 确保在主线程中处理更新
    }

    @Override
    public void onChange(boolean selfChange) {
        onChange(selfChange, null); // 调用下面的onChange方法，传递null以兼容老版本API
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        // 这里处理内容变化
        // uri参数可以用来确定具体是哪个数据项发生了变化
        super.onChange(selfChange, uri);
        // 在这里添加你的代码来响应内容变化，例如更新UI等
    }

}
