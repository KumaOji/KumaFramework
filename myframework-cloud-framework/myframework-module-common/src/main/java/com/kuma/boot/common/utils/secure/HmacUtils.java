/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.secure;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.nio.charset.StandardCharsets;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtils {
    private static final String HMAC_MD5 = "HmacMD5";
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String HMAC_SHA224 = "HmacSHA224";
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HMAC_SHA384 = "HmacSHA384";
    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final String DEFAULT_KEY = "MkI3I1YlFOFr57YL";

    private HmacUtils() {
    }

    public static String encryptHmacMD5(String text) {
        return HmacUtils.encryptHmac(text, DEFAULT_KEY, HMAC_MD5);
    }

    public static String encryptHmacSHA1(String text) {
        return HmacUtils.encryptHmacSHA1(text, DEFAULT_KEY);
    }

    public static String encryptHmacSHA1(String text, String key) {
        return HmacUtils.encryptHmac(text, key, HMAC_SHA1);
    }

    public static String encryptHmacSHA224(String text) {
        return HmacUtils.encryptHmacSHA224(text, DEFAULT_KEY);
    }

    public static String encryptHmacSHA224(String text, String key) {
        return HmacUtils.encryptHmac(text, key, HMAC_SHA224);
    }

    public static String encryptHmacSHA256(String text) {
        return HmacUtils.encryptHmacSHA256(text, DEFAULT_KEY);
    }

    public static String encryptHmacSHA256(String text, String key) {
        return HmacUtils.encryptHmac(text, key, HMAC_SHA256);
    }

    public static String encryptHmacSHA384(String text) {
        return HmacUtils.encryptHmacSHA384(text, DEFAULT_KEY);
    }

    public static String encryptHmacSHA384(String text, String key) {
        return HmacUtils.encryptHmac(text, key, HMAC_SHA384);
    }

    public static String encryptHmacSHA512(String text) {
        return HmacUtils.encryptHmacSHA512(text, DEFAULT_KEY);
    }

    public static String encryptHmacSHA512(String text, String key) {
        return HmacUtils.encryptHmac(text, key, HMAC_SHA512);
    }

    private static String encryptHmac(String text, String key, String type) {
        if (StringUtils.isEmpty(text) || StringUtils.isEmpty(key) || StringUtils.isEmpty(type)) {
            return null;
        }
        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] dataBytes = text.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, type);
            Mac mac = Mac.getInstance(type);
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(dataBytes);
            return HmacUtils.encodeHex(bytes, false);
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return null;
        }
    }

    public static byte[] getHmacKey(String type) {
        byte[] bytes = new byte[]{};
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(type);
            SecretKey secretKey = keyGenerator.generateKey();
            bytes = secretKey.getEncoded();
            return bytes;
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return bytes;
        }
    }

    private static String encodeHex(byte[] bytes, boolean toUpperCase) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(Integer.toHexString(b & 0xFF | 0x100), 1, 3);
        }
        if (toUpperCase) {
            return stringBuilder.toString().toUpperCase();
        }
        return stringBuilder.toString();
    }
}

