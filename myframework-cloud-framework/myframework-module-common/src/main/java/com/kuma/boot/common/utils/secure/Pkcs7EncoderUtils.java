/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.secure;

import java.util.Arrays;

public class Pkcs7EncoderUtils {
    private static final int BLOCK_SIZE = 16;

    public static byte[] encode(byte[] src) {
        int count = src.length;
        int amountToPad = 16 - count % 16;
        byte pad = (byte)(amountToPad & 0xFF);
        byte[] pads = new byte[amountToPad];
        Arrays.fill(pads, pad);
        int length = count + amountToPad;
        byte[] dest = new byte[length];
        System.arraycopy(src, 0, dest, 0, count);
        System.arraycopy(pads, 0, dest, count, amountToPad);
        return dest;
    }

    public static byte[] decode(byte[] decrypted) {
        byte pad = decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 16) {
            pad = 0;
        }
        if (pad > 0) {
            return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
        }
        return decrypted;
    }
}

