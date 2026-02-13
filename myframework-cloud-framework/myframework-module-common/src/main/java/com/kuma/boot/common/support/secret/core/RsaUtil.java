/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.core;

import com.kuma.boot.common.support.secret.core.Base64Util;
import com.kuma.boot.common.support.secret.exception.SecretRuntimeException;
import com.kuma.boot.common.support.tuple.impl.Pair;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public final class RsaUtil {
    public static Pair<String, String> genKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
            RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
            String publicKeyString = Base64Util.encodeToString(publicKey.getEncoded());
            String privateKeyString = Base64Util.encodeToString(privateKey.getEncoded());
            return Pair.of(publicKeyString, privateKeyString);
        }
        catch (NoSuchAlgorithmException e) {
            throw new SecretRuntimeException(e);
        }
    }

    public static String encrypt(String plainText, String publicKey) {
        try {
            byte[] bytes = plainText.getBytes("UTF-8");
            return RsaUtil.encrypt(bytes, publicKey);
        }
        catch (Exception e) {
            throw new SecretRuntimeException(e);
        }
    }

    public static String encrypt(byte[] plainTextBytes, String publicKey) {
        try {
            byte[] decoded = Base64Util.decode(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(1, pubKey);
            byte[] doFinalBytes = cipher.doFinal(plainTextBytes);
            return Base64Util.encodeToString(doFinalBytes);
        }
        catch (Exception e) {
            throw new SecretRuntimeException(e);
        }
    }

    public static String decrypt(String str, String privateKey) {
        try {
            byte[] inputByte = Base64Util.decode(str);
            byte[] decoded = Base64Util.decode(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(2, priKey);
            return new String(cipher.doFinal(inputByte));
        }
        catch (Exception e) {
            throw new SecretRuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String message = "123456";
        Pair<String, String> keyPair = RsaUtil.genKeyPair();
        String publicKey = keyPair.getValueOne();
        String privateKey = keyPair.getValueTwo();
        System.out.println("\u968f\u673a\u751f\u6210\u7684\u516c\u94a5\u4e3a: " + publicKey);
        System.out.println("\u968f\u673a\u751f\u6210\u7684\u79c1\u94a5\u4e3a: " + privateKey);
        String messageEn = RsaUtil.encrypt(message, publicKey);
        System.out.println(message + " \u52a0\u5bc6\u540e\u7684\u5b57\u7b26\u4e32\u4e3a: " + messageEn);
        String messageDe = RsaUtil.decrypt(messageEn, privateKey);
        System.out.println("\u8fd8\u539f\u540e\u7684\u5b57\u7b26\u4e32\u4e3a: " + messageDe);
    }
}

