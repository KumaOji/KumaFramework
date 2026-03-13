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

import java.util.Objects;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.Jedis;

/**
 * jedis自动配置
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-08-08 13:36:40
 */
@AutoConfiguration
@ConditionalOnClass(Jedis.class)
public class JedisAutoConfiguration {

    private final DataRedisProperties redisProperties;

    public JedisAutoConfiguration(DataRedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public JedisConnectionFactory redisConnectionFactory() {
        ConnectionPoolConfig jedisPoolConfig = buildPoolConfig();

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());

        JedisClientConfiguration jedisClientConfiguration =
                JedisClientConfiguration.builder().usePooling().poolConfig(jedisPoolConfig).build();

        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
    }

    private ConnectionPoolConfig buildPoolConfig() {
        ConnectionPoolConfig config = new ConnectionPoolConfig();
        DataRedisProperties.Jedis jedis = redisProperties.getJedis();
        DataRedisProperties.Pool pool = (jedis != null) ? jedis.getPool() : null;
        if (pool != null) {
            config.setMaxTotal(Objects.requireNonNullElse(pool.getMaxActive(), 200));
            config.setMaxIdle(Objects.requireNonNullElse(pool.getMaxIdle(), 150));
            config.setMinIdle(Objects.requireNonNullElse(pool.getMinIdle(), 50));
            if (pool.getMaxWait() != null && !pool.getMaxWait().isNegative()) {
                config.setMaxWait(pool.getMaxWait());
            }
        } else {
            config.setMaxTotal(200);
            config.setMaxIdle(150);
            config.setMinIdle(50);
        }
        return config;
    }
}
