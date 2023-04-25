package com.tedu.base.encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;


public class Md5Util {
	/**
	 * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
	 */
	private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	/**
	 * 生成字符串的md5校验值
	 * 
	 * @param s 字符串
	 * @return 字符串的MD5
	 */
	public static String getMD5String(String s) {
		try {
			return getMD5String(s.getBytes());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return s;
	}

	/**
	 * 生成字节流的md5校验值
	 * 
	 * @param bytes 字节数组
	 * @return 字节数组生成的MD5
	 */
    public static String getMD5String(byte[] bytes) {
		try {
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			messagedigest.update(bytes);
			return bufferToHex(messagedigest.digest());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return Arrays.toString(bytes);
	}

	/**
	 * 判断字符串的md5校验码是否与一个已知的md5码相匹配
	 * 
	 * @param str
	 *            要校验的字符串
	 * @param md5PwdStr
	 *            已知的md5校验码
	 * @return 是否匹配
	 */
    public static boolean checkPassword(String str, String md5PwdStr) {
		String s = getMD5String(str);
		return s.equals(md5PwdStr);
	}

    public static String getFileMD5String(String filePath) {
		File file = new File(filePath);
		return getFileMD5String(file);
	}

	/**
	 * 生成文件的md5校验值
	 * 
	 * @param file 传入的文件
	 * @return  文件的MD5
	 */
    public static String getFileMD5String(File file) {
		try {
			InputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int numRead;
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				messagedigest.update(buffer, 0, numRead);
			}
			fis.close();
			return bufferToHex(messagedigest.digest());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 判断文件的md5校验码是否与一个已知的md5码相匹配
	 * 
	 * @param file
	 *            要校验的文件
	 * @param md5PwdStr
	 *            已知的md5校验码
	 * @return 是否匹配
	 */
    public static boolean checkPassword(File file, String md5PwdStr) {
		String s = getFileMD5String(file);
		return s.equals(md5PwdStr);
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringBuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringBuffer);
		}
		return stringBuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringBuffer) {
		// 取字节中高 4 位的数字转换, >>>为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		// 取字节中低 4 位的数字转换
		char c1 = hexDigits[bt & 0xf];
		stringBuffer.append(c0);
		stringBuffer.append(c1);
	}

}
