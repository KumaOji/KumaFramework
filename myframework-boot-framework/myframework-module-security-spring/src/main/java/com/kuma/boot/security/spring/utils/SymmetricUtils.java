/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.dromara.hutool.core.util.RandomUtil
 *  org.dromara.hutool.crypto.SecureUtil
 *  org.dromara.hutool.crypto.symmetric.AES
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.security.spring.utils;

import com.kuma.boot.security.spring.exception.IllegalSymmetricKeyException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.util.RandomUtil;
import org.dromara.hutool.crypto.SecureUtil;
import org.dromara.hutool.crypto.symmetric.AES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymmetricUtils {
    private static final Logger log = LoggerFactory.getLogger(SymmetricUtils.class);

    private static String encryptedRealSecretKey(String symmetricKey) {
        String realSecretKey = RandomUtil.randomString((int)16);
        log.info("Generate Random Secret Key is : [{}]", (Object)realSecretKey);
        AES ase = SecureUtil.aes((byte[])symmetricKey.getBytes());
        String encryptedRealSecretKey = ase.encryptHex(realSecretKey);
        log.info("Generate Encrypt Hex Secret Key is : [{}]", (Object)encryptedRealSecretKey);
        return encryptedRealSecretKey;
    }

    public static String getEncryptedSymmetricKey() {
        String symmetricKey = RandomUtil.randomString((int)16);
        String realSecretKey = SymmetricUtils.encryptedRealSecretKey(symmetricKey);
        log.info("Generate Symmetric Key is : [{}]", (Object)realSecretKey);
        return symmetricKey + "/" + realSecretKey;
    }

    public static byte[] getDecryptedSymmetricKey(String key) {
        if (!StringUtils.contains((CharSequence)key, (CharSequence)"/")) {
            throw new IllegalSymmetricKeyException("Parameter Illegal!");
        }
        String[] keys = StringUtils.split((String)key, (String)"/");
        String symmetricKey = keys[0];
        String realSecretKey = keys[1];
        AES ase = SecureUtil.aes((byte[])symmetricKey.getBytes());
        return ase.decrypt(realSecretKey);
    }

    public static String decrypt(String content, byte[] key) {
        if (ArrayUtils.isNotEmpty((byte[])key)) {
            AES ase = SecureUtil.aes((byte[])key);
            return ase.decryptStr(content);
        }
        return "";
    }
}

