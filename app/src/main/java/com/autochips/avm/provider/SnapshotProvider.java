package com.autochips.avm.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.goldze.mvvmhabit.utils.KLog;

public class SnapshotProvider extends ContentProvider {
    private static final String TAG = "SnapshotProvider";
    
    // 授权者，必须与AndroidManifest中声明的一致
    public static final String AUTHORITY = "space.syncore.avm.snapshot.provider";
    
    // 数据路径
    public static final String SNAPSHOT_PATH = "snapshot.dat";
    
    // 匹配码
    private static final int SNAPSHOT = 1;
    
    // URI匹配器
    private static final UriMatcher sUriMatcher;
    
    // 初始化URI匹配器
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, SNAPSHOT_PATH, SNAPSHOT);
    }
    
    // 快照文件存储路径
    private File mSnapshotFile;

    @Override
    public boolean onCreate() {
        // 初始化快照文件存储位置
        if (getContext() != null) {
            mSnapshotFile = new File(getContext().getFilesDir(), SNAPSHOT_PATH);
            KLog.d("mSnapshotFile is " + mSnapshotFile);
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "Querying URI: " + uri);
        
        // 验证URI
        if (sUriMatcher.match(uri) != SNAPSHOT) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        // 简单实现：返回快照文件的基本信息
        MatrixCursor cursor = new MatrixCursor(new String[]{"name", "size", "exists"});
        if (mSnapshotFile.exists()) {
            cursor.addRow(new Object[]{
                mSnapshotFile.getName(),
                mSnapshotFile.length(),
                mSnapshotFile.exists()
            });
        } else {
            cursor.addRow(new Object[]{SNAPSHOT_PATH, 0, false});
        }
        
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        // 根据URI返回MIME类型
        if (sUriMatcher.match(uri) == SNAPSHOT) {
            return "application/octet-stream";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "Inserting into URI: " + uri);
        
        if (sUriMatcher.match(uri) != SNAPSHOT) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        // 从ContentValues获取数据并写入文件
        byte[] data = values.getAsByteArray("data");
        if (data != null) {
            try (FileOutputStream fos = new FileOutputStream(mSnapshotFile)) {
                fos.write(data);
                // 通知数据变化
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse("content://" + AUTHORITY + "/" + SNAPSHOT_PATH);
            } catch (IOException e) {
                Log.e(TAG, "Failed to write snapshot data", e);
            }
        }
        
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "Deleting URI: " + uri);
        
        if (sUriMatcher.match(uri) != SNAPSHOT) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        // 删除快照文件
        if (mSnapshotFile.exists() && mSnapshotFile.delete()) {
            getContext().getContentResolver().notifyChange(uri, null);
            return 1; // 表示删除了1条记录
        }
        
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // 更新操作可以复用插入逻辑
        return insert(uri, values) != null ? 1 : 0;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Log.d(TAG, "Opening file for URI: " + uri + " with mode: " + mode);
        
        if (sUriMatcher.match(uri) != SNAPSHOT) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        // 处理文件打开模式
        int fileMode = 0;
        if (mode.contains("r")) {
            fileMode |= ParcelFileDescriptor.MODE_READ_ONLY;
        }
        if (mode.contains("w")) {
            fileMode |= ParcelFileDescriptor.MODE_WRITE_ONLY;
            fileMode |= ParcelFileDescriptor.MODE_CREATE;
            fileMode |= ParcelFileDescriptor.MODE_TRUNCATE;
        }
        
        return ParcelFileDescriptor.open(mSnapshotFile, fileMode);
    }
}
