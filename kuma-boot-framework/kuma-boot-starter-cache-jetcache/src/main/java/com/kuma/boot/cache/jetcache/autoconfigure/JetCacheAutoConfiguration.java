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

package com.kuma.boot.cache.jetcache.autoconfigure;

import com.alicp.jetcache.CacheManager;
import com.kuma.boot.cache.jetcache.autoconfigure.properties.JetCacheProperties;
import com.kuma.boot.cache.jetcache.enhance.HerodotusCacheManager;
import com.kuma.boot.cache.jetcache.enhance.JetCacheCreateCacheFactory;
import com.kuma.boot.cache.jetcache.utils.JetCacheUtils;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * JetCache 自动配置。
 *
 * @author kuma
 * @since 2022-07-03
 */
@AutoConfiguration(after = com.alicp.jetcache.autoconfigure.JetCacheAutoConfiguration.class)
@EnableConfigurationProperties(JetCacheProperties.class)
@ConditionalOnProperty(
        prefix = JetCacheProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class JetCacheAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(JetCacheAutoConfiguration.class, StarterNameConstants.CACHE_JETCACHE_STARTER);
    }

    @Bean
    @ConditionalOnClass(CacheManager.class)
    public JetCacheCreateCacheFactory jetCacheCreateCacheFactory(CacheManager jcCacheManager) {
        JetCacheCreateCacheFactory factory = new JetCacheCreateCacheFactory(jcCacheManager);
        JetCacheUtils.setJetCacheCreateCacheFactory(factory);
        LogUtils.trace("[kmc] |- Bean [JetCacheCreateCacheFactory] Auto Configure.");
        return factory;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public HerodotusCacheManager herodotusCacheManager(
            JetCacheCreateCacheFactory jetCacheCreateCacheFactory, JetCacheProperties cacheProperties) {
        HerodotusCacheManager manager = new HerodotusCacheManager(jetCacheCreateCacheFactory, cacheProperties);
        LogUtils.trace("[kmc] |- Bean [HerodotusCacheManager] Auto Configure.");
        return manager;
    }
}
