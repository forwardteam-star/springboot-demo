package com.darcytech.demo.nacos;

import com.google.common.base.Preconditions;

import lombok.extern.slf4j.Slf4j;

/**
 * nacos 加解密的方法
 */
@Slf4j
public class NacosDecryptUtils {
    private static final String ENCRYPT_PREFIX = "~~";

    public static String encrypt(String source) {
        Preconditions.checkNotNull(source);
        if (isEncrypt(source)) {
            log.info("source:{} has been encrypted ", source);
            return source;
        }
        try {
            String encryptText = AESUtils.encrypt(source);
            return ENCRYPT_PREFIX + encryptText;
        } catch (Exception e) {
            log.error("failed to encrypt text:{}", source, e);
            throw new RuntimeException("failed to encrypt text" + source, e);
        }
    }

    public static String decrypt(String source) {
        Preconditions.checkNotNull(source);
        if (isEncrypt(source)) {
            String realEncryptContent = source.substring(ENCRYPT_PREFIX.length());
            return AESUtils.decrypt(realEncryptContent);
        }
        throw new RuntimeException("不支持此种类型文本解密");
    }

    static boolean isEncrypt(String source) {
        if (source == null) {
            return false;
        }
        return source.startsWith(ENCRYPT_PREFIX);
    }

    public static void main(String[] args) {
        System.out.println(encrypt("TsRAScf1aTojt1"));
    }
}
