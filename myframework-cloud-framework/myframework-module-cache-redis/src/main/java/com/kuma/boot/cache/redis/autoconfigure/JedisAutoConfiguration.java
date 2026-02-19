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

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.exceptions.JedisConnectionException;

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

    public JedisAutoConfiguration( DataRedisProperties redisProperties ) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        ConnectionPoolConfig jedisPoolConfig = new ConnectionPoolConfig();
        jedisPoolConfig.setMaxTotal(200);
        jedisPoolConfig.setMaxIdle(150);
        jedisPoolConfig.setMinIdle(50);

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());

        JedisClientConfiguration.JedisClientConfigurationBuilder configurationBuilder =
                JedisClientConfiguration.builder();
        JedisClientConfiguration jedisClientConfiguration =
                configurationBuilder.usePooling().poolConfig(jedisPoolConfig).build();

        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
    }

    //通过这种方式，我们就可以实现对Redis命令来进行实时的监控操作，
    // 当Redis执行命令的时候，就会在控制台中看到对应的命令输出。
    //@Component

    /**
     * JedisMonitor
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class JedisMonitor {

        private final RedisClient jedisPool;

        public JedisMonitor( RedisClient jedisPool ) {
            this.jedisPool = jedisPool;
        }

        @PostConstruct
        public void startMonitoring() {
//			new Thread(() -> {
//				try (Jedis jedis = jedisPool.getResource()) {
//					jedis.monitor(new redis.clients.jedis.JedisMonitor() {
//						@Override
//						public void onCommand( String response ) {
//							System.out.println("Received command: " + response);
//						}
//					});
//				} catch (JedisConnectionException e) {
//					LogUtils.error(e);
//				}
//			}).start();
        }
    }
}
