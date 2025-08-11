package com.autochips.avm.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SnapshotAccessor {
    private static final String TAG = "SnapshotAccessor";
    // 对应ContentProvider的URI
    public static final Uri SNAPSHOT_URI = Uri.parse("content://space.syncore.avm.snapshot.provider/snapshot.dat");

    private final ContentResolver contentResolver;

    public SnapshotAccessor(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    /**
     * 读取快照数据
     */
    public String readSnapshot() {
        Cursor cursor = null;
        try {
            // 查询快照数据
            cursor = contentResolver.query(
                    SNAPSHOT_URI,
                    null,  // 返回所有列
                    null,  // 无选择条件
                    null,  // 无选择参数
                    null   // 无排序
            );

            if (cursor != null && cursor.moveToFirst()) {
                // 假设数据存储在"content"列中
                int columnIndex = cursor.getColumnIndex("content");
                if (columnIndex != -1) {
                    return cursor.getString(columnIndex);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "读取快照失败: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 写入快照数据
     */
    public boolean writeSnapshot(String data) {
        try {
            ContentValues values = new ContentValues();
            values.put("content", data);
            values.put("timestamp", System.currentTimeMillis());

            // 插入或更新数据
            Uri resultUri = contentResolver.insert(SNAPSHOT_URI, values);
            return resultUri != null;
        } catch (Exception e) {
            Log.e(TAG, "写入快照失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 通过文件流方式读取快照（适用于大文件）
     */
    public byte[] readSnapshotAsFile() {
        ParcelFileDescriptor pfd = null;
        FileInputStream fis = null;
        try {
            // 打开文件描述符
            pfd = contentResolver.openFileDescriptor(SNAPSHOT_URI, "r");
            if (pfd == null) return null;

            fis = new FileInputStream(pfd.getFileDescriptor());
            byte[] buffer = new byte[fis.available()];
            int bytesRead = fis.read(buffer);
            return bytesRead > 0 ? buffer : null;
        } catch (IOException e) {
            Log.e(TAG, "文件流读取失败: " + e.getMessage());
            return null;
        } finally {
            try {
                if (fis != null) fis.close();
                if (pfd != null) pfd.close();
            } catch (IOException e) {
                // 忽略关闭异常
            }
        }
    }

    /**
     * 通过文件流方式写入快照（适用于大文件）
     */
    public boolean writeSnapshotAsFile(byte[] data) {
        ParcelFileDescriptor pfd = null;
        FileOutputStream fos = null;
        try {
            pfd = contentResolver.openFileDescriptor(SNAPSHOT_URI, "w");
            if (pfd == null) return false;

            fos = new FileOutputStream(pfd.getFileDescriptor());
            fos.write(data);
            fos.flush();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "文件流写入失败: " + e.getMessage());
            return false;
        } finally {
            try {
                if (fos != null) fos.close();
                if (pfd != null) pfd.close();
            } catch (IOException e) {
                // 忽略关闭异常
            }
        }
    }

    /**
     * 删除快照数据
     */
    public boolean deleteSnapshot() {
        try {
            int rowsDeleted = contentResolver.delete(
                    SNAPSHOT_URI,
                    null,  // 无选择条件
                    null   // 无选择参数
            );
            return rowsDeleted > 0;
        } catch (Exception e) {
            Log.e(TAG, "删除快照失败: " + e.getMessage());
            return false;
        }
    }
}
