package com.kuma.cloud.lab.redis.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Redis Hash 写入参数。
 */
@Data
public class RedisHashWriteDTO {

    @NotBlank(message = "key 不能为空")
    private String key;

    @NotBlank(message = "field 不能为空")
    private String field;

    @NotBlank(message = "value 不能为空")
    private String value;

}
