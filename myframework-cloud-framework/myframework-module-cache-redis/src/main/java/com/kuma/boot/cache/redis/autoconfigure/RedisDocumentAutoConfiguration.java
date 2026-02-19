/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.context.annotation.Bean
 *  org.springframework.data.redis.core.mapping.RedisMappingContext
 */
package com.kuma.boot.cache.redis.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.mapping.RedisMappingContext;

@ConditionalOnClass(value={RedisMappingContext.class})
@AutoConfiguration
public class RedisDocumentAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedisDocumentAutoConfiguration.class, (String)"kuma-boot-starter-cache-redis", (String[])new String[0]);
    }

    @Bean
    public RedisMappingContext keyValueMappingContext() {
        return new RedisMappingContext();
    }
}

