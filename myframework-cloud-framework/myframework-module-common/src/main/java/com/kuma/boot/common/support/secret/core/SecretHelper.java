/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.core;

import com.kuma.boot.common.support.secret.api.Secret;
import com.kuma.boot.common.support.secret.core.HexUtil;
import com.kuma.boot.common.support.secret.core.SecretBs;
import com.kuma.boot.common.support.secret.core.secret.Secrets;

public final class SecretHelper {
    private static final String FOO_PWD = "123456";

    private SecretHelper() {
    }

    public static String encrypt(Secret secret, String key, String plain) {
        return SecretBs.newInstance().key(key.getBytes()).secret(secret).encryptToHexString(plain);
    }

    public static String encrypt(String key, String plain) {
        return SecretHelper.encrypt(Secrets.aes(), key, plain);
    }

    public static String encrypt(String plain) {
        return SecretHelper.encrypt(FOO_PWD, plain);
    }

    public static String decrypt(Secret secret, String key, String hexString) {
        byte[] bytes = HexUtil.hexStringToByte(hexString);
        return SecretBs.newInstance().key(key.getBytes()).secret(secret).decryptToString(bytes);
    }

    public static String decrypt(String key, String hexString) {
        return SecretHelper.decrypt(Secrets.aes(), key, hexString);
    }

    public static String decrypt(String hexString) {
        return SecretHelper.decrypt(FOO_PWD, hexString);
    }
}

