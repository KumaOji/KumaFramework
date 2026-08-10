package com.kuma.cloud.lab.jni.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * JNI 字符串问候参数。
 */
@Data
public class JniGreetDTO {

    @NotBlank(message = "name 不能为空")
    private String name;

}
