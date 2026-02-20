package com.kuma.boot.web.request.altas.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Atlas Log 敏感数据配置注解
 *
 * @author nemoob
 * @since 0.2.0
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AtlasLogSensitive {

    /**
     * 是否启用敏感数据脱敏
     */
    boolean enabled() default true;

    /**
     * 自定义敏感字段
     */
    String[] customFields() default {"bankCard", "idCard", "socialSecurityNumber"};

    /**
     * 脱敏标记
     */
    String maskValue() default "***";
}
