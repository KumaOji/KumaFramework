package com.kuma.cloud.reference.config;

import com.kuma.boot.cache.redis.autoconfigure.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration(RedisAutoConfiguration.class)
public class RedisConfiguration {}
