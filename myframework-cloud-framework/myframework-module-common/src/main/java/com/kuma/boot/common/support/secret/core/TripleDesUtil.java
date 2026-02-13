/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.core;

import com.kuma.boot.common.support.secret.exception.SecretRuntimeException;
import java.io.UnsupportedEncodingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class TripleDesUtil {
    private static final String ALGORITHM = "DESede";

    private TripleDesUtil() {
    }

    public static byte[] encrypt(byte[] plainBytes, byte[] keyBytes) {
        try {
            SecretKeySpec deskey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(1, deskey);
            return c1.doFinal(plainBytes);
        }
        catch (Exception e1) {
            throw new SecurityException(e1);
        }
    }

    public static byte[] encrypt(String plainText, byte[] keyBytes) {
        return TripleDesUtil.encrypt(keyBytes, plainText.getBytes());
    }

    public static byte[] decrypt(byte[] secretBytes, byte[] keyBytes) {
        try {
            SecretKeySpec deskey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(2, deskey);
            return c1.doFinal(secretBytes);
        }
        catch (Exception e1) {
            throw new SecretRuntimeException(e1);
        }
    }

    public static String decryptToString(byte[] secretBytes, byte[] keyBytes, String charsetName) {
        try {
            byte[] bytes = TripleDesUtil.decrypt(keyBytes, secretBytes);
            return new String(bytes, charsetName);
        }
        catch (UnsupportedEncodingException e) {
            throw new SecretRuntimeException(e);
        }
    }

    public static String decryptToString(byte[] secretBytes, byte[] keyBytes) {
        return TripleDesUtil.decryptToString(keyBytes, secretBytes, "UTF-8");
    }
}

