/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.xkzhangsan.time.utils.StringUtil
 */
package com.kuma.boot.common.support.secret.core;

import com.kuma.boot.common.support.secret.exception.SecretRuntimeException;
import com.xkzhangsan.time.utils.StringUtil;
import java.io.UnsupportedEncodingException;

public final class Base64Util {
    private static final char[] ALPHABET_CHARS;
    private static final byte[] CODES;

    private Base64Util() {
    }

    public static char[] encode(byte[] data) {
        char[] out = new char[(data.length + 2) / 3 * 4];
        int i = 0;
        int index = 0;
        while (i < data.length) {
            boolean quad = false;
            boolean trip = false;
            int val = 0xFF & data[i];
            val <<= 8;
            if (i + 1 < data.length) {
                val |= 0xFF & data[i + 1];
                trip = true;
            }
            val <<= 8;
            if (i + 2 < data.length) {
                val |= 0xFF & data[i + 2];
                quad = true;
            }
            out[index + 3] = ALPHABET_CHARS[quad ? val & 0x3F : 64];
            out[index + 2] = ALPHABET_CHARS[trip ? (val >>= 6) & 0x3F : 64];
            out[index + 1] = ALPHABET_CHARS[(val >>= 6) & 0x3F];
            out[index + 0] = ALPHABET_CHARS[(val >>= 6) & 0x3F];
            i += 3;
            index += 4;
        }
        return out;
    }

    public static String encodeToString(byte[] data) {
        char[] out = Base64Util.encode(data);
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : out) {
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static byte[] decode(char[] data) {
        int len = (data.length + 3) / 4 * 3;
        if (data.length > 0 && data[data.length - 1] == '=') {
            --len;
        }
        if (data.length > 1 && data[data.length - 2] == '=') {
            --len;
        }
        byte[] out = new byte[len];
        int shift = 0;
        int accum = 0;
        int index = 0;
        for (char datum : data) {
            byte value = CODES[datum & 0xFF];
            if (value < 0) continue;
            accum <<= 6;
            accum |= value;
            if ((shift += 6) < 8) continue;
            out[index++] = (byte)(accum >> (shift -= 8) & 0xFF);
        }
        if (index != out.length) {
            throw new SecretRuntimeException("miscalculated data length!");
        }
        return out;
    }

    public static char[] encode(String text) {
        if (StringUtil.isEmpty((String)text)) {
            return new char[0];
        }
        byte[] data = text.getBytes();
        return Base64Util.encode(data);
    }

    public static String encodeToString(String text) {
        if (StringUtil.isEmpty((String)text)) {
            return text;
        }
        char[] chars = Base64Util.encode(text);
        return new String(chars);
    }

    public static byte[] decode(String text) {
        if (StringUtil.isEmpty((String)text)) {
            return new byte[0];
        }
        char[] chars = text.toCharArray();
        return Base64Util.decode(chars);
    }

    public static String decodeToString(String text, String charset) {
        try {
            byte[] bytes = Base64Util.decode(text);
            return new String(bytes, charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new SecretRuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String text = "\u6211\u7231\u4e2d\u56fd!";
        String base64 = Base64Util.encodeToString(text);
        System.out.println(base64);
        String decode64 = Base64Util.decodeToString(base64, "UTF-8");
        System.out.println(decode64);
    }

    static {
        int i;
        ALPHABET_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
        CODES = new byte[256];
        for (i = 0; i < 256; ++i) {
            Base64Util.CODES[i] = -1;
        }
        for (i = 65; i <= 90; ++i) {
            Base64Util.CODES[i] = (byte)(i - 65);
        }
        for (i = 97; i <= 122; ++i) {
            Base64Util.CODES[i] = (byte)(26 + i - 97);
        }
        for (i = 48; i <= 57; ++i) {
            Base64Util.CODES[i] = (byte)(52 + i - 48);
        }
        Base64Util.CODES[43] = 62;
        Base64Util.CODES[47] = 63;
    }
}

