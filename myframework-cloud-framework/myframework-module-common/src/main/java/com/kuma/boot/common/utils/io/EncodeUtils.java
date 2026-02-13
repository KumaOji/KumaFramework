/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.io;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncodeUtils {
    public static String encode(String content) {
        return EncodeUtils.encode(content, "UTF-8");
    }

    public static String encode(String content, String charset) {
        try {
            return URLEncoder.encode(content, charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeUnicode(String string) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }
        int length = 6 * string.length();
        StringBuilder sb = new StringBuilder(length);
        sb.setLength(0);
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            sb.append("\\u");
            int j = c >>> 8;
            String tmp = Integer.toHexString(j);
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
            j = c & 0xFF;
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return new String(sb);
    }

    public static String decodeUnicode(String string) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }
        if (!string.contains("\\u")) {
            return string;
        }
        int actualLength = string.length() / 6;
        StringBuilder sb = new StringBuilder(actualLength);
        for (int i = 0; i <= string.length() - 6; i += 6) {
            String strTemp = string.substring(i, i + 6);
            String value = strTemp.substring(2);
            int c = 0;
            for (int j = 0; j < value.length(); ++j) {
                char tempChar = value.charAt(j);
                int t = 0;
                switch (tempChar) {
                    case 'a': {
                        t = 10;
                        break;
                    }
                    case 'b': {
                        t = 11;
                        break;
                    }
                    case 'c': {
                        t = 12;
                        break;
                    }
                    case 'd': {
                        t = 13;
                        break;
                    }
                    case 'e': {
                        t = 14;
                        break;
                    }
                    case 'f': {
                        t = 15;
                        break;
                    }
                    default: {
                        t = tempChar - 48;
                    }
                }
                c += t * (int)Math.pow(16.0, value.length() - j - 1);
            }
            sb.append((char)c);
        }
        return sb.toString();
    }
}

