//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core;

import com.kuma.boot.common.support.secret.exception.SecretRuntimeException;
import com.kuma.boot.common.support.tuple.impl.Pair;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RsaUtil
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public final class RsaUtil {

    /**
     * 生成 RSA 密钥对。
     *
     * @deprecated 生成的 1024 位密钥已不符合现代安全标准，请改用 2048 位以上。
     *             加解密使用的 RSA/ECB/PKCS1Padding 填充易受 Bleichenbacher 攻击，
     *             新代码应使用 RSA/ECB/OAEPWithSHA-256AndMGF1Padding。
     */
    @Deprecated
    public static Pair<String, String> genKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048, new SecureRandom()); // 升至 2048 位
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            String publicKeyString = Base64Util.encodeToString(publicKey.getEncoded());
            String privateKeyString = Base64Util.encodeToString(privateKey.getEncoded());
            return Pair.of(publicKeyString, privateKeyString);
        } catch (NoSuchAlgorithmException e) {
            throw new SecretRuntimeException(e);
        }
    }

    public static String encrypt( String plainText, String publicKey ) {
        try {
            byte[] bytes = plainText.getBytes("UTF-8");
            return encrypt(bytes, publicKey);
        } catch (Exception e) {
            throw new SecretRuntimeException(e);
        }
    }

    public static String encrypt( byte[] plainTextBytes, String publicKey ) {
        try {
            byte[] decoded = Base64Util.decode(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(1, pubKey);
            byte[] doFinalBytes = cipher.doFinal(plainTextBytes);
            return Base64Util.encodeToString(doFinalBytes);
        } catch (Exception e) {
            throw new SecretRuntimeException(e);
        }
    }

    public static String decrypt( String str, String privateKey ) {
        try {
            byte[] inputByte = Base64Util.decode(str);
            byte[] decoded = Base64Util.decode(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(2, priKey);
            return new String(cipher.doFinal(inputByte));
        } catch (Exception e) {
            throw new SecretRuntimeException(e);
        }
    }

    public static void main( String[] args ) {
        String message = "123456";
        Pair<String, String> keyPair = genKeyPair();
        String publicKey = (String) keyPair.getValueOne();
        String privateKey = (String) keyPair.getValueTwo();
        System.out.println("随机生成的公钥为: " + publicKey);
        System.out.println("随机生成的私钥为: " + privateKey);
        String messageEn = encrypt(message, publicKey);
        System.out.println(message + " 加密后的字符串为: " + messageEn);
        String messageDe = decrypt(messageEn, privateKey);
        System.out.println("还原后的字符串为: " + messageDe);
    }
}
