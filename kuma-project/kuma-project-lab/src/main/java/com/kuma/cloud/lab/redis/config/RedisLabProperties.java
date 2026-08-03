package com.kuma.cloud.lab.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 实验模块配置。
 */
@Data
@ConfigurationProperties(prefix = "kuma.lab.redis")
public class RedisLabProperties {

    /**
     * 实验 key 前缀，避免污染业务数据。
     */
    private String keyPrefix = "lab:redis:";

}
