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

package com.kuma.boot.cache.redis.autoconfigure;

import com.kuma.boot.cache.redis.redisson.RedisDelayQueue;
import com.kuma.boot.cache.redis.redisson.RedisDelayQueueRunner;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * redis 延迟队列自动配置类
 *
 * @author kuma
 * @version 2022.03
 * @since 2022/01/29 15:57
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBean(RedissonClient.class)
public class RedisDelayQueueAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(RedisDelayQueueAutoConfiguration.class, StarterNameConstants.CACHE_REDIS_STARTER);
    }

    @Bean
    public RedisDelayQueue redisDelayQueue(RedissonClient redissonClient) {
        return new RedisDelayQueue(redissonClient);
    }

    @Bean
    public RedisDelayQueueRunner redisDelayQueueRunner(RedisDelayQueue redisDelayQueue) {
        return new RedisDelayQueueRunner(redisDelayQueue);
    }
}
