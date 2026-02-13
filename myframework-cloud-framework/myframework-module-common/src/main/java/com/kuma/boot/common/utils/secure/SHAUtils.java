/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.secure;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SHAUtils {
    private static final String SHA_1 = "SHA-1";
    private static final String SHA_224 = "SHA-224";
    private static final String SHA_256 = "SHA-256";
    private static final String SHA_384 = "SHA-384";
    private static final String SHA_512 = "SHA-512";

    private SHAUtils() {
    }

    public static String encrypt(String text) {
        return SHAUtils.encrypt(text, SHA_1);
    }

    public static String encrypt224(String text) {
        return SHAUtils.encrypt(text, SHA_224);
    }

    public static String encrypt256(String text) {
        return SHAUtils.encrypt(text, SHA_256);
    }

    public static String encrypt384(String text) {
        return SHAUtils.encrypt(text, SHA_384);
    }

    public static String encrypt512(String text) {
        return SHAUtils.encrypt(text, SHA_512);
    }

    private static String encrypt(String text, String algorithm) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(text.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = messageDigest.digest();
            return SHAUtils.bytes2Str(bytes);
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return null;
        }
    }

    private static String bytes2Str(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            String str = Integer.toHexString(bytes[i] & 0xFF);
            if (str.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }
}

