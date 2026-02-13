/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.secure;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtils {
    private static final String DES = "DES";
    private static final String DEFAULT_KEY = "kuma_default_key";

    private DESUtils() {
    }

    public static String encrypt(String text) {
        return DESUtils.encrypt(text, DEFAULT_KEY);
    }

    public static String encrypt(String text, String key) {
        if (StringUtils.isAnyBlank(text, key)) {
            return null;
        }
        try {
            SecureRandom secureRandom = new SecureRandom();
            DESKeySpec keySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey securekey = keyFactory.generateSecret(keySpec);
            Cipher cipher = Cipher.getInstance(DES);
            cipher.init(1, (Key)securekey, secureRandom);
            byte[] bytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return null;
        }
    }

    public static String decrypt(String ciphertext) {
        return DESUtils.decrypt(ciphertext, DEFAULT_KEY);
    }

    public static String decrypt(String ciphertext, String key) {
        if (StringUtils.isAnyBlank(ciphertext, key)) {
            return null;
        }
        try {
            SecureRandom secureRandom = new SecureRandom();
            DESKeySpec keySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey securekey = keyFactory.generateSecret(keySpec);
            Cipher cipher = Cipher.getInstance(DES);
            cipher.init(2, (Key)securekey, secureRandom);
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext.getBytes(StandardCharsets.UTF_8)));
            return new String(bytes, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return null;
        }
    }
}

