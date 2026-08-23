package com.kuma.cloud.lab.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Starter 实验模块配置。
 */
@Data
@ConfigurationProperties(prefix = "kuma.lab.starter")
public class StarterLabProperties {

    /**
     * 冒烟测试写入 Redis / 向量库等外部资源时使用的 key 前缀。
     */
    private String keyPrefix = "lab:starter:";

    /**
     * 是否在场景测试中执行会触碰外部依赖的冒烟测试。
     */
    private boolean smokeTestEnabled = true;

}
