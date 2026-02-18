//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * AesUtil
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public final class AesUtil {

    private static final String ALGORITHM = "AES";

    private AesUtil() {
    }

    public static byte[] encrypt( byte[] plainBytes, byte[] keyBytes ) {
        try {
            SecretKey secretKey = getSecretKey(keyBytes);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(1, secretKey);
            return cipher.doFinal(plainBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decrypt( byte[] cipherBytes, byte[] keyBytes ) {
        try {
            SecretKey secretKey = getSecretKey(keyBytes);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, secretKey);
            return cipher.doFinal(cipherBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static SecretKey getSecretKey( byte[] keySeed ) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(keySeed);
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(secureRandom);
            return generator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
