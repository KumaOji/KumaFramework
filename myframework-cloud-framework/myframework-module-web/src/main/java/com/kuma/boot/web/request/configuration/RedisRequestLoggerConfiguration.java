/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.web.request.configuration;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.enums.RequestLoggerTypeEnum;
import com.kuma.boot.web.request.properties.RequestLoggerProperties;
import com.kuma.boot.web.request.service.RequestLoggerService;
import com.kuma.boot.web.request.service.impl.RedisRequestLoggerServiceImpl;
import java.util.Arrays;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(value={RedisRepository.class})
@ConditionalOnBean(value={RedisRepository.class})
@ConditionalOnProperty(prefix="kuma.boot.web.request", name={"enabled"}, havingValue="true")
public class RedisRequestLoggerConfiguration
implements InitializingBean {
    @Autowired
    private RequestLoggerProperties requestLoggerProperties;

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedisRequestLoggerConfiguration.class, (String)"kuma-boot-starter-logger", (String[])new String[0]);
    }

    @Bean
    public RequestLoggerService redisRequestLoggerService(RedisRepository redisRepository) {
        if (Arrays.stream(this.requestLoggerProperties.getTypes()).anyMatch(e -> e.name().equals(RequestLoggerTypeEnum.REDIS.name()))) {
            return new RedisRequestLoggerServiceImpl(redisRepository);
        }
        return null;
    }
}

