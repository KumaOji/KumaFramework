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

package com.kuma.boot.ratelimit.ratelimitredis;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

/**
 * redis x限流自动配置类
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-03 09:24:19
 */
@AutoConfiguration
@ConditionalOnBean(RedissonClient.class)
@ConditionalOnProperty(prefix = "kuma.boot.ratelimit.enable", value = "true", matchIfMissing = true)
public class RedisRateLimiterAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedisRateLimiterAutoConfiguration.class, StarterNameConstants.CACHE_REDIS_STARTER);
    }

    @SuppressWarnings("unchecked")
    private RedisScript<List<Long>> redisRateLimiterScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("META-INF/scripts/kuma_rate_limiter.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisRateLimiterClient redisRateLimiterClient(StringRedisTemplate redisTemplate, Environment environment) {
        RedisScript<List<Long>> redisRateLimiterScript = redisRateLimiterScript();
        return new RedisRateLimiterClient(redisTemplate, redisRateLimiterScript, environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public com.kuma.boot.ratelimit.ratelimitredis.RedisRateLimiterAspect redisRateLimiterAspect(RedisRateLimiterClient rateLimiterClient) {
        return new com.kuma.boot.ratelimit.ratelimitredis.RedisRateLimiterAspect(rateLimiterClient);
    }
}
