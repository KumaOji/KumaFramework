/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.lang.Assert
 *  jakarta.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.kuma.boot.common.utils.secure;

import cn.hutool.core.lang.Assert;
import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.secure.HexUtils;
import com.kuma.boot.common.utils.secure.Pkcs7EncoderUtils;
import jakarta.annotation.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;

public class AESUtils {
    private static final String AES = "AES";
    private static final String IV_STRING = "16-Bytes--String";
    private static final String DEFAULT_KEY = "70pQxrWV7NWgGRXQ";
    private static final String CIPHER = "AES/CBC/PKCS5Padding";
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private AESUtils() {
    }

    public static String encrypt(String text) {
        return Base64.getEncoder().encodeToString(AESUtils.encrypt(text, DEFAULT_KEY));
    }

    public static String decrypt(String ciphertext) {
        return AESUtils.decrypt(ciphertext, DEFAULT_KEY);
    }

    public static String decrypt(String ciphertext, String key) {
        if (StringUtils.isAnyBlank((CharSequence[])new CharSequence[]{ciphertext, key}) || 16 != key.length()) {
            return null;
        }
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
            byte[] enCodeFormat = key.getBytes();
            SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, AES);
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(2, (Key)secretKey, ivParameterSpec);
            byte[] result = cipher.doFinal(encryptedBytes);
            return new String(result, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return null;
        }
    }

    public static String genAesKey() {
        return com.kuma.boot.common.utils.lang.StringUtils.random(32);
    }

    public static String encryptToHex(String content, String aesTextKey) {
        return HexUtils.encodeToString(AESUtils.encrypt(content, aesTextKey));
    }

    public static String encryptToHex(byte[] content, String aesTextKey) {
        return HexUtils.encodeToString(AESUtils.encrypt(content, aesTextKey));
    }

    public static String encryptToBase64(String content, String aesTextKey) {
        return Base64.getEncoder().encodeToString(AESUtils.encrypt(content, aesTextKey));
    }

    public static String encryptToBase64(byte[] content, String aesTextKey) {
        return Base64.getEncoder().encodeToString(AESUtils.encrypt(content, aesTextKey));
    }

    public static byte[] encrypt(String content, String aesTextKey) {
        return AESUtils.encrypt(content.getBytes(DEFAULT_CHARSET), aesTextKey);
    }

    public static byte[] encrypt(String content, Charset charset, String aesTextKey) {
        return AESUtils.encrypt(content.getBytes(charset), aesTextKey);
    }

    public static byte[] encrypt(byte[] content, String aesTextKey) {
        return AESUtils.encrypt(content, Objects.requireNonNull(aesTextKey).getBytes(DEFAULT_CHARSET));
    }

    @Nullable
    public static String decryptFormHexToString(@Nullable String content, String aesTextKey) {
        byte[] hexBytes = AESUtils.decryptFormHex(content, aesTextKey);
        if (hexBytes == null) {
            return null;
        }
        return new String(hexBytes, DEFAULT_CHARSET);
    }

    @Nullable
    public static byte[] decryptFormHex(@Nullable String content, String aesTextKey) {
        if (com.kuma.boot.common.utils.lang.StringUtils.isBlank(content)) {
            return null;
        }
        return AESUtils.decryptFormHex(content.getBytes(DEFAULT_CHARSET), aesTextKey);
    }

    public static byte[] decryptFormHex(byte[] content, String aesTextKey) {
        return AESUtils.decrypt(HexUtils.decode(content), aesTextKey);
    }

    @Nullable
    public static String decryptFormBase64ToString(@Nullable String content, String aesTextKey) {
        byte[] hexBytes = AESUtils.decryptFormBase64(content, aesTextKey);
        if (hexBytes == null) {
            return null;
        }
        return new String(hexBytes, DEFAULT_CHARSET);
    }

    @Nullable
    public static byte[] decryptFormBase64(@Nullable String content, String aesTextKey) {
        if (com.kuma.boot.common.utils.lang.StringUtils.isBlank(content)) {
            return null;
        }
        return AESUtils.decryptFormBase64(content.getBytes(DEFAULT_CHARSET), aesTextKey);
    }

    public static byte[] decryptFormBase64(byte[] content, String aesTextKey) {
        return AESUtils.decrypt(Base64.getDecoder().decode(content), aesTextKey);
    }

    public static String decryptToString(byte[] content, String aesTextKey) {
        return new String(AESUtils.decrypt(content, aesTextKey), DEFAULT_CHARSET);
    }

    public static byte[] decrypt(byte[] content, String aesTextKey) {
        return AESUtils.decrypt(content, Objects.requireNonNull(aesTextKey).getBytes(DEFAULT_CHARSET));
    }

    public static byte[] encrypt(byte[] content, byte[] aesKey) {
        return AESUtils.aes(Pkcs7EncoderUtils.encode(content), aesKey, 1);
    }

    public static byte[] decrypt(byte[] encrypted, byte[] aesKey) {
        return Pkcs7EncoderUtils.decode(AESUtils.aes(encrypted, aesKey, 2));
    }

    private static byte[] aes(byte[] encrypted, byte[] aesKey, int mode) {
        Assert.isTrue((aesKey.length == 32 ? 1 : 0) != 0, (String)"IllegalAesKey, aesKey's length must be 32", (Object[])new Object[0]);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, AES);
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(mode, (Key)keySpec, iv);
            return cipher.doFinal(encrypted);
        }
        catch (Exception e) {
            throw ExceptionUtils.unchecked(e);
        }
    }
}

