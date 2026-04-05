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

package com.kuma.boot.ratelimit.ratelimitprovider;

import com.kuma.boot.cache.redis.autoconfigure.RedisAutoConfiguration;
import com.kuma.boot.ratelimit.ratelimitaspect.LimitProperties;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 速度限制器自动配置
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-10-26 08:56:50
 */
@EnableConfigurationProperties({LimitProperties.class})
@ConditionalOnProperty(prefix = LimitProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(RedisAutoConfiguration.class)
@AutoConfiguration(after = RedisAutoConfiguration.class)
public class RateLimiterAutoConfiguration {

    @Bean
    public com.kuma.boot.ratelimit.ratelimitprovider.BizKeyProvider bizKeyProvider() {
        return new com.kuma.boot.ratelimit.ratelimitprovider.BizKeyProvider();
    }

    @Bean
    public RateLimiterService rateLimiterService(com.kuma.boot.ratelimit.ratelimitprovider.BizKeyProvider bizKeyProvider) {
        return new RateLimiterService(bizKeyProvider);
    }

    @Configuration
    @ConditionalOnClass(RedissonClient.class)
    @ConditionalOnProperty(prefix = LimitProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public static class RateLimitAspectHandlerConfiguration {

        @Bean
        public com.kuma.boot.ratelimit.ratelimitprovider.RateLimitAspectHandler rateLimitAspectHandler(RedissonClient client, RateLimiterService rateLimiterService) {
            return new com.kuma.boot.ratelimit.ratelimitprovider.RateLimitAspectHandler(client, rateLimiterService);
        }
    }


}
