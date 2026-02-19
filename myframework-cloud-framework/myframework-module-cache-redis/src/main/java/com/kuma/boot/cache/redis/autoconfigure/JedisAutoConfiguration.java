/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.PostConstruct
 *  org.apache.commons.pool2.impl.GenericObjectPoolConfig
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.data.redis.autoconfigure.DataRedisProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.data.redis.connection.RedisStandaloneConfiguration
 *  org.springframework.data.redis.connection.jedis.JedisClientConfiguration
 *  org.springframework.data.redis.connection.jedis.JedisClientConfiguration$JedisClientConfigurationBuilder
 *  org.springframework.data.redis.connection.jedis.JedisConnectionFactory
 *  redis.clients.jedis.ConnectionPoolConfig
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.RedisClient
 */
package com.kuma.boot.cache.redis.autoconfigure;

import jakarta.annotation.PostConstruct;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.RedisClient;

@AutoConfiguration
@ConditionalOnClass(value={Jedis.class})
public class JedisAutoConfiguration {
    private final DataRedisProperties redisProperties;

    public JedisAutoConfiguration(DataRedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        ConnectionPoolConfig jedisPoolConfig = new ConnectionPoolConfig();
        jedisPoolConfig.setMaxTotal(200);
        jedisPoolConfig.setMaxIdle(150);
        jedisPoolConfig.setMinIdle(50);
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(this.redisProperties.getHost());
        redisStandaloneConfiguration.setPort(this.redisProperties.getPort());
        redisStandaloneConfiguration.setPassword(this.redisProperties.getPassword());
        redisStandaloneConfiguration.setDatabase(this.redisProperties.getDatabase());
        JedisClientConfiguration.JedisClientConfigurationBuilder configurationBuilder = JedisClientConfiguration.builder();
        JedisClientConfiguration jedisClientConfiguration = configurationBuilder.usePooling().poolConfig((GenericObjectPoolConfig)jedisPoolConfig).build();
        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
    }

    public static class JedisMonitor {
        private final RedisClient jedisPool;

        public JedisMonitor(RedisClient jedisPool) {
            this.jedisPool = jedisPool;
        }

        @PostConstruct
        public void startMonitoring() {
        }
    }
}

