package com.bim.sdk;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class FileUtils {

    private final static String TAG = "BIM";

    public static String saveFile(String content, Context context, String subDir, String filename) {
        String filePath = getFilePath(context, subDir, filename);
//        Log.d(TAG, "saveToFile : " + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        try {
            final FileOutputStream out = new FileOutputStream(file);
            out.write(content.getBytes());
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (final Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage());
        }

        return null;
    }

    public static String saveFile(byte[] content, int offset, int length, Context context, String subDir, String filename) {
        String filePath = getFilePath(context, subDir, filename);
        Log.d(TAG, "FileUtils saveToFile : " + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(content, offset, length);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (final Exception e) {
            Log.e(TAG, "Exception : " + e);
        }

        return null;
    }

    public static String saveFile(float[] content, int offset, int length, Context context, String subDir, String filename) {
        String filePath = getFilePath(context, subDir, filename);
        Log.d(TAG, "FileUtils float[] saveToFile : " + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            StringBuffer stringBuffer = new StringBuffer();
            FileOutputStream out = new FileOutputStream(file);
            for (int i=offset; i<length; i++) {
                stringBuffer.append(content[i]).append(",");
            }
            byte[] bytes = stringBuffer.toString().getBytes();
            out.write(bytes);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (final Exception e) {
            Log.e(TAG, "Exception : " + e);
        }

        return null;
    }

    /*
    public static String saveFile(byte[] content, int offset, int length, String filename) {
        final String root =
                Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "bim";
        final File myDir = new File(root);
        try {
            if (!myDir.exists()) myDir.createNewFile();
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage());
        }

        final File file = new File(myDir, filename);
        if (file.exists()) {
            file.delete();
        }
        try {
            final FileOutputStream out = new FileOutputStream(file);
            out.write(content, offset, length);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (final Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage());
        }

        return null;
    }

     */

    public static String getFilePath(Context context, String subDir, String filename) {
        return context.getExternalFilesDir(subDir).getAbsolutePath()+"/"+filename;
    }

    /*
    public static String getFilePath(String filename) {
        String root =
                Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "bim";
        return new File(root, filename).getAbsolutePath();
    }

     */

    public String getAlgorithm(Context context, String className, String methodName) {
        File directory = new File("");
        File fP = new File(directory.getAbsolutePath());
        for (File file : fP.listFiles()) {
            File optimizedDexOutputPath = context.getDir("temp", Context.MODE_PRIVATE);
            String filePath = file.getAbsolutePath();
            String optimizedPath = optimizedDexOutputPath.getAbsolutePath();
            DexClassLoader classLoader = new DexClassLoader(filePath, optimizedPath, null, context.getClassLoader());
            try {
                Class iClass = classLoader.loadClass(className);
                Constructor localConstructor = iClass.getConstructor(new Class[]{});
                Object obj = localConstructor.newInstance(new Object[]{});
                Method method = iClass.getDeclaredMethod(methodName, new Class[0]);
                method.setAccessible(true);
                String name = (String) method.invoke(obj);
                Log.w(TAG, "name：" + name);
                return name;
            } catch (Exception e) {
                Log.e(TAG, "解析jar包失败：" + e.getMessage());
            }
        }

        return "";
    }

    public static void copyFile(String from, String to) {
        try {
            FileInputStream fis = new FileInputStream(from);
            FileOutputStream fos = new FileOutputStream(to);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fis.close();
            fos.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 读取文件内容为二进制数组
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] read(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        byte[] data = inputStream2ByteArray(in);
        in.close();

        return data;
    }

    /**
     * 流转二进制数组
     *
     * @param in
     * @return
     * @throws IOException
     */
    static byte[] inputStream2ByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        byte[] bytes = out.toByteArray();
        out.close();
        return bytes;
    }

}
