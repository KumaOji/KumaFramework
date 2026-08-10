package com.kuma.cloud.lab.jni.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * JNI 二元运算参数。
 */
@Data
public class JniBinaryOpDTO {

    @NotNull(message = "left 不能为空")
    private Integer left;

    @NotNull(message = "right 不能为空")
    private Integer right;

}
