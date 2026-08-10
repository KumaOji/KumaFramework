package com.kuma.cloud.lab.jni.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JNI 实验模块配置。
 */
@Data
@ConfigurationProperties(prefix = "kuma.lab.jni")
public class JniLabProperties {

    /**
     * 是否启用 JNI 测试接口。
     */
    private boolean enabled = true;

    /**
     * JNI 动态库基础名，不含平台后缀。
     */
    private String libraryName = "lab_math";

}
