/*
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 */
package com.kuma.boot.ratelimit.ratelimitenhance.config;

import com.kuma.boot.ratelimit.ratelimitenhance.helper.RedisHelper;
import com.kuma.boot.ratelimit.ratelimitenhance.helper.RedisLimitHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@ConditionalOnClass(value={RedisConnectionFactory.class})
public class RedisLimitingAutoConfiguration {
    @Bean(value={"redisHelper"})
    public RedisHelper redisHelper() {
        return new RedisHelper();
    }

    @Bean(value={"redisLimitHelper"})
    public RedisLimitHelper redisLimitHelper(@Qualifier(value="redisHelper") RedisHelper redisHelper) {
        return new RedisLimitHelper(redisHelper);
    }
}

