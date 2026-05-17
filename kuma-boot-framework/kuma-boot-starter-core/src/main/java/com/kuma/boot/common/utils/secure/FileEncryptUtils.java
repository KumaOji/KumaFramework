/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.utils.secure;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * 文件加密工具类
 *
 * <p>支持四种算法：
 * <ul>
 *   <li>{@link Algorithm#AES} — AES-256/CBC，密钥为任意字符串（内部 SHA-256 派生），<b>推荐</b></li>
 *   <li>{@link Algorithm#DES} — DES/CBC，密钥为任意字符串（有效位取前 8 字节）</li>
 *   <li>{@link Algorithm#SM4} — 国密 SM4/CBC，密钥为 32 位十六进制字符串（16 字节）</li>
 *   <li>{@link Algorithm#RSA} — RSA 混合加密：内部生成随机 AES-256 密钥加密文件内容，
 *       再用 RSA 公钥加密 AES 密钥；解密时需对应私钥</li>
 * </ul>
 *
 * <p>文件格式（加密输出）：
 * <pre>
 *   AES / DES / SM4:  [IV bytes] [密文数据]
 *   RSA:              [4 bytes: encKeyLen] [encKeyLen bytes: RSA加密的AES密钥] [16 bytes: IV] [密文数据]
 * </pre>
 *
 * <p>所有操作均使用流式处理（8 KB 缓冲），支持超大文件，不会一次性加载到内存。
 *
 * @author kuma
 * @version 2026.01
 * @since 2026-03-14
 */
public final class FileEncryptUtils {

    private FileEncryptUtils() {}

    /** 流缓冲区大小 */
    private static final int BUFFER_SIZE = 8192;

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // ------------------------------------------------------------------ 算法枚举

    /**
     * 支持的加密算法
     */
    public enum Algorithm {
        /**
         * AES-256/CBC。
         * 密钥：任意字符串，内部 SHA-256 派生为 32 字节密钥；IV 随机生成，写入文件头部。
         */
        AES,
        /**
         * DES/CBC。
         * 密钥：任意字符串，前 8 字节有效；IV 随机生成，写入文件头部。
         * 注意：DES 已不推荐用于新系统，如需高安全请改用 AES。
         */
        DES,
        /**
         * 国密 SM4/CBC（依赖 BouncyCastle）。
         * 密钥：32 位十六进制字符串（对应 16 字节），可用 {@link #genSm4HexKey()} 生成；
         * IV 随机生成，写入文件头部。
         */
        SM4,
        /**
         * RSA 混合加密（RSA/ECB/PKCS1Padding + AES-256/CBC）。
         * 加密时 key 为 Base64 编码的 RSA 公钥；
         * 解密时 key 为 Base64 编码的 RSA 私钥。
         * 内部自动生成随机 AES 密钥和 IV，RSA 仅用于保护该 AES 密钥。
         */
        RSA
    }

    // ------------------------------------------------------------------ Path API

    /**
     * 使用 AES 加密文件（默认算法）
     *
     * @param src 源文件
     * @param dst 目标文件（加密输出）
     * @param key 密钥（任意字符串）
     */
    public static void encrypt(Path src, Path dst, String key) throws IOException {
        encrypt(src, dst, Algorithm.AES, key);
    }

    /**
     * 使用 AES 解密文件（默认算法）
     *
     * @param src 源文件（加密文件）
     * @param dst 目标文件（解密输出）
     * @param key 密钥（任意字符串）
     */
    public static void decrypt(Path src, Path dst, String key) throws IOException {
        decrypt(src, dst, Algorithm.AES, key);
    }

    /**
     * 加密文件
     *
     * @param src       源文件
     * @param dst       目标文件（加密输出）
     * @param algorithm 加密算法
     * @param key       密钥（含义由算法决定，参见 {@link Algorithm}）
     */
    public static void encrypt(Path src, Path dst, Algorithm algorithm, String key) throws IOException {
        Objects.requireNonNull(src, "src must not be null");
        Objects.requireNonNull(dst, "dst must not be null");
        Objects.requireNonNull(algorithm, "algorithm must not be null");
        Objects.requireNonNull(key, "key must not be null");
        try (InputStream in = new BufferedInputStream(Files.newInputStream(src));
             OutputStream out = new BufferedOutputStream(Files.newOutputStream(dst))) {
            encrypt(in, out, algorithm, key);
        }
    }

    /**
     * 解密文件
     *
     * @param src       源文件（加密文件）
     * @param dst       目标文件（解密输出）
     * @param algorithm 加密算法（需与加密时一致）
     * @param key       密钥（含义由算法决定，参见 {@link Algorithm}）
     */
    public static void decrypt(Path src, Path dst, Algorithm algorithm, String key) throws IOException {
        Objects.requireNonNull(src, "src must not be null");
        Objects.requireNonNull(dst, "dst must not be null");
        Objects.requireNonNull(algorithm, "algorithm must not be null");
        Objects.requireNonNull(key, "key must not be null");
        try (InputStream in = new BufferedInputStream(Files.newInputStream(src));
             OutputStream out = new BufferedOutputStream(Files.newOutputStream(dst))) {
            decrypt(in, out, algorithm, key);
        }
    }

    // ------------------------------------------------------------------ File API

    /**
     * 加密文件
     *
     * @param src       源文件
     * @param dst       目标文件（加密输出）
     * @param algorithm 加密算法
     * @param key       密钥
     */
    public static void encrypt(File src, File dst, Algorithm algorithm, String key) throws IOException {
        encrypt(src.toPath(), dst.toPath(), algorithm, key);
    }

    /**
     * 解密文件
     *
     * @param src       源文件（加密文件）
     * @param dst       目标文件（解密输出）
     * @param algorithm 加密算法
     * @param key       密钥
     */
    public static void decrypt(File src, File dst, Algorithm algorithm, String key) throws IOException {
        decrypt(src.toPath(), dst.toPath(), algorithm, key);
    }

    // ------------------------------------------------------------------ Stream API

    /**
     * 加密流
     *
     * @param in        明文输入流
     * @param out       密文输出流
     * @param algorithm 加密算法
     * @param key       密钥
     */
    public static void encrypt(InputStream in, OutputStream out, Algorithm algorithm, String key) throws IOException {
        switch (algorithm) {
            case AES -> encryptAes(in, out, key);
            case DES -> encryptDes(in, out, key);
            case SM4 -> encryptSm4(in, out, key);
            case RSA -> encryptRsa(in, out, key);
        }
    }

    /**
     * 解密流
     *
     * @param in        密文输入流
     * @param out       明文输出流
     * @param algorithm 加密算法（需与加密时一致）
     * @param key       密钥
     */
    public static void decrypt(InputStream in, OutputStream out, Algorithm algorithm, String key) throws IOException {
        switch (algorithm) {
            case AES -> decryptAes(in, out, key);
            case DES -> decryptDes(in, out, key);
            case SM4 -> decryptSm4(in, out, key);
            case RSA -> decryptRsa(in, out, key);
        }
    }

    // ------------------------------------------------------------------ 密钥生成工具

    /**
     * 生成随机 AES 密钥（32位字符串）
     *
     * @return 32 字符随机密钥
     */
    public static String genAesKey() {
        return AESUtils.genAesKey();
    }

    /**
     * 生成随机 SM4 密钥（32位十六进制字符串 = 16 字节）
     *
     * @return 32 字符十六进制密钥
     */
    public static String genSm4HexKey() {
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);
        return HexUtils.encodeToString(key);
    }

    /**
     * 生成 RSA 密钥对（Base64 编码）
     *
     * @return [0]=Base64公钥, [1]=Base64私钥
     */
    public static String[] genRsaKeyPair() {
        return genRsaKeyPair(2048);
    }

    /**
     * 生成 RSA 密钥对（Base64 编码）
     *
     * @param keySize 密钥位数，推荐 2048 或 4096
     * @return [0]=Base64公钥, [1]=Base64私钥
     */
    public static String[] genRsaKeyPair(int keySize) {
        try {
            java.security.KeyPairGenerator gen = java.security.KeyPairGenerator.getInstance("RSA");
            gen.initialize(keySize, new SecureRandom());
            java.security.KeyPair pair = gen.generateKeyPair();
            String pub = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
            String pri = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
            return new String[]{pub, pri};
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    // ------------------------------------------------------------------ AES 实现

    /**
     * 文件格式: [16 bytes IV][AES/CBC/PKCS5Padding 密文]
     * 密钥派生: SHA-256(textKey.getBytes(UTF-8)) → 32 bytes
     */
    private static void encryptAes(InputStream in, OutputStream out, String textKey) throws IOException {
        try {
            byte[] keyBytes = sha256(textKey.getBytes(StandardCharsets.UTF_8));
            byte[] iv = randomBytes(16);
            out.write(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(keyBytes, "AES"),
                    new IvParameterSpec(iv));
            pipeEncrypt(in, out, cipher);
        } catch (GeneralSecurityException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    private static void decryptAes(InputStream in, OutputStream out, String textKey) throws IOException {
        try {
            byte[] keyBytes = sha256(textKey.getBytes(StandardCharsets.UTF_8));
            byte[] iv = readExact(in, 16);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(keyBytes, "AES"),
                    new IvParameterSpec(iv));
            pipeDecrypt(in, out, cipher);
        } catch (GeneralSecurityException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    // ------------------------------------------------------------------ DES 实现

    /**
     * 文件格式: [8 bytes IV][DES/CBC/PKCS5Padding 密文]
     */
    private static void encryptDes(InputStream in, OutputStream out, String textKey) throws IOException {
        try {
            byte[] iv = randomBytes(8);
            out.write(iv);
            SecretKey secretKey = SecretKeyFactory.getInstance("DES")
                    .generateSecret(new DESKeySpec(textKey.getBytes(StandardCharsets.UTF_8)));
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            pipeEncrypt(in, out, cipher);
        } catch (GeneralSecurityException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    private static void decryptDes(InputStream in, OutputStream out, String textKey) throws IOException {
        try {
            byte[] iv = readExact(in, 8);
            SecretKey secretKey = SecretKeyFactory.getInstance("DES")
                    .generateSecret(new DESKeySpec(textKey.getBytes(StandardCharsets.UTF_8)));
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            pipeDecrypt(in, out, cipher);
        } catch (GeneralSecurityException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    // ------------------------------------------------------------------ SM4 实现

    /**
     * 文件格式: [16 bytes IV][SM4/CBC/PKCS5Padding 密文]
     * 密钥: 32 位十六进制字符串（16 字节），可用 {@link #genSm4HexKey()} 生成
     */
    private static void encryptSm4(InputStream in, OutputStream out, String hexKey) throws IOException {
        byte[] keyBytes = decodeSm4Key(hexKey);
        try {
            byte[] iv = randomBytes(16);
            out.write(iv);
            Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS5Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(keyBytes, "SM4"),
                    new IvParameterSpec(iv));
            pipeEncrypt(in, out, cipher);
        } catch (GeneralSecurityException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    private static void decryptSm4(InputStream in, OutputStream out, String hexKey) throws IOException {
        byte[] keyBytes = decodeSm4Key(hexKey);
        try {
            byte[] iv = readExact(in, 16);
            Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS5Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(keyBytes, "SM4"),
                    new IvParameterSpec(iv));
            pipeDecrypt(in, out, cipher);
        } catch (GeneralSecurityException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    private static byte[] decodeSm4Key(String hexKey) {
        if (hexKey == null || hexKey.length() != 32) {
            throw new IllegalArgumentException("SM4 密钥必须为 32 位十六进制字符串（16 字节），当前长度: "
                    + (hexKey == null ? "null" : hexKey.length()));
        }
        byte[] keyBytes = HexUtils.decode(hexKey);
        if (keyBytes == null) {
            throw new IllegalArgumentException("SM4 密钥包含非法十六进制字符");
        }
        return keyBytes;
    }

    // ------------------------------------------------------------------ RSA 混合加密实现

    /**
     * 文件格式:
     * [4 bytes: encKeyLen][encKeyLen bytes: RSA加密的AES密钥][16 bytes: IV][AES/CBC/PKCS5Padding 密文]
     *
     * @param key Base64 编码的 RSA 公钥
     */
    private static void encryptRsa(InputStream in, OutputStream out, String key) throws IOException {
        try {
            // 1. 生成随机 AES-256 密钥和 IV
            byte[] aesKey = randomBytes(32);
            byte[] iv = randomBytes(16);

            // 2. 用 RSA 公钥加密 AES 密钥
            PublicKey publicKey = loadPublicKey(key);
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encAesKey = rsaCipher.doFinal(aesKey);

            // 3. 写入文件头: [encKeyLen][encKey][IV]
            out.write(ByteBuffer.allocate(4).putInt(encAesKey.length).array());
            out.write(encAesKey);
            out.write(iv);

            // 4. 流式加密文件内容
            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(aesKey, "AES"),
                    new IvParameterSpec(iv));
            pipeEncrypt(in, out, aesCipher);
        } catch (GeneralSecurityException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * @param key Base64 编码的 RSA 私钥
     */
    private static void decryptRsa(InputStream in, OutputStream out, String key) throws IOException {
        try {
            // 1. 读取文件头
            int encKeyLen = ByteBuffer.wrap(readExact(in, 4)).getInt();
            if (encKeyLen <= 0 || encKeyLen > 1024) {
                throw new IOException("无效的文件头: encKeyLen=" + encKeyLen);
            }
            byte[] encAesKey = readExact(in, encKeyLen);
            byte[] iv = readExact(in, 16);

            // 2. 用 RSA 私钥解密 AES 密钥
            PrivateKey privateKey = loadPrivateKey(key);
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] aesKey = rsaCipher.doFinal(encAesKey);

            // 3. 流式解密文件内容
            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(aesKey, "AES"),
                    new IvParameterSpec(iv));
            pipeDecrypt(in, out, aesCipher);
        } catch (GeneralSecurityException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    // ------------------------------------------------------------------ 底层流操作

    private static void pipeEncrypt(InputStream in, OutputStream out, Cipher cipher) throws IOException {
        try (CipherOutputStream cos = new CipherOutputStream(out, cipher)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) != -1) {
                cos.write(buf, 0, n);
            }
        }
    }

    private static void pipeDecrypt(InputStream in, OutputStream out, Cipher cipher) throws IOException {
        try (CipherInputStream cis = new CipherInputStream(in, cipher)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = cis.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        }
    }

    // ------------------------------------------------------------------ 辅助方法

    private static byte[] readExact(InputStream in, int length) throws IOException {
        byte[] bytes = in.readNBytes(length);
        if (bytes.length != length) {
            throw new IOException("文件格式错误或文件已损坏: 期望 " + length + " 字节，实际读取 " + bytes.length + " 字节");
        }
        return bytes;
    }

    private static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    private static byte[] sha256(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    private static PublicKey loadPublicKey(String base64PublicKey) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    private static PrivateKey loadPrivateKey(String base64PrivateKey) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }
}
