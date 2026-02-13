/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jce.provider.BouncyCastleProvider
 */
package com.kuma.boot.common.support.secret.core;

import com.kuma.boot.common.support.secret.exception.SecretRuntimeException;
import java.security.Provider;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class Sm4Util {
    private static final String ENCODING = "UTF-8";
    private static final String ALGORITHM_NAME = "SM4";
    private static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";

    private Sm4Util() {
    }

    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, "BC");
        SecretKeySpec sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    public static String encryptEcb(String plainText, String hexKey) {
        try {
            return null;
        }
        catch (Exception exception) {
            throw new SecretRuntimeException(exception);
        }
    }

    public static byte[] encryptEcbPadding(byte[] data, byte[] key) {
        try {
            Cipher cipher = Sm4Util.generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, 1, key);
            return cipher.doFinal(data);
        }
        catch (Exception exception) {
            throw new SecretRuntimeException(exception);
        }
    }

    public static String decryptEcb(String cipherText, String hexKey) {
        try {
            return null;
        }
        catch (Exception exception) {
            throw new SecretRuntimeException(exception);
        }
    }

    public static byte[] decryptEcbPadding(byte[] cipherText, byte[] key) {
        try {
            Cipher cipher = Sm4Util.generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, 2, key);
            return cipher.doFinal(cipherText);
        }
        catch (Exception exception) {
            throw new SecretRuntimeException(exception);
        }
    }

    public static boolean verifyEcb(String hexKey, String cipherText, String plainText) {
        try {
            return false;
        }
        catch (Exception exception) {
            throw new SecretRuntimeException(exception);
        }
    }

    static {
        Security.addProvider((Provider)new BouncyCastleProvider());
    }
}

