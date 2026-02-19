/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.interceptor.encrypt.enumd;

import com.kuma.boot.data.mybatis.interceptor.encrypt.core.encryptor.AbstractEncryptor;
import com.kuma.boot.data.mybatis.interceptor.encrypt.core.encryptor.AesEncryptor;
import com.kuma.boot.data.mybatis.interceptor.encrypt.core.encryptor.Base64Encryptor;
import com.kuma.boot.data.mybatis.interceptor.encrypt.core.encryptor.RsaEncryptor;
import com.kuma.boot.data.mybatis.interceptor.encrypt.core.encryptor.Sm2Encryptor;
import com.kuma.boot.data.mybatis.interceptor.encrypt.core.encryptor.Sm4Encryptor;

/** 算法名称 */
public enum AlgorithmType {

    /** 默认走yml配置 */
    DEFAULT(null),

    /** base64 */
    BASE64(Base64Encryptor.class),

    /** aes */
    AES(AesEncryptor.class),

    /** rsa */
    RSA(RsaEncryptor.class),

    /** sm2 */
    SM2(Sm2Encryptor.class),

    /** sm4 */
    SM4(Sm4Encryptor.class);

    private final Class<? extends AbstractEncryptor> clazz;

    AlgorithmType(Class<? extends AbstractEncryptor> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends AbstractEncryptor> getClazz() {
        return clazz;
    }
}
