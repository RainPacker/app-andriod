package com.tedu.base.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HMac工具类
 * Created by huangyx on 2018/5/15.
 */
public class HMacMD5 {
    private static final String KEY = "111711874113112412161121811151016112116169512169101871114111711561111198";

    /**
     * 计算参数的md5信息
     *
     * @param str 待处理的字节数组
     * @return md5摘要信息
     * @throws NoSuchAlgorithmException
     */
    private static byte[] md5(byte[] str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str);
        return md.digest();
    }

    /**
     * 将待加密数据data，通过密钥key，使用hmac-md5算法进行加密，然后返回加密结果。
     * 参照rfc2104 HMAC算法介绍实现。
     *
     * @param key  密钥
     * @param data 待加密数据
     * @return 加密结果
     * @throws NoSuchAlgorithmException
     * @author 尹星
     */
    public static byte[] getHmacMd5Bytes(byte[] key, byte[] data) throws NoSuchAlgorithmException {
        /**
         * HmacMd5 calculation formula: H(K XOR opad, H(K XOR ipad, text))
         * HmacMd5 计算公式：H(K XOR opad, H(K XOR ipad, text))
         * H代表hash算法，本类中使用MD5算法，K代表密钥，text代表要加密的数据
         * ipad为0x36，opad为0x5C。
         */
        int length = 64;
        byte[] ipad = new byte[length];
        byte[] opad = new byte[length];
        for (int i = 0; i < length; i++) {
            ipad[i] = 0x36;
            opad[i] = 0x5C;
        }
        byte[] actualKey = key; //Actual key.
        byte[] keyArr = new byte[length]; //Key bytes of 64 bytes length
        /*
        * 如果密钥长度，大于64字节，就使用哈希算法，计算其摘要，作为真正的密钥。
        */
        if (key.length > length) {
            actualKey = md5(key);
        }
        for (int i = 0; i < actualKey.length; i++) {
            keyArr[i] = actualKey[i];
        }
        /*
        * 如果密钥长度不足64字节，就使用0x00补齐到64字节。
        */
        if (actualKey.length < length) {
            for (int i = actualKey.length; i < keyArr.length; i++)
                keyArr[i] = 0x00;
        }

        /*
        * 使用密钥和ipad进行异或运算。
        */
        byte[] kIpadXorResult = new byte[length];
        for (int i = 0; i < length; i++) {
            kIpadXorResult[i] = (byte) (keyArr[i] ^ ipad[i]);
        }

        /*
        * 将待加密数据追加到K XOR ipad计算结果后面。
        */
        byte[] firstAppendResult = new byte[kIpadXorResult.length + data.length];
        for (int i = 0; i < kIpadXorResult.length; i++) {
            firstAppendResult[i] = kIpadXorResult[i];
        }
        for (int i = 0; i < data.length; i++) {
            firstAppendResult[i + keyArr.length] = data[i];
        }

        /*
        * 使用哈希算法计算上面结果的摘要。
        */
        byte[] firstHashResult = md5(firstAppendResult);

        /*
        * 使用密钥和opad进行异或运算。
        */
        byte[] kOpadXorResult = new byte[length];
        for (int i = 0; i < length; i++) {
            kOpadXorResult[i] = (byte) (keyArr[i] ^ opad[i]);
        }

        /*
        * 将H(K XOR ipad, text)结果追加到K XOR opad结果后面
        */
        byte[] secondAppendResult = new byte[kOpadXorResult.length + firstHashResult.length];
        for (int i = 0; i < kOpadXorResult.length; i++) {
            secondAppendResult[i] = kOpadXorResult[i];
        }
        for (int i = 0; i < firstHashResult.length; i++) {
            secondAppendResult[i + keyArr.length] = firstHashResult[i];
        }

        /*
        * 对上面的数据进行哈希运算。
        */
        byte[] hmacMd5Bytes = md5(secondAppendResult);
        return hmacMd5Bytes;
    }

    public static String getHmacMd5Str(String data) {
        String result = "";
        try {
            byte[] keyByte = KEY.getBytes("UTF-8");
            byte[] dataByte = data.getBytes("UTF-8");
            byte[] hmacMd5Byte = getHmacMd5Bytes(keyByte, dataByte);
            StringBuffer md5StrBuff = new StringBuffer();
            for (int i = 0; i < hmacMd5Byte.length; i++) {
                if (Integer.toHexString(0xFF & hmacMd5Byte[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & hmacMd5Byte[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & hmacMd5Byte[i]));
                }
            }
            result = md5StrBuff.toString().toUpperCase();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
