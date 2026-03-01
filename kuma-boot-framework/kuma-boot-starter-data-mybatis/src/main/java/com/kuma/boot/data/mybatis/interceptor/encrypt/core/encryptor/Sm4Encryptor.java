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

package com.kuma.boot.data.mybatis.interceptor.encrypt.core.encryptor;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import com.kuma.boot.data.mybatis.interceptor.encrypt.core.EncryptContext;
import com.kuma.boot.data.mybatis.interceptor.encrypt.enumd.AlgorithmType;
import com.kuma.boot.data.mybatis.interceptor.encrypt.enumd.EncodeType;
import java.nio.charset.StandardCharsets;
import cn.hutool.core.util.StrUtil;

/** sm4算法实现 */
public class Sm4Encryptor extends AbstractEncryptor {

    private final SM4 sm4;

    public Sm4Encryptor(EncryptContext context) {
        super(context);
        String password = context.getPassword();
        if (StrUtil.isBlank(password)) {
            throw new IllegalArgumentException("SM4没有获得秘钥信息");
        }
        // sm4算法的秘钥要求是16位长度
        if (16 != password.length()) {
            throw new IllegalArgumentException("SM4秘钥长度应该为16位，实际为" + password.length() + "位");
        }
        this.sm4 = SmUtil.sm4(password.getBytes(StandardCharsets.UTF_8));
    }

    /** 获得当前算法 */
    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.SM4;
    }

    /**
     * 加密
     *
     * @param value 待加密字符串
     * @param encodeType 加密后的编码格式
     */
    @Override
    public String encrypt(String value, EncodeType encodeType) {
        if (encodeType == EncodeType.HEX) {
            return sm4.encryptHex(value);
        } else {
            return sm4.encryptBase64(value);
        }
    }

    /**
     * 解密
     *
     * @param value 待加密字符串
     */
    @Override
    public String decrypt(String value) {
        return this.sm4.decryptStr(value);
    }
}
