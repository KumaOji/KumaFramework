package com.kuma.boot.web.exception.advice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorResponseBody {

    /**
     * 是否记录堆栈跟踪
     */
    boolean logStackTrace() default true;

    /**
     * 日志级别
     */
    Level logLevel() default Level.ERROR;

    public static enum Level {DEBUG, INFO, WARN, ERROR}
}
