package com.kuma.cloud.lab.snowflake.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 雪花算法实验模块配置。
 */
@Data
@ConfigurationProperties(prefix = "kuma.lab.snowflake")
public class SnowflakeLabProperties {

    /**
     * 工作机器 ID（0-31）。
     */
    private long workerId = 1;

    /**
     * 数据中心 ID（0-31）。
     */
    private long datacenterId = 1;

    /**
     * 场景测试批量生成数量。
     */
    private int batchSize = 10;

}
