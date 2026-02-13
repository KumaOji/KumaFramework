/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.core;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public final class AesUtil {
    private static final String ALGORITHM = "AES";

    private AesUtil() {
    }

    public static byte[] encrypt(byte[] plainBytes, byte[] keyBytes) {
        try {
            SecretKey secretKey = AesUtil.getSecretKey(keyBytes);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(1, secretKey);
            return cipher.doFinal(plainBytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decrypt(byte[] cipherBytes, byte[] keyBytes) {
        try {
            SecretKey secretKey = AesUtil.getSecretKey(keyBytes);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(2, secretKey);
            return cipher.doFinal(cipherBytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static SecretKey getSecretKey(byte[] keySeed) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(keySeed);
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            generator.init(secureRandom);
            return generator.generateKey();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

