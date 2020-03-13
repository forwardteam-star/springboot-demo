package com.darcytech.demo.nacos;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AESUtils {

    private static Logger logger = LoggerFactory.getLogger(AESUtils.class);
    private static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";
    // 此密钥长度必须为16位
    private static final String SECRET_KEY = "1234567890123456";

    private static SecretKeySpec getKey(String strKey) {
        byte[] arrBTmp = strKey.getBytes();
        byte[] arrB = new byte[16];

        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }

        return new SecretKeySpec(arrB, "AES");
    }

    /**
     * @param clearText 待加密明文
     * @return String 输出Base64加密后密文
     */
    public static String encrypt(String clearText) throws Exception {
        SecretKeySpec skeySpec = getKey(SECRET_KEY);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
        IvParameterSpec iv = new IvParameterSpec(SECRET_KEY.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(clearText.getBytes(Charsets.UTF_8));
        return new BASE64Encoder().encode(encrypted);
    }

    /**
     * @param cipherText 密文
     * @return String 输出解密之后明文
     */
    public static String decrypt(String cipherText) {
        try {
            SecretKeySpec skeySpec = getKey(SECRET_KEY);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            IvParameterSpec iv = new IvParameterSpec(SECRET_KEY.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(cipherText);

            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, Charsets.UTF_8);
        } catch (Exception e) {
            logger.error("decrypt failed :{}", e);
        }
        return null;
    }

}
