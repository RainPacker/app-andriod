
package com.tedu.zhongzhao.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.tedu.zhongzhao.WorkApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件管理工具类
 */
public class FileUtils {

    private static final String NOMEDIA = ".nomedia";

    private static void createNomedia(File folder) throws IOException {
        File nomedia = new File(folder, NOMEDIA);
        if (!nomedia.exists()) {
            nomedia.createNewFile();
        }
    }

    public static void createFolder(File folder) throws IOException {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (!folder.exists() || !folder.isDirectory()) {
                folder.mkdirs();
                createNomedia(folder);
            }
        }
    }

    public static void createFolder(String path) throws IOException {
        createFolder(new File(path));
    }

    /**
     * 读取Assets文件
     *
     * @param path 文件地址
     * @return 读取结果
     */
    public static String readAssetFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        AssetManager am = WorkApplication.getApplication().getAssets();
        InputStream is = null;
        String result = null;
        try {
            is = am.open(path);
            result = streamToString(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 将输入流转换成字符串，按行读取，并添加上换行符"\n"
     *
     * @param is 输入流 InputStream
     * @return 字符串
     * @throws IOException
     */
    public static String streamToString(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }

        return sb.toString();
    }

    /**
     * 删除文件
     *
     * @param f
     */
    public static void deleteFile(File f) {
        if (f != null && f.exists()) {
            if (f.isDirectory()) {
                File[] files = f.listFiles();
                if (files != null && files.length > 0) {
                    for (File f1 : files) {
                        deleteFile(f1);
                    }
                }
            }
            f.delete();
        }
    }

    /**
     * 判断是否是图片
     *
     * @param file 文件
     * @return true/false
     */
    public static boolean isImageFile(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return options.outWidth > 0 && options.outHeight > 0;
    }
}
