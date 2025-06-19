package com.easemob.livedemo.utils;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptor {
    // GCM建议的IV长度是12字节（96位）
    private static final int IV_LENGTH = 12;
    // 认证标签长度，通常为128位
    private static final int AUTH_TAG_LENGTH = 128;

    // 加密方法（与iOS的encryptWithAES功能一致）
    public static String encrypt(String data, SecretKeySpec secretKey) {
        try {
            // 2. 初始化加密器（使用相同的AES/GCM/NoPadding模式）
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // 3. 使用12字节IV（与iOS默认行为一致）
            // 注意：iOS的sealedBox会自动生成IV，我们需要在Android端模拟相同行为
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);

            // 4. 使用128位认证标签（与iOS一致）
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            // 5. 加密数据
            byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));

            // 6. 组合IV + 加密数据 + 认证标签（与iOS的sealedBox.combined结构一致）
            // 结构：IV(12) + 加密数据 + 认证标签(16)
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            // 7. Base64编码返回（与iOS一致）
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // 解密
    public static String decrypt(String encryptedText, SecretKeySpec secretKey) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            // 提取IV
            byte[] iv = new byte[IV_LENGTH];
            byte[] cipherText = new byte[combined.length - IV_LENGTH];

            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(AUTH_TAG_LENGTH, iv);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] decryptedData = cipher.doFinal(cipherText);
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}