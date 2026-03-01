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

package com.kuma.boot.security.spring.authentication.login.social.justauth.repository.factory;

import com.kuma.boot.security.spring.authentication.login.social.justauth.properties.RepositoryProperties;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.UsersConnectionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * UsersConnectionRepository 工厂
 *
 * @author YongWu zheng
 * @version V2.0 Created by 2020/5/13 23:04
 */
public interface UsersConnectionRepositoryFactory {

    /**
     * UsersConnectionRepository 工厂
     *
     * @param textEncryptor                   对 key 与 secret 进行加解密。
     * @param auth2UserConnectionJdbcTemplate 对 key 与 secret 进行加解密。
     * @param repositoryProperties            repositoryProperties
     * @return UsersConnectionRepository
     */
    UsersConnectionRepository getUsersConnectionRepository(
            JdbcTemplate auth2UserConnectionJdbcTemplate,
            TextEncryptor textEncryptor,
            RepositoryProperties repositoryProperties);
}
