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

package com.kuma.boot.security.spring.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.kuma.boot.security.spring.constants.SymbolConstants;
import com.kuma.boot.security.spring.exception.IllegalSymmetricKeyException;
import org.apache.commons.lang3.ArrayUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> 基于Hutool的Aes加解密工具
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:09:20
 */
public class SymmetricUtils {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(SymmetricUtils.class);

    /**
     * 真正秘密密钥加密
     *
     * @param symmetricKey 对称密钥
     * @return {@link String }
     * @since 2023-07-04 10:09:20
     */
    private static String encryptedRealSecretKey(String symmetricKey) {
        String realSecretKey = RandomUtil.randomString(16);
        log.info("Generate Random Secret Key is : [{}]", realSecretKey);

        AES ase = SecureUtil.aes(symmetricKey.getBytes());
        String encryptedRealSecretKey = ase.encryptHex(realSecretKey);
        log.info("Generate Encrypt Hex Secret Key is : [{}]", encryptedRealSecretKey);

        return encryptedRealSecretKey;
    }

    /**
     * 得到加密对称密钥
     *
     * @return {@link String }
     * @since 2023-07-04 10:09:20
     */
    public static String getEncryptedSymmetricKey() {
        String symmetricKey = RandomUtil.randomString(16);
        String realSecretKey = encryptedRealSecretKey(symmetricKey);
        log.info("Generate Symmetric Key is : [{}]", realSecretKey);

        return symmetricKey + SymbolConstants.FORWARD_SLASH + realSecretKey;
    }

    /**
     * 得到解密对称密钥
     *
     * @param key 关键
     * @return {@link byte[] }
     * @since 2023-07-04 10:09:20
     */
    public static byte[] getDecryptedSymmetricKey(String key) {
        if (!StringUtils.contains(key, SymbolConstants.FORWARD_SLASH)) {
            throw new IllegalSymmetricKeyException("Parameter Illegal!");
        }

        String[] keys = StringUtils.split(key, SymbolConstants.FORWARD_SLASH);
        String symmetricKey = keys[0];
        String realSecretKey = keys[1];

        AES ase = SecureUtil.aes(symmetricKey.getBytes());
        return ase.decrypt(realSecretKey);
    }

    /**
     * 解密
     *
     * @param content 内容
     * @param key     关键
     * @return {@link String }
     * @since 2023-07-04 10:09:20
     */
    public static String decrypt(String content, byte[] key) {
        if (ArrayUtils.isNotEmpty(key)) {
            AES ase = SecureUtil.aes(key);
            return ase.decryptStr(content);
        }

        return "";
    }
}
