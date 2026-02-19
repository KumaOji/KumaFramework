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

package com.kuma.boot.data.mybatis.interceptor.cipher.config;

import com.kuma.boot.data.mybatis.interceptor.cipher.service.CryptService;
import com.kuma.boot.data.mybatis.interceptor.cipher.service.CryptServiceImpl;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisInterceptorProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * CipherConfig
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-12 14:04:30
 */
@AutoConfiguration
@ConditionalOnProperty(
        prefix = MybatisInterceptorProperties.CIPHER_ENCRYPT_PREFIX,
        name = "enabled",
        havingValue = "true")
public class CipherConfig {

    @Bean
    public CryptService cryptService() {
        return new CryptServiceImpl();
    }
}
