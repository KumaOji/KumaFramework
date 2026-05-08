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

package com.kuma.cloud.graalvmtest;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/** 与 Blog 相同：OAuth2 Client 已从 classpath 排除时，用语义扫描过滤掉 OAuth2ClientManagerConfiguration。 */
final class ExcludeOAuth2ClientManagerTypeFilter implements TypeFilter {

    private static final String OAUTH2_CLIENT_MANAGER_CONFIGURATION =
            "com.kuma.boot.security.spring.autoconfigure.OAuth2ClientManagerConfiguration";

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
        return OAUTH2_CLIENT_MANAGER_CONFIGURATION.equals(metadataReader.getClassMetadata().getClassName());
    }
}
