package com.kuma.cloud.lab.spring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring 实验模块配置。
 */
@Data
@ConfigurationProperties(prefix = "kuma.lab.spring")
public class SpringLabProperties {

    /**
     * 事件演示默认用户名。
     */
    private String demoUsername = "kuma";

    /**
     * 分层架构演示默认订单号。
     */
    private String demoOrderId = "ORD-2026-001";

}
