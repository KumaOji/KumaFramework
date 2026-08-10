package com.kuma.cloud.lab.jni.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * JNI 数组求和参数。
 */
@Data
public class JniSumDTO {

    @NotEmpty(message = "values 不能为空")
    private List<Integer> values;

}
