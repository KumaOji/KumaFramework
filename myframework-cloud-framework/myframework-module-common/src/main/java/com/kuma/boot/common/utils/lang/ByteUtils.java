/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.lang;

public final class ByteUtils {
    private ByteUtils() {
    }

    public static int byteToInt(byte[] bytes) {
        return (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | bytes[3] & 0xFF;
    }

    public byte[] intToByte(int num) {
        byte[] bytes = new byte[]{(byte)(num >> 24 & 0xFF), (byte)(num >> 16 & 0xFF), (byte)(num >> 8 & 0xFF), (byte)(num & 0xFF)};
        return bytes;
    }
}

