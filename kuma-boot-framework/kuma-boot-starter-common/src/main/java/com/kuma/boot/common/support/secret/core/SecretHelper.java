//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core;


import com.kuma.boot.common.support.secret.api.Secret;
import com.kuma.boot.common.support.secret.core.secret.Secrets;

/**
 * SecretHelper
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public final class SecretHelper {

    private static final String FOO_PWD = "123456";

    private SecretHelper() {
    }

    public static String encrypt( Secret secret, String key, String plain ) {
        return SecretBs.newInstance().key(key.getBytes()).secret(secret).encryptToHexString(plain);
    }

    public static String encrypt( String key, String plain ) {
        return encrypt(Secrets.aes(), key, plain);
    }

    public static String encrypt( String plain ) {
        return encrypt("123456", plain);
    }

    public static String decrypt( Secret secret, String key, String hexString ) {
        byte[] bytes = HexUtil.hexStringToByte(hexString);
        return SecretBs.newInstance().key(key.getBytes()).secret(secret).decryptToString(bytes);
    }

    public static String decrypt( String key, String hexString ) {
        return decrypt(Secrets.aes(), key, hexString);
    }

    public static String decrypt( String hexString ) {
        return decrypt("123456", hexString);
    }
}
