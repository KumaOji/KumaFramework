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

package com.kuma.boot.data.datasource.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

/**
 * RSAUtil
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class RSAUtil {

    private static final KeyPair keyPair = KeyUtil.generateKeyPair("RSA");

    /**
     * 获取RSA公钥
     *
     * @return base64的公钥
     */
    public static String generatePublicKey() {
        return Base64.encode(keyPair.getPublic().getEncoded());
    }

    /**
     * 获取RSA私钥
     *
     * @return base64的私钥
     */
    public static String generatePrivateKey() {
        return Base64.encode(keyPair.getPrivate().getEncoded());
    }

    /**
     * 内容进行私钥加密
     *
     * @param content 需加密内容
     * @param privateKey 私钥
     * @return 加密后信息
     */
    public static String encryptByPrivateKey( String content, String privateKey ) {
        RSA rsa = new RSA(privateKey);
        return rsa.encryptHex(content, KeyType.PrivateKey);
    }

    /**
     * 内容进行加密
     *
     * @param content 需加密内容
     * @param publicKey 公钥
     * @return 加密后信息
     */
    public static String encryptByPublicKey( String content, String publicKey, String privateKey ) {
        RSA rsa = new RSA(privateKey.replace(" ", "+"), publicKey.replace(" ", "+"));
        return rsa.encryptBase64(content, KeyType.PublicKey);
    }

    /**
     * RSA私钥解密
     *
     * @param encryptContent 公钥加密内容
     * @param privateKey 私钥
     * @return 解密后的信息
     */
    public static String decryptByPrivateKey( String encryptContent, String privateKey ) {
        RSA rsa = new RSA(privateKey);
        return rsa.decryptStr(encryptContent, KeyType.PrivateKey, StandardCharsets.UTF_8);
    }

    /**
     * RSA公钥解密
     *
     * @param encryptContent 私钥加密后的内容
     * @param publicKey 公钥
     * @return 解密后的信息
     */
    public static String decryptByPublicKey( String encryptContent, String publicKey ) {
        RSA rsa = new RSA(null, publicKey);
        return rsa.decryptStr(encryptContent, KeyType.PublicKey, StandardCharsets.UTF_8);
    }
}
