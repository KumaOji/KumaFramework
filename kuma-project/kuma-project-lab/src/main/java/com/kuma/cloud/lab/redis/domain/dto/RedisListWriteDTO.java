package com.kuma.cloud.lab.redis.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Redis List 写入参数。
 */
@Data
public class RedisListWriteDTO {

    @NotBlank(message = "key 不能为空")
    private String key;

    /**
     * true 表示 LPUSH，false 表示 RPUSH。
     */
    private boolean leftPush = true;

    @NotEmpty(message = "values 不能为空")
    private List<String> values;

}
