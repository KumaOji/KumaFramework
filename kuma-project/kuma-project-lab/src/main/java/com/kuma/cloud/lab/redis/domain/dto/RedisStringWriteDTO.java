package com.kuma.cloud.lab.redis.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Redis String 写入参数。
 */
@Data
public class RedisStringWriteDTO {

    @NotBlank(message = "key 不能为空")
    private String key;

    @NotBlank(message = "value 不能为空")
    private String value;

    /**
     * 过期时间（秒），为空表示不过期。
     */
    private Long ttlSeconds;

}
