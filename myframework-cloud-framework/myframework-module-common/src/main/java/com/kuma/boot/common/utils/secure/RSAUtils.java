/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.kuma.boot.common.utils.secure;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.secure.RSAKey;
import jakarta.annotation.Nullable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import javax.crypto.Cipher;
import org.apache.commons.lang3.StringUtils;

public class RSAUtils {
    private static final String RSA = "RSA";
    private static final int KEY_SIZE = 1024;
    public static final String RSA_ALGORITHM = "RSA";
    public static final String RSA_PADDING = "RSA/ECB/PKCS1Padding";

    private RSAUtils() {
    }

    public static Map<RSAKey, String> generatorPairKey() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            EnumMap<RSAKey, String> pairKeyMap = new EnumMap<RSAKey, String>(RSAKey.class);
            pairKeyMap.put(RSAKey.PUBLIC, publicKeyString);
            pairKeyMap.put(RSAKey.PRIVATE, privateKeyString);
            return pairKeyMap;
        }
        catch (NoSuchAlgorithmException e) {
            LogUtils.error(e.getMessage(), e);
            return null;
        }
    }

    public static String encrypt(String text, String publicKey) {
        if (StringUtils.isAnyBlank((CharSequence[])new CharSequence[]{text, publicKey})) {
            return null;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(publicKey);
            RSAPublicKey rsaPublicKey = (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(1, rsaPublicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return null;
        }
    }

    public static String decrypt(String ciphertext, String privateKey) {
        if (StringUtils.isAnyBlank((CharSequence[])new CharSequence[]{ciphertext, privateKey})) {
            return null;
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(ciphertext.getBytes(StandardCharsets.UTF_8));
            byte[] decoded = Base64.getDecoder().decode(privateKey);
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(2, rsaPrivateKey);
            return new String(cipher.doFinal(bytes));
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return null;
        }
    }

    public static com.kuma.boot.common.support.tuple.pair.KeyPair genKeyPair() {
        return RSAUtils.genKeyPair(1024);
    }

    public static com.kuma.boot.common.support.tuple.pair.KeyPair genKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(keySize);
            return new com.kuma.boot.common.support.tuple.pair.KeyPair(keyPairGen.generateKeyPair());
        }
        catch (NoSuchAlgorithmException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static PrivateKey generatePrivateKey(String modulus, String exponent) {
        return RSAUtils.generatePrivateKey(new BigInteger(modulus), new BigInteger(exponent));
    }

    public static PrivateKey generatePrivateKey(BigInteger modulus, BigInteger exponent) {
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulus, exponent);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static PublicKey generatePublicKey(String modulus, String exponent) {
        return RSAUtils.generatePublicKey(new BigInteger(modulus), new BigInteger(exponent));
    }

    public static PublicKey generatePublicKey(BigInteger modulus, BigInteger exponent) {
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static PublicKey getPublicKey(String base64PubKey) {
        Objects.requireNonNull(base64PubKey, "base64 public key is null.");
        byte[] keyBytes = Base64.getDecoder().decode(base64PubKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static String getPublicKeyToBase64(String base64PubKey) {
        PublicKey publicKey = RSAUtils.getPublicKey(base64PubKey);
        return RSAUtils.getKeyString(publicKey);
    }

    public static PrivateKey getPrivateKey(String base64PriKey) {
        Objects.requireNonNull(base64PriKey, "base64 private key is null.");
        byte[] keyBytes = Base64.getDecoder().decode(base64PriKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static String getKeyString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getPrivateKeyToBase64(String base64PriKey) {
        PrivateKey privateKey = RSAUtils.getPrivateKey(base64PriKey);
        return RSAUtils.getKeyString(privateKey);
    }

    public static byte[] encrypt(String base64PublicKey, byte[] data) {
        return RSAUtils.encrypt(RSAUtils.getPublicKey(base64PublicKey), data);
    }

    public static byte[] encrypt(PublicKey publicKey, byte[] data) {
        return RSAUtils.rsa(publicKey, data, 1);
    }

    public static byte[] encryptByPrivateKey(String base64PrivateKey, byte[] data) {
        return RSAUtils.encryptByPrivateKey(RSAUtils.getPrivateKey(base64PrivateKey), data);
    }

    public static String encryptByPrivateKeyToBase64(String base64PrivateKey, byte[] data) {
        return Base64.getEncoder().encodeToString(RSAUtils.encryptByPrivateKey(base64PrivateKey, data));
    }

    public static byte[] encryptByPrivateKey(PrivateKey privateKey, byte[] data) {
        return RSAUtils.rsa(privateKey, data, 1);
    }

    @Nullable
    public static String encryptToBase64(PublicKey publicKey, @Nullable String data) {
        if (com.kuma.boot.common.utils.lang.StringUtils.isBlank(data)) {
            return null;
        }
        return Base64.getEncoder().encodeToString(RSAUtils.encrypt(publicKey, data.getBytes(StandardCharsets.UTF_8)));
    }

    @Nullable
    public static String encryptToBase64(String base64PublicKey, @Nullable String data) {
        return RSAUtils.encryptToBase64(RSAUtils.getPublicKey(base64PublicKey), data);
    }

    public static byte[] decrypt(String base64PrivateKey, byte[] data) {
        return RSAUtils.decrypt(RSAUtils.getPrivateKey(base64PrivateKey), data);
    }

    public static byte[] decryptByPublicKey(String base64publicKey, byte[] data) {
        return RSAUtils.decryptByPublicKey(RSAUtils.getPublicKey(base64publicKey), data);
    }

    public static byte[] decrypt(PrivateKey privateKey, byte[] data) {
        return RSAUtils.rsa(privateKey, data, 2);
    }

    public static byte[] decryptByPublicKey(PublicKey publicKey, byte[] data) {
        return RSAUtils.rsa(publicKey, data, 2);
    }

    private static byte[] rsa(Key key, byte[] data, int mode) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_PADDING);
            cipher.init(mode, key);
            return cipher.doFinal(data);
        }
        catch (Exception e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static byte[] decryptByPublicKeyFromBase64(PublicKey publicKey, byte[] base64Data) {
        return RSAUtils.decryptByPublicKey(publicKey, Base64.getDecoder().decode(base64Data));
    }

    public static byte[] decryptByPublicKeyFromBase64(String base64PublicKey, byte[] base64Data) {
        return RSAUtils.decryptByPublicKeyFromBase64(RSAUtils.getPublicKey(base64PublicKey), base64Data);
    }

    @Nullable
    public static String decryptFromBase64(PrivateKey privateKey, @Nullable String base64Data) {
        if (com.kuma.boot.common.utils.lang.StringUtils.isBlank(base64Data)) {
            return null;
        }
        return new String(RSAUtils.decrypt(privateKey, Base64.getDecoder().decode(base64Data)), StandardCharsets.UTF_8);
    }

    @Nullable
    public static String decryptFromBase64(String base64PrivateKey, @Nullable String base64Data) {
        return RSAUtils.decryptFromBase64(RSAUtils.getPrivateKey(base64PrivateKey), base64Data);
    }

    public static byte[] decryptFromBase64(String base64PrivateKey, byte[] base64Data) {
        return RSAUtils.decrypt(base64PrivateKey, Base64.getDecoder().decode(base64Data));
    }

    @Nullable
    public static String decryptByPublicKeyFromBase64(PublicKey publicKey, @Nullable String base64Data) {
        if (com.kuma.boot.common.utils.lang.StringUtils.isBlank(base64Data)) {
            return null;
        }
        return new String(RSAUtils.decryptByPublicKey(publicKey, Base64.getDecoder().decode(base64Data)), StandardCharsets.UTF_8);
    }

    @Nullable
    public static String decryptByPublicKeyFromBase64(String base64PublicKey, @Nullable String base64Data) {
        return RSAUtils.decryptByPublicKeyFromBase64(RSAUtils.getPublicKey(base64PublicKey), base64Data);
    }
}

