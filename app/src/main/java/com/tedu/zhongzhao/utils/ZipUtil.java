package com.tedu.zhongzhao.utils;

import android.text.TextUtils;

import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.bean.ImageSize;
import com.tedu.zhongzhao.net.RequestData;
import com.tedu.zhongzhao.net.UploadInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩文件工具类
 * Created by huangyx on 2018/5/13.
 */
public class ZipUtil {

    /**
     * 将单个文件及请求参数压缩到zip文件
     *
     * @param src         要放入压缩包中的内容
     * @param requestData 请求参数
     * @return 压缩后的文件路径
     */
    public static String zip(File src, RequestData<UploadInfo> requestData) {
        return zip(src, requestData, null);
    }

    /**
     * 将单个文件及请求参数压缩到zip文件
     *
     * @param src         要放入压缩包中的内容
     * @param requestData 请求参数
     * @param imageSize   限定图片高宽
     * @return 压缩后的文件路径
     */
    public static String zip(File src, RequestData<UploadInfo> requestData, ImageSize imageSize) {
        if (requestData == null || requestData.getData() == null) {
            return null;
        }
        if (src == null || !src.exists() && !src.isFile()) {
            return null;
        }
        File resizeFile = null;
        if (FileUtils.isImageFile(src)) {
            if (imageSize != null) {
                String suffix = "";
                int idx = src.getName().lastIndexOf(".");
                if (idx >= 0) {
                    suffix = src.getName().substring(idx + 1);
                }
                resizeFile = new File(WorkApplication.getImageDir(), System.currentTimeMillis() + "." + suffix);
                ImageUtil.compressAndSave(src.getAbsolutePath(), resizeFile.getAbsolutePath(), imageSize.getWidth(), imageSize.getHeight());
                src = resizeFile;
            }
        }
        /* 按要求，去掉压缩
        boolean result = true;
        String fileId = Md5Util.getMD5String(AndroidUtils.getUniqueId() + src).toUpperCase();
        String fileName = src.getName();
        // 压缩后的文件
        File dst = new File(WorkApplication.getMyCacheDir(), fileId + ".zip");
        // 文件输出流
        FileOutputStream fos = null;
        // zip输出流
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(dst);
            zos = new ZipOutputStream(fos);
            // 将文件放入zip中
            compress(zos, src, fileName);
            // 将RequestData中内容写入到zip中
            UploadInfo info = requestData.getData();
            info.setFileName(fileName);
            info.setFileId(fileId);
            info.setFileSize(String.valueOf(src.length()));
            compress(zos, *//*bos,*//* JsonUtil.toJson(requestData), fileId + ".json");
            zos.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (resizeFile != null) {
            FileUtils.deleteFile(resizeFile);
        }
        if (!result) {
            FileUtils.deleteFile(dst);
            dst = null;
            return null;
        }
        return dst.getAbsolutePath();*/
        return src.getAbsolutePath();
    }

    /**
     * 对文件进行进行压缩
     *
     * @param out        zip文件输出流
     * @param sourceFile 要压缩的文件
     * @param entryName  在压缩包中的文件名（或路径）
     * @throws Exception
     */
    public static void compress(ZipOutputStream out, File sourceFile, String entryName) throws IOException {
        //如果路径为目录（文件夹）
        if (sourceFile.isDirectory()) {
            //取出文件夹中的文件（或子文件夹）
            File[] flist = sourceFile.listFiles();
            //如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
            if (flist.length == 0) {
                out.putNextEntry(new ZipEntry(entryName + "/"));
            } else {//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
                for (int i = 0; i < flist.length; i++) {
                    compress(out, flist[i], entryName + "/" + flist[i].getName());
                }
            }
        } else {//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
            out.putNextEntry(new ZipEntry(entryName));
            FileInputStream fos = new FileInputStream(sourceFile);
            BufferedInputStream bis = new BufferedInputStream(fos);
            int tag;
            //将源文件写入到zip文件中
            while ((tag = bis.read()) != -1) {
                out.write(tag);
            }
            bis.close();
            fos.close();
        }
    }

    /**
     * 对文件进行进行压缩
     *
     * @param out       zip文件输出流
     * @param content   要压缩的文件内容
     * @param entryName 在压缩包中的文件名（或路径）
     * @throws Exception
     */
    public static void compress(ZipOutputStream out, String content, String entryName) throws IOException {
        //如果路径为目录（文件夹）
        out.putNextEntry(new ZipEntry(entryName));
        if (!TextUtils.isEmpty(content)) {
            out.write(content.getBytes(Charset.forName("UTF-8")));
        }
    }
}
