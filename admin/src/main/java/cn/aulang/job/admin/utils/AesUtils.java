package cn.aulang.job.admin.utils;

import cn.hutool.crypto.SecureUtil;

import java.nio.charset.StandardCharsets;

/**
 * AES帮助类
 *
 * @author wulang
 */
public class AesUtils {

    public static String encrypt(String message, String key) {
        return SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8)).encryptHex(message);
    }

    public static String decrypt(String ciphertext, String key) {
        return SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8)).decryptStr(ciphertext);
    }
}
