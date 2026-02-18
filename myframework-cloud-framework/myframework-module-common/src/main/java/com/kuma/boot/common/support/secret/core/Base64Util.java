//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core;

import com.kuma.boot.common.support.secret.exception.SecretRuntimeException;
import com.xkzhangsan.time.utils.StringUtil;

import java.io.UnsupportedEncodingException;

/**
 * Base64Util
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public final class Base64Util {

    private static final char[] ALPHABET_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    private static final byte[] CODES = new byte[256];

    private Base64Util() {
    }

    public static char[] encode( byte[] data ) {
        char[] out = new char[( data.length + 2 ) / 3 * 4];
        int i = 0;

        for (int index = 0; i < data.length; index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = 255 & data[i];
            val <<= 8;
            if (i + 1 < data.length) {
                val |= 255 & data[i + 1];
                trip = true;
            }

            val <<= 8;
            if (i + 2 < data.length) {
                val |= 255 & data[i + 2];
                quad = true;
            }

            out[index + 3] = ALPHABET_CHARS[quad ? val & 63 : 64];
            val >>= 6;
            out[index + 2] = ALPHABET_CHARS[trip ? val & 63 : 64];
            val >>= 6;
            out[index + 1] = ALPHABET_CHARS[val & 63];
            val >>= 6;
            out[index + 0] = ALPHABET_CHARS[val & 63];
            i += 3;
        }

        return out;
    }

    public static String encodeToString( byte[] data ) {
        char[] out = encode(data);
        StringBuilder stringBuilder = new StringBuilder();

        for (char c : out) {
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }

    public static byte[] decode( char[] data ) {
        int len = ( data.length + 3 ) / 4 * 3;
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
            int value = CODES[datum & 255];
            if (value >= 0) {
                accum <<= 6;
                shift += 6;
                accum |= value;
                if (shift >= 8) {
                    shift -= 8;
                    out[index++] = (byte) ( accum >> shift & 255 );
                }
            }
        }

        if (index != out.length) {
            throw new SecretRuntimeException("miscalculated data length!");
        } else {
            return out;
        }
    }

    public static char[] encode( String text ) {
        if (StringUtil.isEmpty(text)) {
            return new char[0];
        } else {
            byte[] data = text.getBytes();
            return encode(data);
        }
    }

    public static String encodeToString( String text ) {
        if (StringUtil.isEmpty(text)) {
            return text;
        } else {
            char[] chars = encode(text);
            return new String(chars);
        }
    }

    public static byte[] decode( String text ) {
        if (StringUtil.isEmpty(text)) {
            return new byte[0];
        } else {
            char[] chars = text.toCharArray();
            return decode(chars);
        }
    }

    public static String decodeToString( String text, String charset ) {
        try {
            byte[] bytes = decode(text);
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            throw new SecretRuntimeException(e);
        }
    }

    public static void main( String[] args ) {
        String text = "我爱中国!";
        String base64 = encodeToString(text);
        System.out.println(base64);
        String decode64 = decodeToString(base64, "UTF-8");
        System.out.println(decode64);
    }

    static {
        for (int i = 0; i < 256; ++i) {
            CODES[i] = -1;
        }

        for (int i = 65; i <= 90; ++i) {
            CODES[i] = (byte) ( i - 65 );
        }

        for (int i = 97; i <= 122; ++i) {
            CODES[i] = (byte) ( 26 + i - 97 );
        }

        for (int i = 48; i <= 57; ++i) {
            CODES[i] = (byte) ( 52 + i - 48 );
        }

        CODES[43] = 62;
        CODES[47] = 63;
    }
}
