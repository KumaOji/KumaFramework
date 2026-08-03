package com.kuma.cloud.lab.redis.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Redis Set 写入参数。
 */
@Data
public class RedisSetWriteDTO {

    @NotBlank(message = "key 不能为空")
    private String key;

    @NotEmpty(message = "members 不能为空")
    private List<String> members;

}
