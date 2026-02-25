/*
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.api.RedissonClient
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Bean
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 *  org.springframework.data.redis.core.StringRedisTemplate
 *  org.springframework.data.redis.core.script.DefaultRedisScript
 *  org.springframework.data.redis.core.script.RedisScript
 *  org.springframework.scripting.ScriptSource
 *  org.springframework.scripting.support.ResourceScriptSource
 */
package com.kuma.boot.ratelimit.ratelimitredis;

import com.kuma.boot.common.utils.log.LogUtils;

import java.util.List;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

@AutoConfiguration
@ConditionalOnBean(value={RedissonClient.class})
@ConditionalOnProperty(prefix="kuma.boot.ratelimit.enable", value={"true"}, matchIfMissing=true)
public class RedisRateLimiterAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RedisRateLimiterAutoConfiguration.class, (String)"kuma-boot-starter-cache-redis", (String[])new String[0]);
    }

    private RedisScript<List<Long>> redisRateLimiterScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript();
        redisScript.setScriptSource((ScriptSource)new ResourceScriptSource((Resource)new ClassPathResource("META-INF/scripts/kuma_rate_limiter.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisRateLimiterClient redisRateLimiterClient(StringRedisTemplate redisTemplate, Environment environment) {
        RedisScript<List<Long>> redisRateLimiterScript = this.redisRateLimiterScript();
        return new RedisRateLimiterClient(redisTemplate, redisRateLimiterScript, environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisRateLimiterAspect redisRateLimiterAspect(RedisRateLimiterClient rateLimiterClient) {
        return new RedisRateLimiterAspect(rateLimiterClient);
    }
}

