package com.tedu.zhongzhao.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;

import com.tedu.zhongzhao.WorkApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片工具类
 * Created by huangyx on 2018/3/12.
 */
public class ImageUtil {


    /**
     * 获取Asset里图片
     *
     * @param path String
     * @return Bitmap
     */
    public static Bitmap getAssetImage(String path) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(path)) {
            InputStream is = null;
            try {
                is = WorkApplication.getApplication().getAssets().open(path);
                bitmap = BitmapFactory.decodeStream(is);
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
        }
        return bitmap;
    }

    /**
     * 获取Asset里图片
     *
     * @param path String
     * @return Bitmap
     */
    public static Bitmap getAssetImage(String path, int width, int height) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(path)) {
            InputStream is = null;
            try {
                is = WorkApplication.getApplication().getAssets().open(path);
                bitmap = BitmapFactory.decodeStream(is);
                float scx = width * 1f / bitmap.getWidth();
                float scy = height * 1f / bitmap.getHeight();
                float sc = Math.min(scx, scy);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * sc), (int) (bitmap.getHeight() * sc), true);
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
        }
        return bitmap;
    }

    /**
     * bitmap转换成drawable
     *
     * @param bitmap Bitmap
     * @return Drawable
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        bd.setTargetDensity(bitmap.getDensity());
        return new BitmapDrawable(bitmap);
    }

    /**
     * 压缩并保存图片(等比)
     *
     * @param src    源图片地址
     * @param dst    目标图片地址
     * @param width  目标图片宽
     * @param height 目标图片高
     * @return true/false
     */
    public static boolean compressAndSave(String src, String dst, int width, int height) {
        boolean result = false;
        int dstWidth = width, dstHeight = height;
        int degree = getPictureDegree(src);
        if ((degree / 90) % 2 != 0) {
            dstWidth = height;
            dstHeight = width;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(src, options);
        if (dstWidth == 0) {
            dstWidth = options.outWidth;
        }
        if (dstHeight == 0) {
            dstHeight = options.outHeight;
        }
        if (options.outWidth > dstWidth || options.outHeight > dstHeight) {
            float scaleX = options.outWidth / dstWidth * 1f;
            float scaleY = options.outHeight / dstHeight * 1f;
            float scale = Math.min(scaleX, scaleY);
            options.inSampleSize = Math.round(scale);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(src, options);
            if ((degree / 90) % 2 != 0) {
                // 对图片进行旋转
                Matrix matrix = new Matrix();
                matrix.postRotate(degree);
                Bitmap tmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                bitmap.recycle();
                bitmap = tmp;
                dstWidth = width;
                dstHeight = height;
            }
            scaleX = dstWidth * 1f / bitmap.getWidth();
            scaleY = dstHeight * 1f / bitmap.getHeight();
            scale = Math.min(scaleX, scaleY);
            // 因 inSampleSize是2的n次方，可能导致图片变小很多，所以再进行一次放大
            width = Math.round(scale * bitmap.getWidth());
            height = Math.round(scale * bitmap.getHeight());
            Bitmap dstBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
//            bitmap.recycle();
            result = saveBitmap(dstBitmap, dst);
        } else {
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;
            result = saveBitmap(BitmapFactory.decodeFile(src, options), dst);
        }
        return result;
    }

    /**
     * 保存bitmap
     *
     * @param bitmap  Bitmap
     * @param dstPath 目标地址
     * @return true/false
     */
    private static boolean saveBitmap(Bitmap bitmap, String dstPath) {
        File file = new File(dstPath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        boolean result = false;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        return result;
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度
     */
    public static int getPictureDegree(String path) {
        int degree = 0;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
        } catch (IOException ex) {
        }
        if (exifInterface == null)
            return degree;
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        return degree;
    }
}
