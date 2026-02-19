/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.support.hash.api.Hash
 *  com.kuma.boot.common.support.hash.core.Hashes
 *  com.kuma.boot.common.support.secret.api.Secret
 *  com.kuma.boot.common.support.secret.core.secret.Secrets
 */
package com.kuma.boot.web.support.encryption.core.util;

import com.kuma.boot.common.support.hash.api.Hash;
import com.kuma.boot.common.support.hash.core.Hashes;
import com.kuma.boot.common.support.secret.api.Secret;
import com.kuma.boot.common.support.secret.core.secret.Secrets;
import com.kuma.boot.web.support.encryption.api.dto.resp.CommonEncryptResponse;
import com.kuma.boot.web.support.encryption.api.enums.EncryptTypeEnum;
import com.kuma.boot.web.support.encryption.core.bs.EncryptionLocalBs;

public class EncryptionLocalUtil {
    private static final String DEFAULT_SALT = "99886622";

    private EncryptionLocalUtil() {
    }

    public static CommonEncryptResponse addressEncrypt(String plainText, String salt) {
        return EncryptionLocalUtil.encrypt(EncryptTypeEnum.ADDRESS, plainText, salt);
    }

    public static CommonEncryptResponse addressEncrypt(String plainText) {
        return EncryptionLocalUtil.addressEncrypt(plainText, DEFAULT_SALT);
    }

    public static CommonEncryptResponse nameEncrypt(String plainText, String salt) {
        return EncryptionLocalUtil.encrypt(EncryptTypeEnum.NAME, plainText, salt);
    }

    public static CommonEncryptResponse nameEncrypt(String plainText) {
        return EncryptionLocalUtil.nameEncrypt(plainText, DEFAULT_SALT);
    }

    public static CommonEncryptResponse emailEncrypt(String plainText, String salt) {
        return EncryptionLocalUtil.encrypt(EncryptTypeEnum.EMAIL, plainText, salt);
    }

    public static CommonEncryptResponse emailEncrypt(String plainText) {
        return EncryptionLocalUtil.emailEncrypt(plainText, DEFAULT_SALT);
    }

    public static CommonEncryptResponse phoneEncrypt(String plainText, String salt) {
        return EncryptionLocalUtil.encrypt(EncryptTypeEnum.PHONE, plainText, salt);
    }

    public static CommonEncryptResponse phoneEncrypt(String plainText) {
        return EncryptionLocalUtil.phoneEncrypt(plainText, DEFAULT_SALT);
    }

    public static CommonEncryptResponse idCardEncrypt(String plainText, String salt) {
        return EncryptionLocalUtil.encrypt(EncryptTypeEnum.ID_CARD, plainText, salt);
    }

    public static CommonEncryptResponse idCardEncrypt(String plainText) {
        return EncryptionLocalUtil.idCardEncrypt(plainText, DEFAULT_SALT);
    }

    public static CommonEncryptResponse bankCardNoEncrypt(String plainText, String salt) {
        return EncryptionLocalUtil.encrypt(EncryptTypeEnum.BANK_CARD_NUM, plainText, salt);
    }

    public static CommonEncryptResponse bankCardNoEncrypt(String plainText) {
        return EncryptionLocalUtil.bankCardNoEncrypt(plainText, DEFAULT_SALT);
    }

    public static CommonEncryptResponse passwordEncrypt(String plainText, String salt) {
        return EncryptionLocalUtil.encrypt(EncryptTypeEnum.PASSWORD, plainText, salt);
    }

    public static CommonEncryptResponse passwordEncrypt(String plainText) {
        return EncryptionLocalUtil.passwordEncrypt(plainText, DEFAULT_SALT);
    }

    public static String addressDecrypt(String cipher, String salt) {
        return EncryptionLocalUtil.decrypt(EncryptTypeEnum.ADDRESS, cipher, salt);
    }

    public static String emailDecrypt(String cipher, String salt) {
        return EncryptionLocalUtil.decrypt(EncryptTypeEnum.EMAIL, cipher, salt);
    }

    public static String phoneDecrypt(String cipher, String salt) {
        return EncryptionLocalUtil.decrypt(EncryptTypeEnum.PHONE, cipher, salt);
    }

    public static String nameDecrypt(String cipher, String salt) {
        return EncryptionLocalUtil.decrypt(EncryptTypeEnum.NAME, cipher, salt);
    }

    public static String bankCardNumDecrypt(String cipher, String salt) {
        return EncryptionLocalUtil.decrypt(EncryptTypeEnum.BANK_CARD_NUM, cipher, salt);
    }

    public static String idCardDecrypt(String cipher, String salt) {
        return EncryptionLocalUtil.decrypt(EncryptTypeEnum.ID_CARD, cipher, salt);
    }

    public static String addressDecrypt(String cipher) {
        return EncryptionLocalUtil.addressDecrypt(cipher, DEFAULT_SALT);
    }

    public static String emailDecrypt(String cipher) {
        return EncryptionLocalUtil.emailDecrypt(cipher, DEFAULT_SALT);
    }

    public static String phoneDecrypt(String cipher) {
        return EncryptionLocalUtil.phoneDecrypt(cipher, DEFAULT_SALT);
    }

    public static String nameDecrypt(String cipher) {
        return EncryptionLocalUtil.nameDecrypt(cipher, DEFAULT_SALT);
    }

    public static String bankCardNumDecrypt(String cipher) {
        return EncryptionLocalUtil.bankCardNumDecrypt(cipher, DEFAULT_SALT);
    }

    public static String idCardDecrypt(String cipher) {
        return EncryptionLocalUtil.idCardDecrypt(cipher, DEFAULT_SALT);
    }

    public static String passwordDecrypt(String cipher, String salt) {
        return EncryptionLocalUtil.decrypt(EncryptTypeEnum.PASSWORD, cipher, salt);
    }

    public static String passwordDecrypt(String cipher) {
        return EncryptionLocalUtil.passwordDecrypt(cipher, DEFAULT_SALT);
    }

    private static CommonEncryptResponse encrypt(EncryptTypeEnum typeEnum, String plainText, String salt) {
        return EncryptionLocalUtil.encrypt(typeEnum, plainText, salt, Hashes.md5(), Secrets.aes());
    }

    private static CommonEncryptResponse encrypt(EncryptTypeEnum typeEnum, String plainText, String salt, Hash hash, Secret secret) {
        EncryptionLocalBs encryptionLocalBs = EncryptionLocalBs.newInstance().salt(salt).hash(hash).secret(secret);
        return encryptionLocalBs.encrypt(plainText, typeEnum.getCode());
    }

    private static String decrypt(EncryptTypeEnum typeEnum, String cipher, String salt) {
        return EncryptionLocalUtil.decrypt(typeEnum, cipher, salt, Hashes.md5(), Secrets.aes());
    }

    private static String decrypt(EncryptTypeEnum typeEnum, String cipher, String salt, Hash hash, Secret secret) {
        EncryptionLocalBs encryptionLocalBs = EncryptionLocalBs.newInstance().salt(salt).hash(hash).secret(secret);
        return encryptionLocalBs.decrypt(cipher, typeEnum.getCode());
    }

    public static String hash(String plainText, String salt, Hash hash) {
        EncryptionLocalBs encryptionLocalBs = EncryptionLocalBs.newInstance().salt(salt).hash(hash);
        return encryptionLocalBs.hash(plainText);
    }

    public static String hash(String plainText, String salt) {
        return EncryptionLocalUtil.hash(plainText, salt, Hashes.md5());
    }
}

