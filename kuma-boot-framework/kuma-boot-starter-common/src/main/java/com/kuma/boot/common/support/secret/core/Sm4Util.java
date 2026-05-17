//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core;

import com.kuma.boot.common.support.secret.exception.SecretRuntimeException;
import com.kuma.boot.common.utils.lang.ByteUtils;

import java.security.Key;
import java.security.Security;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Sm4Util
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public final class Sm4Util {

    private static final String ENCODING = "UTF-8";
    private static final String ALGORITHM_NAME = "SM4";
    private static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";

    private Sm4Util() {
    }

    private static Cipher generateEcbCipher( String algorithmName, int mode, byte[] key ) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, "BC");
        Key sm4Key = new SecretKeySpec(key, "SM4");
        cipher.init(mode, sm4Key);
        return cipher;
    }

    public static String encryptEcb( String plainText, String hexKey ) {
        try {
//			String cipherText = "";
//			byte[] keyData = ByteUtils.fromHexString(hexKey);
//			byte[] srcData = plainText.getBytes("UTF-8");
//			byte[] cipherArray = encryptEcbPadding(keyData, srcData);
//			cipherText = ByteUtils.toHexString(cipherArray);
//			return cipherText;
            return null;
        } catch (Exception exception) {
            throw new SecretRuntimeException(exception);
        }
    }

    public static byte[] encryptEcbPadding( byte[] data, byte[] key ) {
        try {
            Cipher cipher = generateEcbCipher("SM4/ECB/PKCS5Padding", 1, key);
            return cipher.doFinal(data);
        } catch (Exception exception) {
            throw new SecretRuntimeException(exception);
        }
    }

    public static String decryptEcb( String cipherText, String hexKey ) {
        try {
//			String decryptStr = "";
//			byte[] keyData = ByteUtils.fromHexString(hexKey);
//			byte[] cipherData = ByteUtils.fromHexString(cipherText);
//			byte[] srcData = decryptEcbPadding(keyData, cipherData);
//			decryptStr = new String(srcData, "UTF-8");
//			return decryptStr;
            return null;
        } catch (Exception exception) {
            throw new SecretRuntimeException(exception);
        }
    }

    public static byte[] decryptEcbPadding( byte[] cipherText, byte[] key ) {
        try {
            Cipher cipher = generateEcbCipher("SM4/ECB/PKCS5Padding", 2, key);
            return cipher.doFinal(cipherText);
        } catch (Exception exception) {
            throw new SecretRuntimeException(exception);
        }
    }

    public static boolean verifyEcb( String hexKey, String cipherText, String plainText ) {
        try {
//			boolean flag = false;
//			byte[] keyData = ByteUtils.fromHexString(hexKey);
//			byte[] cipherData = ByteUtils.fromHexString(cipherText);
//			byte[] decryptData = decryptEcbPadding(keyData, cipherData);
//			byte[] srcData = plainText.getBytes("UTF-8");
//			flag = Arrays.equals(decryptData, srcData);
//			return flag;
            return false;
        } catch (Exception exception) {
            throw new SecretRuntimeException(exception);
        }
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
}
