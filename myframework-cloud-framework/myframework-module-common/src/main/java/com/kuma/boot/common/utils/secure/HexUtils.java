/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.common.utils.secure;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.jspecify.annotations.Nullable;

public class HexUtils {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final byte[] DIGITS_LOWER = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    private static final byte[] DIGITS_UPPER = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};

    public static byte[] encode(byte[] data) {
        return HexUtils.encode(data, true);
    }

    public static byte[] encode(byte[] data, boolean toLowerCase) {
        return HexUtils.encode(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    private static byte[] encode(byte[] data, byte[] digits) {
        int len = data.length;
        byte[] out = new byte[len << 1];
        int j = 0;
        for (int i = 0; i < len; ++i) {
            out[j++] = digits[(0xF0 & data[i]) >>> 4];
            out[j++] = digits[0xF & data[i]];
        }
        return out;
    }

    public static String encodeToString(byte[] data, boolean toLowerCase) {
        return new String(HexUtils.encode(data, toLowerCase), DEFAULT_CHARSET);
    }

    public static String encodeToString(byte[] data) {
        return new String(HexUtils.encode(data), DEFAULT_CHARSET);
    }

    public static @Nullable String encodeToString(@Nullable String data) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        return HexUtils.encodeToString(data.getBytes(DEFAULT_CHARSET));
    }

    public static @Nullable byte[] decode(@Nullable String data) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        return HexUtils.decode(data.getBytes(DEFAULT_CHARSET));
    }

    public static String decodeToString(byte[] data) {
        byte[] decodeBytes = HexUtils.decode(data);
        return new String(decodeBytes, DEFAULT_CHARSET);
    }

    public static @Nullable String decodeToString(@Nullable String data) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        return HexUtils.decodeToString(data.getBytes(DEFAULT_CHARSET));
    }

    public static byte[] decode(byte[] data) {
        int len = data.length;
        if ((len & 1) != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + len);
        }
        byte[] out = new byte[len >> 1];
        int i = 0;
        int j = 0;
        while (j < len) {
            int f = HexUtils.toDigit(data[j], j) << 4;
            f |= HexUtils.toDigit(data[++j], j);
            ++j;
            out[i] = (byte)(f & 0xFF);
            ++i;
        }
        return out;
    }

    private static int toDigit(byte b, int index) {
        int digit = Character.digit(b, 16);
        if (digit == -1) {
            throw new IllegalArgumentException("Illegal hexadecimal byte " + b + " at index " + index);
        }
        return digit;
    }
}

