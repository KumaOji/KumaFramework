package com.kuma.cloud.lab.bloom.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 布隆过滤器实验模块配置。
 */
@Data
@ConfigurationProperties(prefix = "kuma.lab.bloom")
public class BloomLabProperties {

    /**
     * 预期插入元素数量，用于计算位数组大小。
     */
    private int expectedInsertions = 10_000;

    /**
     * 可接受的误判率。
     */
    private double falsePositiveProbability = 0.01;

}
