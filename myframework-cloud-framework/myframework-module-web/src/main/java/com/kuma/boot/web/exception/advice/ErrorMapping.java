package com.kuma.boot.web.exception.advice;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorMapping {

    /**
     * 异常类型
     */
    Class<? extends Exception> exception();

    /**
     * 错误码
     */
    String code();

    /**
     * 错误消息
     */
    String message();

    /**
     * HTTP状态码
     */
    int httpStatus() default 500;
}
