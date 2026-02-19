/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface JsonPathCompare {
    public String[] value() default {};

    public CompareMode mode() default CompareMode.ARGS_VS_RESULT;

    public boolean logComparison() default true;

    public LogLevel logLevel() default LogLevel.INFO;

    public boolean logDifferences() default true;

    public String messageTemplate() default "JsonPath\u6bd4\u8f83: {path} | \u53c2\u6570\u503c: {value1} | \u8fd4\u56de\u503c: {value2} | \u76f8\u7b49: {equal}";

    public FailureStrategy onFailure() default FailureStrategy.LOG_WARNING;

    public static enum FailureStrategy {
        LOG_WARNING,
        LOG_ERROR,
        IGNORE,
        THROW_EXCEPTION;

    }

    public static enum CompareMode {
        ARGS_VS_RESULT,
        BEFORE_VS_AFTER,
        EXTRACT_ONLY;

    }
}

