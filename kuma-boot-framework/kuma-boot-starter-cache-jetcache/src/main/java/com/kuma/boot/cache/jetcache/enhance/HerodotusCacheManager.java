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

package com.kuma.boot.cache.jetcache.enhance;

import com.kuma.boot.cache.jetcache.autoconfigure.properties.Expire;
import com.kuma.boot.cache.jetcache.autoconfigure.properties.JetCacheProperties;
import com.kuma.boot.common.constant.SymbolConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import java.util.Map;

/**
 * 支持按 cache name 配置独立过期时间的 {@link JetCacheSpringCacheManager} 扩展。
 *
 * <p>通过 {@link JetCacheProperties#getExpires()} 匹配 cache name（{@code :} 替换为配置的分隔符），
 * 找到配置则使用自定义 TTL，否则回退到默认行为。
 *
 * @author kuma
 * @since 2022-07-03
 */
public class HerodotusCacheManager extends JetCacheSpringCacheManager {

    private static final Logger log = LoggerFactory.getLogger(HerodotusCacheManager.class);

    private final JetCacheProperties cacheProperties;

    public HerodotusCacheManager(JetCacheCreateCacheFactory jetCacheCreateCacheFactory, JetCacheProperties cacheProperties) {
        super(jetCacheCreateCacheFactory);
        this.cacheProperties = cacheProperties;
        setAllowNullValues(cacheProperties.getAllowNullValues());
    }

    public HerodotusCacheManager(
            JetCacheCreateCacheFactory jetCacheCreateCacheFactory,
            JetCacheProperties cacheProperties,
            String... cacheNames) {
        super(jetCacheCreateCacheFactory, cacheNames);
        this.cacheProperties = cacheProperties;
    }

    @Override
    protected Cache createJetCache(String name) {
        Map<String, Expire> expires = cacheProperties.getExpires();
        if (expires != null && !expires.isEmpty()) {
            String key = name.replace(SymbolConstants.COLON, cacheProperties.getSeparator());
            Expire expire = expires.get(key);
            if (expire != null) {
                log.debug("[kmc] |- CACHE - Cache [{}] is set to use CUSTOM expire.", name);
                return super.createJetCache(name, expire.getTtl());
            }
        }
        return super.createJetCache(name);
    }
}
