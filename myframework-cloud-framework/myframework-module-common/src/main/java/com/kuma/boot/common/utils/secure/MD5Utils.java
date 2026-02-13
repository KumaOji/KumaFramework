/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.secure;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    private static final String MD5 = "MD5";

    private MD5Utils() {
    }

    public static String encrypt(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        char[] hexs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            MessageDigest messageDigest = MessageDigest.getInstance(MD5);
            messageDigest.update(bytes);
            byte[] md = messageDigest.digest();
            int j = md.length;
            char[] chars = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                chars[k++] = hexs[byte0 >>> 4 & 0xF];
                chars[k++] = hexs[byte0 & 0xF];
            }
            return String.valueOf(chars);
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return null;
        }
    }

    private static String md5(String src, String charset) {
        byte[] md5Bytes;
        MessageDigest md5;
        StringBuilder hexValue = new StringBuilder(32);
        try {
            md5 = MessageDigest.getInstance(MD5);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        byte[] byteArray = new byte[]{};
        try {
            byteArray = src.getBytes(charset);
        }
        catch (UnsupportedEncodingException e) {
            LogUtils.error(e.getMessage(), e);
        }
        for (byte md5Byte : md5Bytes = md5.digest(byteArray)) {
            int val = md5Byte & 0xFF;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static String md5(String src) {
        return MD5Utils.md5(src, StandardCharsets.UTF_8.name());
    }
}

