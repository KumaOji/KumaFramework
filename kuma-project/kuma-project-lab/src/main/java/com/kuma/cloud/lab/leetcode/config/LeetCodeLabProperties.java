package com.kuma.cloud.lab.leetcode.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LeetCode 实验模块配置。
 */
@Data
@ConfigurationProperties(prefix = "kuma.lab.leetcode")
public class LeetCodeLabProperties {

    /**
     * 是否启用 LeetCode 实验接口。
     */
    private boolean enabled = true;

}
