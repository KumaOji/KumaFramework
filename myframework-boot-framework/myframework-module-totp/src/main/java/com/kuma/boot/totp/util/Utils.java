/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package com.kuma.boot.totp.util;

import org.apache.commons.codec.binary.Base64;

public class Utils {
    private static Base64 base64Codec = new Base64();

    private Utils() {
    }

    public static String getDataUriForImage(byte[] data, String mimeType) {
        String encodedData = new String(base64Codec.encode(data));
        return String.format("data:%s;base64,%s", mimeType, encodedData);
    }
}

