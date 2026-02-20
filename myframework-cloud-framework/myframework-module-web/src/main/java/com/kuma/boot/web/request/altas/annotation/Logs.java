package com.kuma.boot.web.request.altas.annotation;

import java.lang.annotation.*;

/**
 * 多个日志注解的容器
 * 当在同一个方法或类上使用多个@Log注解时，会自动使用此容器
 *
 * @author nemoob
 * @since 0.2.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Logs {

    /**
     * 日志注解数组
     *
     * @return Log注解数组
     */
    com.kuma.boot.web.request.altas.annotation.Log[] value();
}
