package com.tedu.base.encrypt;


import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * DH 非对称密钥算法，并AES加、解密
 * Created by huangyx on 2018/6/23.
 */
public class DHCoder {

    private static final String DEFAULT_KEY = "111711874113112412161121811151016112116169512169101871114111711561111198";

    /**
     * 非对称加密密钥算法
     */
    private static final String KEY_ALGORITHM = "DH";
    /**
     * 本地密钥算法，即对称加密密钥算法
     * 可选DES、DESede或者AES
     */
    private static final String SELECT_ALGORITHM = "AES";
    /**
     * 密钥长度
     */
    private static final int KEY_SIZE = 512;

    /**
     * 初始化甲方密钥
     *
     * @return KeyPair 甲方密钥
     * @throws Exception
     */
    public static KeyPair initKey() throws Exception {
        //实例化密钥对生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥对生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //生成密钥对
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 初始化乙方密钥
     *
     * @param key 甲方公钥
     * @return KeyPair 乙方密钥
     * @throws Exception
     */
    public static KeyPair initKey(byte[] key) throws Exception {
        //解析甲方公钥
        //转换公钥材料
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //由甲方公钥构建乙方密钥
        DHParameterSpec dhParameterSpec = ((DHPublicKey) pubKey).getParams();
        //实例化密钥对生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥对生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //产生密钥对
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        //生成本地密钥
        SecretKey secretKey = new SecretKeySpec(key, SELECT_ALGORITHM);
        //数据加密
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        //生成本地密钥
        SecretKey secretKey = new SecretKeySpec(key, SELECT_ALGORITHM);
        //数据揭秘
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * 构建密钥
     *
     * @param publicKey  公钥
     * @param privateKey 私钥
     * @return byte[] 本地密钥
     * @throws Exception
     */
    public static byte[] getSecretKey(byte[] publicKey, byte[] privateKey) throws Exception {
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //初始化私钥
        //密钥材料转换
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        //产生私钥
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //实例化
        KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
        //初始化
        keyAgree.init(priKey);
        keyAgree.doPhase(pubKey, true);
        //生成本地密钥
        SecretKey secretKey = keyAgree.generateSecret(SELECT_ALGORITHM);
        return secretKey.getEncoded();
    }

    public static void main(String[] args) {
        try {
            //生成甲方密钥对
            KeyPair keyMap1 = initKey();
            byte[] publicKey1 = keyMap1.getPublic().getEncoded();
            byte[] privateKey1 = keyMap1.getPrivate().getEncoded();
            System.out.println("甲方公钥:\n" + Base64.encode(publicKey1));
            System.out.println("甲方私钥:\n" + Base64.encode(privateKey1));
            //由甲方公钥产生本地密钥对
            KeyPair keyMap2 = DHCoder.initKey(publicKey1);
            byte[] publicKey2 = keyMap2.getPublic().getEncoded();
            byte[] privateKey2 = keyMap2.getPrivate().getEncoded();
            System.out.println("乙方公钥:\n" + Base64.encode(publicKey2));
            System.out.println("乙方私钥:\n" + Base64.encode(privateKey2));
            byte[] key1 = DHCoder.getSecretKey(publicKey2, privateKey1);
            System.out.println("甲方本地密钥:\n" + Base64.encode(key1));
            byte[] key2 = DHCoder.getSecretKey(publicKey1, privateKey2);
            System.out.println("乙方本地密钥:\n" + Base64.encode(key2));

            System.out.println("===甲方向乙方发送加密数据===");
            String input1 = "求知若饥，虚心若愚。";
            System.out.println("原文:\n" + input1);
            System.out.println("---使用甲方本地密钥对数据进行加密---");
            //使用甲方本地密钥对数据加密
            byte[] encode1 = DHCoder.encrypt(input1.getBytes(), key1);
            System.out.println("加密:\n" + Base64.encode(encode1));
            System.out.println("---使用乙方本地密钥对数据库进行解密---");
            //使用乙方本地密钥对数据进行解密
            byte[] decode1 = DHCoder.decrypt(encode1, key2);
            String output1 = new String(decode1);
            System.out.println("解密:\n" + output1);

            System.out.println("/~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~..~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~/");
            initKey();
            System.out.println("===乙方向甲方发送加密数据===");
            String input2 = "好好学习，天天向上。";
            System.out.println("原文:\n" + input2);
            System.out.println("---使用乙方本地密钥对数据进行加密---");
            //使用乙方本地密钥对数据进行加密
            byte[] encode2 = DHCoder.encrypt(input2.getBytes(), key2);
            System.out.println("加密:\n" + Base64.encode(encode2));
            System.out.println("---使用甲方本地密钥对数据进行解密---");
            //使用甲方本地密钥对数据进行解密
            byte[] decode2 = DHCoder.decrypt(encode2, key1);
            String output2 = new String(decode2);
            System.out.println("解密:\n" + output2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String src) throws Exception {
        return null;
    }
}
